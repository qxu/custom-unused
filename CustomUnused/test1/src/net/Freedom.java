package net;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class Freedom
{
	private static final char[] validChars;
	static
	{
		StringBuilder validCharBuilder = new StringBuilder();
		for(char c = 'A'; c <= 'Z'; ++c)
			validCharBuilder.append(c);
		for(char c = 'a'; c <= 'z'; ++c)
			validCharBuilder.append(c);
		for(char c = '0'; c <= '9'; ++c)
			validCharBuilder.append(c);
		validChars = validCharBuilder.toString().toCharArray();
	}
	
	private static String randomString(int length)
	{
		Random rand = new Random();
		char[] charData = new char[1024];
		for(int i = 0; i < charData.length; ++i)
		{
			charData[i] = validChars[rand.nextInt(validChars.length)];
		}
		return String.valueOf(charData);
	}
	
	public static void main(String[] args) throws Exception
	{
		final String dataStr = randomString(1024);

		long start = System.nanoTime();
		
		final TestSocket ss = new TestSocket();
		final TestSocket cs = new TestSocket(ss);
		
		Thread serverThread = new Thread("server")
		{
			@Override
			public void run()
			{
				try
				{
					final InputStream in = ss.getInputStream();
					final OutputStream out = ss.getOutputStream();
					AESNode server = new AESNode(in, out, new AESKeyRecieverNode(1024, in, out));
					System.out.println("sending: " + dataStr);
					server.encryptAndWrite(dataStr.getBytes("UTF-8"));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		Thread clientThread = new Thread("client")
		{
			@Override
			public void run()
			{
				try
				{
					final InputStream in = cs.getInputStream();
					final OutputStream out = cs.getOutputStream();
					AESNode client = new AESNode(in, out, new AESKeyGeneratorNode(1024, in, out));
					String message = new String(client.readAndDecrypt(), "UTF-8");
					System.out.println("recieved: " + message);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		clientThread.start();
		serverThread.start();
		
		serverThread.join();
		clientThread.join();
		
		long stop = System.nanoTime();
		System.out.println((stop - start) / 1.0E6 + " ms");

	}
}
