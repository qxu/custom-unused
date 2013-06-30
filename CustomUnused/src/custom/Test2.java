package custom;

import custom.factory.ClassFactory;

public class Test2
{
	public static void main(String[] args) throws Exception
	{
		System.out.print("");
		
		CustomSourceReader src = CustomSourceReader.fromSourceDir("test1/src/");
		CustomClassParser parser = new CustomClassParser(src);
		
		parser.parse("net.Test2");
	}
}
