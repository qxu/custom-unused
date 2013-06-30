package util;

import java.math.BigInteger;

public class DTest
{
	public static void main(String[] args)
	{
		DataByteEncoder encoder = new DataByteEncoder();
		encoder.writeByte((byte)-121);
		encoder.writeShort((short)-31004);
		encoder.writeInt(921810980);
		encoder.writeFloat(0.123771F);
		encoder.writeDouble(1.10293809128322);
		encoder.writeUTF("hello sir");
		encoder.writeBigInteger(new BigInteger("109283091283092183091283092183092183092183"));
		encoder.writeData("welcome rsadot32".getBytes());
		
		byte[] bytes = encoder.getBytes();
		encoder.close();
		
		DataByteDecoder decoder = new DataByteDecoder(bytes);
		System.out.println(decoder.readByte());
		System.out.println(decoder.readShort());
		System.out.println(decoder.readInt());
		System.out.println(decoder.readFloat());
		System.out.println(decoder.readDouble());
		System.out.println(decoder.readUTF());
		System.out.println(decoder.readBigInteger());
		System.out.println(new String(decoder.readData()));
		decoder.close();
	}
}
