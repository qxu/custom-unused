package custom;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomSourceReader
{
	public static CustomSourceReader fromSourceDir(String srcDirName)
	{
		File srcDir = new File(srcDirName);
		return fromSourceDir(srcDir);
	}
	
	public static CustomSourceReader fromSourceDir(File srcDir)
	{
		CustomSourceReader loader = new CustomSourceReader(srcDir);
		return loader;
	}
	
	private File srcDir;
	
	private CustomSourceReader(File srcDir)
	{
		if(!srcDir.isDirectory())
			throw new IllegalArgumentException();
		this.srcDir = srcDir;
	}
	
	public List<String> getClassNamesInPackage(String pkgName)
	{
		List<String> classNames = new ArrayList<>();
		File pkgFile = new File(srcDir, pkgName.replace(".", "/"));
		for(String fileName : pkgFile.list())
		{
			if(fileName.endsWith(".java"))
			{
				String name = fileName.substring(0, fileName.length() - 5);
				classNames.add(pkgName +  "." + name);
			}
		}
		return classNames;
	}
	
	public boolean exists(String className)
	{
		File srcFile = new File(srcDir, className.replace(".", "/") + ".java");
		return srcFile.exists();
	}
	
	public InputStream getStream(String className)
	{
		File srcFile = new File(srcDir, className.replace(".", "/") + ".java");
		try
		{
			return new FileInputStream(srcFile);
		}
		catch(FileNotFoundException e)
		{
			throw new IllegalArgumentException("Class " + className + " does not exist");
		}
	}
}
