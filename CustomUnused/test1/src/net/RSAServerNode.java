package net;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;


public class RSAServerNode
{
	private RSAPublicKeySpec publicKeySpec;
	private PrivateKey privateKey;
	
	public static RSAServerNode getInstance(int keySize)
	{
		try
		{
			return new RSAServerNode(keySize);
		}
		catch(InvalidKeySpecException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public RSAServerNode(int keySize) throws InvalidKeySpecException
	{
		initRSAServices();
		
		KeyPairGenerator kpg = getKeyPairGenerator();
		kpg.initialize(keySize);
		KeyPair kp = kpg.generateKeyPair();

		KeyFactory kf = getKeyFactory();
		
		this.publicKeySpec = kf.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
		this.privateKey = kp.getPrivate();
	}
	
	public byte[] decrypt(byte[] enc)
	{
		try
		{
			Cipher c = getCipher();
			c.init(Cipher.DECRYPT_MODE, privateKey);
			return c.doFinal(enc);
		}
		catch(GeneralSecurityException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void sendPublicKey(OutputStream out) throws IOException
	{
		ByteDataSender sender = new ByteDataSender(out);
		sender.writeBytes(publicKeySpec.getModulus().toByteArray());
		sender.writeBytes(publicKeySpec.getPublicExponent().toByteArray());
		sender.flush();
	}

	private static boolean servicesInitialized = false;
	private static KeyPairGenerator keyPairGenerator;
	private static KeyFactory keyFactory;
	private static Cipher cipher;
	
	private static void initRSAServices()
	{
		if(!servicesInitialized)
		{
			try
			{
				keyPairGenerator = KeyPairGenerator.getInstance("RSA");
				keyFactory = KeyFactory.getInstance("RSA");
				cipher = Cipher.getInstance("RSA");
				servicesInitialized = true;
			}
			catch(Exception e)
			{
				throw new ExceptionInInitializerError(e);
			}
		}
	}
	
	private static KeyPairGenerator getKeyPairGenerator()
	{
		return keyPairGenerator;
	}
	
	private static Cipher getCipher()
	{
		return cipher;
	}
	
	private static KeyFactory getKeyFactory()
	{
		return keyFactory;
	}
}
