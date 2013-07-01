package custom;

import java.io.IOException;

import com.github.antlrjavaparser.JavaParser;
import com.github.antlrjavaparser.ParseException;
import com.github.antlrjavaparser.api.CompilationUnit;

import custom.factory.ClassFactory;

public class CustomClassParser
{
	private CustomSourceReader source;

	public CustomClassParser(CustomSourceReader src)
	{
		this.source = src;
	}
	
	public CustomClass parse(String className)
	{
		try
		{
			CompilationUnit cu = JavaParser.parse(source.getStream(className));
			CustomClassVisitor cv = new CustomClassVisitor(source);
			cu.accept(cv, null);

			for(ClassFactory cf : cv.classes)
			{
				System.out.println(cf);
			}
			
			return null;
		}
		catch(ParseException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
