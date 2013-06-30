package util;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import static util.DataByteHeader.*;

public class DataByteEncoder implements Closeable, AutoCloseable
{
	private byte[] data;
	private int pos;

	public DataByteEncoder()
	{
		this(32);
	}
	
	public DataByteEncoder(int initialCapacity)
	{
		this.data = new byte[initialCapacity];
		this.pos = 0;
	}
	
	public void writeByte(byte n)
	{
		writeDataHeader(BYTE_HEADER);
		writeRaw(n);
	}
	
	public void writeShort(short n)
	{
		writeDataHeader(SHORT_HEADER);
		writeRaw((byte)(n >>> 8));
		writeRaw((byte)n);
	}
	
	public void writeInt(int n)
	{
		writeDataHeader(INT_HEADER);
		writeRaw((byte)(n >>> 24));
		writeRaw((byte)(n >>> 16));
		writeRaw((byte)(n >>> 8));
		writeRaw((byte)n);
	}
	
	public void writeLong(long n)
	{
		writeDataHeader(LONG_HEADER);
		writeRaw((byte)(n >>> 56));
		writeRaw((byte)(n >>> 48));
		writeRaw((byte)(n >>> 40));
		writeRaw((byte)(n >>> 32));
		writeRaw((byte)(n >>> 24));
		writeRaw((byte)(n >>> 16));
		writeRaw((byte)(n >>> 8));
		writeRaw((byte)n);
	}

	public void writeFloat(float n)
	{
		writeDataHeader(FLOAT_HEADER);
		int bits = Float.floatToIntBits(n);
		writeRaw((byte)(bits >>> 24));
		writeRaw((byte)(bits >>> 16));
		writeRaw((byte)(bits >>> 8));
		writeRaw((byte)bits);
	}
	
	public void writeDouble(double n)
	{
		writeDataHeader(DOUBLE_HEADER);
		long bits = Double.doubleToLongBits(n);
		writeRaw((byte)(bits >>> 56));
		writeRaw((byte)(bits >>> 48));
		writeRaw((byte)(bits >>> 40));
		writeRaw((byte)(bits >>> 32));
		writeRaw((byte)(bits >>> 24));
		writeRaw((byte)(bits >>> 16));
		writeRaw((byte)(bits >>> 8));
		writeRaw((byte)bits);
	}
	
	public void writeData(byte[] b)
	{
		writeData(b, 0, b.length);
	}
	
	public void writeData(byte[] b, int off, int len)
	{
		if(off < 0 || off + len > b.length)
			throw new IndexOutOfBoundsException();
		writeDataHeader(BYTE_ARRAY_HEADER);
		writeLength(b.length);
		writeRaw(b, off, len);
	}
	
	public void writeBigInteger(BigInteger n)
	{
		byte[] b = n.toByteArray();
		writeDataHeader(BIG_INTEGER_HEADER);
		writeLength(b.length);
		writeRaw(b, 0, b.length);
	}
	
	public void writeUTF(String s)
	{
		byte[] b;
		try
		{
			b = s.getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e); 
		}
		writeDataHeader(STRING_HEADER);
		writeLength(b.length);
		writeRaw(b, 0, b.length);
	}
	
	public int getDataLength()
	{
		return this.pos;
	}

	public byte[] getBytes()
	{
		byte[] bytes = new byte[pos];
		System.arraycopy(data, 0, bytes, 0, pos);
		return bytes;
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

	private void writeDataHeader(int type)
	{
		writeRaw((byte)type);
	}
	
	private void writeLength(int len)
	{
		writeRaw((byte)(len >>> 24));
		writeRaw((byte)(len >>> 16));
		writeRaw((byte)(len >>> 8));
		writeRaw((byte)len);
	}
	
	private void writeRaw(byte n)
	{
		ensureCapacity(pos + 1);
		data[pos] = n;
		++pos;
	}
	
	private void writeRaw(byte[] b, int off, int len)
	{
		ensureCapacity(pos + len);
		System.arraycopy(b, off, data, pos, len);
		pos += len;
	}
	
	private void ensureCapacity(int minLen)
	{
		int oldLen = data.length;
		if(minLen > oldLen)
		{
			int newLen = oldLen << 1;
			if(newLen - minLen < 0)
				newLen = minLen;
			if(newLen < 0)
			{
				if(minLen < 0) // overflow
					throw new OutOfMemoryError();
				newLen = Integer.MAX_VALUE;
			}
			byte[] tmp = new byte[newLen];
			System.arraycopy(data, 0, tmp, 0, oldLen);
			data = tmp;
		}
	}
}
