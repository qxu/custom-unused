package net;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteDataSender extends FilterOutputStream
{
	public ByteDataSender(OutputStream out)
	{
		super(out);
	}

	public void writeBytes(byte[] data) throws IOException
	{
		final int len = data.length;
		out.write((len >>> 24) & 0xff);
		out.write((len >>> 16) & 0xff);
		out.write((len >>> 8) & 0xff);
		out.write(len & 0xff);
		
		out.write(data, 0, len);
	}
}
