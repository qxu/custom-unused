package net;

import java.util.Arrays;


public abstract class AESKeyNode
{
	protected byte[] encodedKey;
	
	public byte[] getEncodedKey()
	{
		return encodedKey;
	}
	
	public void clear()
	{
		Arrays.fill(encodedKey, (byte)0);
	}
}
