package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AESKeyRecieverNode extends AESKeyNode
{
	public AESKeyRecieverNode(int rsaKeySize, InputStream in, OutputStream out) throws IOException
	{
		System.out.println("[R] creating rsa server");
		RSAServerNode node = RSAServerNode.getInstance(rsaKeySize);
		System.out.println("[R] sending rsa public key");
		node.sendPublicKey(out);
		
		ByteDataReciever reciever = new ByteDataReciever(in);
		System.out.println("[R] recieving encrypted aes key");
		byte[] encrypted = reciever.readBytes();
		System.out.println("[R] encrypted aes key recieved");
		
		this.encodedKey = node.decrypt(encrypted);
	}
	
	public AESKeyRecieverNode(byte[] key)
	{
		this.encodedKey = key;
	}
}
