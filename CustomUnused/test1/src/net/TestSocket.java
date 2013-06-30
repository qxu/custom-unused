package net;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class TestSocket
{
	byte[] dataBuffer;
	private final Object bufMonitor = new Object();
	private int readPos;
	private int writePos;

	private TestSocket link;

	public TestSocket()
	{
		this(null);
	}
	
	public TestSocket(TestSocket other)
	{
		this.dataBuffer = new byte[32];
		link(other);
	}

	public InputStream getInputStream()
	{
		return new TestInputStream();
	}

	public OutputStream getOutputStream()
	{
		return link.new TestOutputStream();
	}

	public TestSocket getLinkedSocket()
	{
		return this.link;
	}

	public void unlink()
	{
		if(link != null)
		{
			link.link = null;
			this.link = null;
		}
	}

	public void link(TestSocket other)
	{
		unlink();
		
		if(other != null)
		{
			other.unlink();
	
			this.link = other;
			other.link = this;
		}
	}

	private void ensureCapacity(int minCapacity)
	{
		synchronized(dataBuffer)
		{
			if(minCapacity > dataBuffer.length)
				grow(minCapacity);
		}
	}

	private void grow(int minCapacity)
	{
		synchronized(dataBuffer)
		{
			int oldCapacity = dataBuffer.length;
			int newCapacity = oldCapacity << 1;
			if(newCapacity - minCapacity < 0)
				newCapacity = minCapacity;
			if(newCapacity < 0)
			{
				if(minCapacity < 0) // overflow
					throw new OutOfMemoryError();
				newCapacity = Integer.MAX_VALUE;
			}
			dataBuffer = Arrays.copyOf(dataBuffer, newCapacity);
		}
	}

	private class TestInputStream extends InputStream
	{
		public int read()
		{
			synchronized(bufMonitor)
			{
//				System.out.println("[TestSocket.TestInputStream] read " + (dataBuffer[readPos] & 0xff));
				return checkBytesWritten() ? dataBuffer[readPos++] & 0xff : -1;
			}
		}

		public int read(byte[] buf, int off, int len)
		{
			if(buf == null)
			{
				throw new NullPointerException();
			}
			else if(off < 0 || len < 0 || len > buf.length - off)
			{
				throw new IndexOutOfBoundsException();
			}

			synchronized(bufMonitor)
			{
				if(!checkBytesWritten())
					return -1;
				
				int avail = writePos - readPos;
				if(len > avail)
				{
					len = avail;
				}
				if(len <= 0)
				{
					return 0;
				}
				
				System.arraycopy(dataBuffer, readPos, buf, off, len);
				readPos += len;
//				System.out.println("[TestSocket.TestInputStream] read " + Arrays.toString(Arrays.copyOfRange(buf, off, off + len)));
				return len;
			}
		}

		public long skip(long n)
		{
			synchronized(bufMonitor)
			{
				long k = writePos - readPos;
				if(n < k)
				{
					k = n < 0 ? 0 : n;
				}

				readPos += k;
				return k;
			}
		}

		public int available()
		{
			synchronized(bufMonitor)
			{
				return writePos - readPos;
			}
		}
		
		private boolean checkBytesWritten()
		{
			while(readPos >= writePos)
			{
				try
				{
//					System.out.println("[TestSocket.TestInputStream#" + hashCode() + "] waiting for buffer update");
					bufMonitor.wait();
				}
				catch(InterruptedException e)
				{ // ignore
				}
			}
//			System.out.println("[TestSocket.TestInputStream#" + hashCode() + "] buffer update recieved");
			return true;
		}
		
		public void close()
		{
			System.out.println("closing input stream!");
		}
	}

	private class TestOutputStream extends OutputStream
	{
		public void write(int b)
		{
			synchronized(bufMonitor)
			{
				ensureCapacity(writePos + 1);
				dataBuffer[writePos] = (byte)b;
				++writePos;
				
//				System.out.println("[TestSocket.TestOutputStream] wrote " + b);
				bufMonitor.notifyAll();
			}
		}

		public void write(byte[] data, int off, int len)
		{
			if((off < 0) || (off > data.length) || (len < 0)
					|| ((off + len) - data.length > 0))
			{
				throw new IndexOutOfBoundsException();
			}
			synchronized(bufMonitor)
			{
				ensureCapacity(writePos + len);
				System.arraycopy(data, off, dataBuffer, writePos, len);
				writePos += len;

//				System.out.println("[TestSocket.TestOutputStream] wrote " + Arrays.toString(data));
				bufMonitor.notifyAll();
			}
		}

		public String toString()
		{
			synchronized(bufMonitor)
			{
				return new String(dataBuffer, 0, writePos);
			}
		}
		
		public void close()
		{
			System.out.println("closing output stream!");
		}
	}
}
