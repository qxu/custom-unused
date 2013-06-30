package net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocketFactory;

public class Test
{
	private static String toString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		for(byte b : bytes)
		{
			sb.append(Integer.toHexString(b & 0xff) + " ");
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception
	{
		Thread serverThread = new ServerThread();

		serverThread.start();

		serverThread.join();
	}

	private static class ServerThread extends Thread
	{
		private ServerSocket createServerSocket() throws Exception
		{
			SSLContext context = SSLContext.getInstance("TLS");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("JKS");
			char[] pass = "password".toCharArray();
			ks.load(new FileInputStream("testkeys"), pass);
	        kmf.init(ks, pass);
	        context.init(kmf.getKeyManagers(), null, null);
	        ServerSocket ss = context.getServerSocketFactory().createServerSocket(8881);
	        ((SSLServerSocket)ss).setNeedClientAuth(false);
	        return ss;
		}
		
		@Override
		public void run()
		{
			try
			{
				ServerSocket server = createServerSocket();
				Socket socket = server.accept();

				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String str;
				try
				{
					while((str = reader.readLine()) != null)
					{
						System.out.println(str);
						if(str.equals(""))
							break;
					}

				}
				catch(SSLException e)
				{
					e.printStackTrace();
				}

				PrintWriter out = new PrintWriter(socket.getOutputStream());
				System.out.println("sending...");
				out.println("<html><head><title>This is title</title></head><body>Hello world</body></html>");

				out.flush();
				out.close();

				socket.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class ClientThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				Socket socket = SSLSocketFactory.getDefault().createSocket("localhost", 8881);

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
