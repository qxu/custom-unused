package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

/**
 * Implements the Counter with Cipher Block Chaining mode (CCM) detailed in
 * NIST Special Publication 800-38C.
 * <p>
 * <b>Note</b>: this mode is a packet mode - it needs all the data up front.
 */
public class CCMBlockCipher
    implements AEADBlockCipher
{
    private BlockCipher           cipher;
    private int                   blockSize;
    private boolean               forEncryption;
    private byte[]                nonce;
    private byte[]                initialAssociatedText;
    private int                   macSize;
    private CipherParameters      keyParam;
    private byte[]                macBlock;
    private ByteArrayOutputStream associatedText = new ByteArrayOutputStream();
    private ByteArrayOutputStream data = new ByteArrayOutputStream();

    /**
     * Basic constructor.
     *
     * @param c the block cipher to be used.
     */
    public CCMBlockCipher(BlockCipher c)
    {
        this.cipher = c;
        this.blockSize = c.getBlockSize();
        this.macBlock = new byte[blockSize];
        
        if (blockSize != 16)
        {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
    }

    /**
     * return the underlying block cipher that we are wrapping.
     *
     * @return the underlying block cipher that we are wrapping.
     */
    public BlockCipher getUnderlyingCipher()
    {
        return cipher;
    }


    public void init(boolean forEncryption, CipherParameters params)
          throws IllegalArgumentException
    {
        this.forEncryption = forEncryption;

        if (params instanceof AEADParameters)
        {
            AEADParameters param = (AEADParameters)params;

            nonce = param.getNonce();
            initialAssociatedText = param.getAssociatedText();
            macSize = param.getMacSize() / 8;
            keyParam = param.getKey();
        }
        else if (params instanceof ParametersWithIV)
        {
            ParametersWithIV param = (ParametersWithIV)params;

            nonce = param.getIV();
            initialAssociatedText = null;
            macSize = macBlock.length / 2;
            keyParam = param.getParameters();
        }
        else
        {
            throw new IllegalArgumentException("invalid parameters passed to CCM");
        }

        if (nonce == null || nonce.length < 7 || nonce.length > 13)
        {
            throw new IllegalArgumentException("nonce must have length from 7 to 13 octets");
        }
    }

    public String getAlgorithmName()
    {
        return cipher.getAlgorithmName() + "/CCM";
    }

    public void processAADByte(byte in)
    {
        associatedText.write(in);
    }

    public void processAADBytes(byte[] in, int inOff, int len)
    {
        // TODO: Process AAD online
        associatedText.write(in, inOff, len);
    }

    public int processByte(byte in, byte[] out, int outOff)
        throws DataLengthException, IllegalStateException
    {
        data.write(in);

        return 0;
    }

    public int processBytes(byte[] in, int inOff, int inLen, byte[] out, int outOff)
        throws DataLengthException, IllegalStateException
    {
        data.write(in, inOff, inLen);

        return 0;
    }

    public int doFinal(byte[] out, int outOff)
        throws IllegalStateException, InvalidCipherTextException
    {
        byte[] text = data.toByteArray();
        byte[] enc = processPacket(text, 0, text.length);

        System.arraycopy(enc, 0, out, outOff, enc.length);

        reset();

        return enc.length;
    }

    public void reset()
    {
        cipher.reset();
        associatedText.reset();
        data.reset();
    }

    /**
     * Returns a byte array containing the mac calculated as part of the
     * last encrypt or decrypt operation.
     * 
     * @return the last mac calculated.
     */
    public byte[] getMac()
    {
        byte[] mac = new byte[macSize];
        
        System.arraycopy(macBlock, 0, mac, 0, mac.length);
        
        return mac;
    }

    public int getUpdateOutputSize(int len)
    {
        return 0;
    }

    public int getOutputSize(int len)
    {
        int totalData = len + data.size();

        if (forEncryption)
        {
             return totalData + macSize;
        }

        return totalData < macSize ? 0 : totalData - macSize;
    }

    public byte[] processPacket(byte[] in, int inOff, int inLen)
        throws IllegalStateException, InvalidCipherTextException
    {
        // TODO: handle null keyParam (e.g. via RepeatedKeySpec)
        // Need to keep the CTR and CBC Mac parts around and reset
        if (keyParam == null)
        {
            throw new IllegalStateException("CCM cipher unitialized.");
        }

        int n = nonce.length;
        int q = 15 - n;
        if (q < 4)
        {
            int limitLen = 1 << (8 * q);
            if (inLen >= limitLen)
            {
                throw new IllegalStateException("CCM packet too large for choice of q.");
            }
        }

        byte[] iv = new byte[blockSize];
        iv[0] = (byte)((q - 1) & 0x7);
        System.arraycopy(nonce, 0, iv, 1, nonce.length);

        BlockCipher ctrCipher = new SICBlockCipher(cipher);
        ctrCipher.init(forEncryption, new ParametersWithIV(keyParam, iv));

        int index = inOff;
        int outOff = 0;
        byte[] output;

        if (forEncryption)
        {
            output = new byte[inLen + macSize];

            calculateMac(in, inOff, inLen, macBlock);

            ctrCipher.processBlock(macBlock, 0, macBlock, 0);   // S0

            while (index < inLen - blockSize)                   // S1...
            {
                ctrCipher.processBlock(in, index, output, outOff);
                outOff += blockSize;
                index += blockSize;
            }

            byte[] block = new byte[blockSize];

            System.arraycopy(in, index, block, 0, inLen - index);

            ctrCipher.processBlock(block, 0, block, 0);

            System.arraycopy(block, 0, output, outOff, inLen - index);

            outOff += inLen - index;

            System.arraycopy(macBlock, 0, output, outOff, output.length - outOff);
        }
        else
        {
            output = new byte[inLen - macSize];

            System.arraycopy(in, inOff + inLen - macSize, macBlock, 0, macSize);

            ctrCipher.processBlock(macBlock, 0, macBlock, 0);

            for (int i = macSize; i != macBlock.length; i++)
            {
                macBlock[i] = 0;
            }

            while (outOff < output.length - blockSize)
            {
                ctrCipher.processBlock(in, index, output, outOff);
                outOff += blockSize;
                index += blockSize;
            }

            byte[] block = new byte[blockSize];

            System.arraycopy(in, index, block, 0, output.length - outOff);

            ctrCipher.processBlock(block, 0, block, 0);

            System.arraycopy(block, 0, output, outOff, output.length - outOff);

            byte[] calculatedMacBlock = new byte[blockSize];

            calculateMac(output, 0, output.length, calculatedMacBlock);

            if (!Arrays.constantTimeAreEqual(macBlock, calculatedMacBlock))
            {
                throw new InvalidCipherTextException("mac check in CCM failed");
            }
        }

        return output;
    }

    private int calculateMac(byte[] data, int dataOff, int dataLen, byte[] macBlock)
    {
        Mac cMac = new CBCBlockCipherMac(cipher, macSize * 8);

        cMac.init(keyParam);

        //
        // build b0
        //
        byte[] b0 = new byte[16];
    
        if (hasAssociatedText())
        {
            b0[0] |= 0x40;
        }
        
        b0[0] |= (((cMac.getMacSize() - 2) / 2) & 0x7) << 3;

        b0[0] |= ((15 - nonce.length) - 1) & 0x7;
        
        System.arraycopy(nonce, 0, b0, 1, nonce.length);
        
        int q = dataLen;
        int count = 1;
        while (q > 0)
        {
            b0[b0.length - count] = (byte)(q & 0xff);
            q >>>= 8;
            count++;
        }
        
        cMac.update(b0, 0, b0.length);
        
        //
        // process associated text
        //
        if (hasAssociatedText())
        {
            int extra;
            
            int textLength = getAssociatedTextLength();
            if (textLength < ((1 << 16) - (1 << 8)))
            {
                cMac.update((byte)(textLength >> 8));
                cMac.update((byte)textLength);
                
                extra = 2;
            }
            else // can't go any higher than 2^32
            {
                cMac.update((byte)0xff);
                cMac.update((byte)0xfe);
                cMac.update((byte)(textLength >> 24));
                cMac.update((byte)(textLength >> 16));
                cMac.update((byte)(textLength >> 8));
                cMac.update((byte)textLength);
                
                extra = 6;
            }

            if (initialAssociatedText != null)
            {
                cMac.update(initialAssociatedText, 0, initialAssociatedText.length);
            }
            if (associatedText.size() > 0)
            {
                byte[] tmp = associatedText.toByteArray();
                cMac.update(tmp, 0, tmp.length);
            }

            extra = (extra + textLength) % 16;
            if (extra != 0)
            {
                for (int i = extra; i != 16; i++)
                {
                    cMac.update((byte)0x00);
                }
            }
        }
 
        //
        // add the text
        //
        cMac.update(data, dataOff, dataLen);

        return cMac.doFinal(macBlock, 0);
    }

    private int getAssociatedTextLength()
    {
        return associatedText.size() + ((initialAssociatedText == null) ? 0 : initialAssociatedText.length);
    }

    private boolean hasAssociatedText()
    {
        return getAssociatedTextLength() > 0;
    }
}
