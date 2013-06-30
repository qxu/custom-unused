package net;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;


public class RSAClientNode
{
	private PublicKey publicKey;
	
	public static RSAClientNode getInstance()
	{
		return new RSAClientNode();
	}
	
	public RSAClientNode()
	{
		initRSAServices();
	}
	
	public void recievePublicKey(InputStream in) throws IOException
	{
		ByteDataReciever reciever = new ByteDataReciever(in);
		
		BigInteger mod = new BigInteger(reciever.readBytes());
		BigInteger exp = new BigInteger(reciever.readBytes());
		
		RSAPublicKeySpec pubKs = new RSAPublicKeySpec(mod, exp);
		KeyFactory kf = getKeyFactory();
		try
		{
			this.publicKey = kf.generatePublic(pubKs);
		}
		catch(InvalidKeySpecException e)
		{
			throw new IOException("Invalid data recieved");
		}
	}
	
	public byte[] encrypt(byte[] data)
	{
		try
		{
			Cipher c = getCipher();
			c.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] enc = c.doFinal(data);
			return enc;
		}
		catch(IllegalBlockSizeException e)
		{
			throw new IllegalArgumentException(e);
		}
		catch(GeneralSecurityException e)
		{
			throw new AssertionError(e);
		}
	}
	
	private static boolean servicesInitialized = false;
	private static KeyFactory keyFactory;
	private static Cipher cipher;
	
	private static void initRSAServices()
	{
		if(!servicesInitialized)
		{
			try
			{
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
	
	private static Cipher getCipher()
	{
		return cipher;
	}
	
	private static KeyFactory getKeyFactory()
	{
		return keyFactory;
	}
}
