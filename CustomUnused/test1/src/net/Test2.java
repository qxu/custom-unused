package net;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi;
import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;

public class Test2
{
	private static String toHexString(byte[] b)
	{
		final int len = b.length;
		if(len == 0)
			return "";
		char[] buf = new char[2];
		StringBuilder sb = new StringBuilder(len * 3 - 1);
		for(int i = 0;;)
		{
			sb.append(buf);
			sb.append(' ');
			
			++i;
			if(i == len)
				return sb.append(buf).toString();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		List<String> genDummy = new ArrayList<>();
		
		String text = "hello sirs";
		
		long start = System.nanoTime();
		aes(text);
		rsa(text);
		long stop = System.nanoTime();
		System.out.println((stop - start) / 1.0E6 + " ms");
	}
	
	private static void aes(String text) throws Exception
	{
		final SecureRandom rand = new SecureRandom();
		BaseKeyGenerator gen = new AES.KeyGen(256);
		gen.engineInit(rand);
		
		SecretKey key = gen.engineGenerateKey();

		BaseBlockCipher aesEnc = new AES.CBC();
		aesEnc.engineSetPadding("PKCS7Padding");
		byte[] iv = new byte[16];
		rand.nextBytes(iv);
		aesEnc.engineInit(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv), rand);
		
		byte[] textBytes = text.getBytes("UTF-8");
		byte[] enc = aesEnc.engineDoFinal(textBytes, 0, textBytes.length);
		
		SecretKeySpec spec = new SecretKeySpec(key.getEncoded(), "AES");
		
		BaseBlockCipher aesDec = new AES.CBC();
		aesDec.engineInit(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(iv), rand);
		byte[] dec = aesDec.engineDoFinal(enc, 0, enc.length);
		System.out.println(new String(dec, "UTF-8"));
	}
	
	private static void rsa(String text) throws Exception
	{
		final SecureRandom rand = new SecureRandom();
		KeyPairGeneratorSpi gen = new KeyPairGeneratorSpi();
		gen.initialize(1024);
		KeyPair keyPair = gen.generateKeyPair();
		
		CipherSpi rsaEnc = new CipherSpi(OAEPParameterSpec.DEFAULT);
		rsaEnc.engineInit(Cipher.ENCRYPT_MODE, keyPair.getPublic(), rand);
		
		byte[] textBytes = text.getBytes("UTF-8");
		byte[] enc = rsaEnc.engineDoFinal(textBytes, 0, textBytes.length);
		
		CipherSpi rsaDec = new CipherSpi(OAEPParameterSpec.DEFAULT);
		rsaDec.engineInit(Cipher.DECRYPT_MODE, keyPair.getPrivate(), rand);
		byte[] dec = rsaDec.engineDoFinal(enc, 0, enc.length);
		System.out.println(new String(dec, "UTF-8"));
	}
	
	private static class CDummy extends ArrayList<Object>
	{
		private int x;
		
		public CDummy()
		{
			super();
			String a = new String();
			Math.sqrt(2.0);
			String s = (String)"123";
		}
		
		public CDummy(String s)
		{
			this();
		}
		
		private void innerDummyMethod()
		{
			new Iterator<Integer>()
			{
				@Override
				public boolean hasNext()
				{
					return false;
				}

				@Override
				public Integer next()
				{
					class Wtf
					{
						public void a()
						{
							
						}
					}
					return null;
				}

				@Override
				public void remove()
				{
				}
			};
		}
	}
}