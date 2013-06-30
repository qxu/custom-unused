package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AESKeyGeneratorNode extends AESKeyNode
{
	public AESKeyGeneratorNode(int aeskeySize, InputStream in, OutputStream out) throws IOException
	{
		initAESKeyServices();
		
		KeyGenerator gen = getKeyGenerator();
		SecretKey key = gen.generateKey();
		
		byte[] encoded = key.getEncoded();

		RSAClientNode node = RSAClientNode.getInstance();
		System.out.println("[G] recieving rsa public key");
		node.recievePublicKey(in);
		System.out.println("[G] rsa public key recieved");
		byte[] encrypted = node.encrypt(encoded);
		
		ByteDataSender sender = new ByteDataSender(out);
		sender.writeBytes(encrypted);
		sender.flush();
		
		this.encodedKey = encoded;
	}

	private static boolean servicesInitialized = false;
	private static KeyGenerator keyGenerator;
	
	private static void initAESKeyServices()
	{
		if(!servicesInitialized)
		{
			try
			{
				keyGenerator = KeyGenerator.getInstance("AES");
				servicesInitialized = true;
			}
			catch(Exception e)
			{
				throw new ExceptionInInitializerError(e);
			}
		}
	}
	
	private static KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}
}
