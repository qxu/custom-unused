package net;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteDataReciever extends FilterInputStream
{
	public ByteDataReciever(InputStream in)
	{
		super(in);
	}

	private byte[] readFully(int len) throws IOException
	{
		byte[] buf = new byte[len];
		int n = 0;
		while(n < len)
		{
			int count = in.read(buf, n, len - n);
			if(count < 0)
			{
				throw new EOFException();
			}
			n += count;
		}
		return buf;
	}

	public byte[] readBytes() throws IOException
	{
		byte[] lengthBytes = readFully(4);
		
		int len = (lengthBytes[0] << 24)
				+ ((lengthBytes[1] & 0xff) << 16)
				+ ((lengthBytes[2] & 0xff) << 8)
				+ (lengthBytes[3] & 0xff);
		
		byte[] data = readFully(len);
		return data;
	}
}
