package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESNode
{
	private SecretKeySpec keySpec;
	private ByteDataReciever byteIn;
	private ByteDataSender byteOut;

	public AESNode(InputStream in, OutputStream out, AESKeyNode keyNode) throws IOException
	{
		initAESServices();
		
		byte[] key = keyNode.getEncodedKey();
		keyNode.clear();
		
		this.byteIn = new ByteDataReciever(in);
		this.byteOut = new ByteDataSender(out);

		keySpec = new SecretKeySpec(key, "AES");
	}

	public byte[] encrypt(byte[] iv, byte[] data)
	{
		try
		{
			Cipher c = getCipher();
			c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return c.doFinal(data);
		}
		catch(GeneralSecurityException e)
		{
			throw new AssertionError(e);
		}
	}

	public byte[] encryptAndWrite(byte[] data) throws IOException
	{
		SecureRandom rand = new SecureRandom();
		byte[] iv = new byte[16];
		rand.nextBytes(iv);

		byte[] enc = encrypt(iv, data);

		byteOut.writeBytes(iv);
		byteOut.writeBytes(enc);
		byteOut.flush();
		return enc;
	}

	public byte[] decrypt(byte[] iv, byte[] enc)
	{
		try
		{
			Cipher c = getCipher();
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return c.doFinal(enc);
		}
		catch(GeneralSecurityException e)
		{
			throw new AssertionError(e);
		}
	}

	public byte[] readAndDecrypt() throws IOException
	{
		byte[] iv = byteIn.readBytes();
		byte[] enc = byteIn.readBytes();
		return decrypt(iv, enc);
	}

	private static boolean servicesInitialized = false;
	private static Cipher cipher;

	private static void initAESServices()
	{
		if(!servicesInitialized)
		{
			try
			{
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				System.out.println(cipher.getProvider().getClass());
				servicesInitialized = true;
			}
			catch(Exception e)
			{
				throw new ExceptionInInitializerError(e);
			}
		}
	}
	
	private static Cipher getCipher()
	{
		return cipher;
	}
}
