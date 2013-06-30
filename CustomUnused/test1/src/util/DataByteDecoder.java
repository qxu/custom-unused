package util;

import static util.DataByteHeader.BIG_INTEGER_HEADER;
import static util.DataByteHeader.BYTE_ARRAY_HEADER;
import static util.DataByteHeader.BYTE_HEADER;
import static util.DataByteHeader.DOUBLE_HEADER;
import static util.DataByteHeader.FLOAT_HEADER;
import static util.DataByteHeader.INT_HEADER;
import static util.DataByteHeader.LONG_HEADER;
import static util.DataByteHeader.SHORT_HEADER;
import static util.DataByteHeader.STRING_HEADER;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class DataByteDecoder implements Closeable, AutoCloseable
{
	private byte[] data;
	private int pos;
	
	public DataByteDecoder(byte[] data)
	{
		this.data = data.clone();
		this.pos = 0;
	}
	
	public byte readByte()
	{
		readDataHeader(BYTE_HEADER);
		return (byte)readRaw();
	}
	
	public short readShort()
	{
		readDataHeader(SHORT_HEADER);
		int b1 = readRaw() & 0xff;
		int b2 = readRaw() & 0xff;
		return (short)((b1 << 8) + b2);
	}
	
	public int readInt()
	{
		readDataHeader(INT_HEADER);
        int b1 = readRaw() & 0xff;
        int b2 = readRaw() & 0xff;
        int b3 = readRaw() & 0xff;
        int b4 = readRaw() & 0xff;
        return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
	}
	
	public long readLong()
	{
		readDataHeader(LONG_HEADER);
		byte[] b = readRaw(8);
		return ((b[0] & 0xff) << 56) + ((b[1] & 0xff) << 48)
				+ ((b[2] & 0xff) << 40) + ((b[3] & 0xff) << 32)
				+ ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
				+ ((b[6] & 0xff) << 8) + (b[7] & 0xff);
	}

	public float readFloat()
	{
		readDataHeader(FLOAT_HEADER);
        int b1 = readRaw() & 0xff;
        int b2 = readRaw() & 0xff;
        int b3 = readRaw() & 0xff;
        int b4 = readRaw() & 0xff;
        int bits =  (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
        return Float.intBitsToFloat(bits);
	}
	
	public double readDouble()
	{
		readDataHeader(DOUBLE_HEADER);
		byte[] b = readRaw(8);
		long bits = ((b[0] & 0xff) << 56) + ((b[1] & 0xff) << 48)
				+ ((b[2] & 0xff) << 40) + ((b[3] & 0xff) << 32)
				+ ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
				+ ((b[6] & 0xff) << 8) + (b[7] & 0xff);
		return Double.longBitsToDouble(bits);
	}

	public byte[] readData()
	{
		readDataHeader(BYTE_ARRAY_HEADER);
		int len = readLength();
		return readRaw(len);
	}
	
	public BigInteger readBigInteger()
	{
		readDataHeader(BIG_INTEGER_HEADER);
		int len = readLength();
		byte[] b = readRaw(len);
		return new BigInteger(b);
	}
	
	public String readUTF()
	{
		readDataHeader(STRING_HEADER);
		int len = readLength();
		byte[] b = readRaw(len);
		try
		{
			return new String(b, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e); 
		}
	}
	
	public void close()
	{
		final int len = data.length;
		for(int i = 0; i < len; ++i)
		{
			data[i] = 0;
		}
		data = null;
		pos = -1;
	}
	
	private void readDataHeader(int type)
	{
		int raw = readRaw();
		if(raw != type)
			throw new RuntimeException("Wrong type read");
	}
	
	private int readLength()
	{
        int b1 = readRaw() & 0xff;
        int b2 = readRaw() & 0xff;
        int b3 = readRaw() & 0xff;
        int b4 = readRaw() & 0xff;
        return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
	}
	
	private int readRaw()
	{
		return data[pos++] & 0xff;
	}
	
	private byte[] readRaw(int len)
	{
		if(pos + len > data.length)
			throw new RuntimeException("Reached end of data");
		byte[] b = new byte[len];
		System.arraycopy(data, pos, b, 0, len);
		pos += len;
		return b;
	}
}
