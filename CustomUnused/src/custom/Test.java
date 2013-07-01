package custom;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Test
{
	static final String SRC_DIR = "test1/src";
	static final String BIN_DIR = "test1/bin";
	
	private static List<File> listFilesRec(File dir)
	{
		return listFilesRec0(dir);
	}
	
	private static List<File> listFilesRec0(File dir)
	{
		List<File> files = new ArrayList<>();
		for(File subF : dir.listFiles())
		{
			if(subF.isDirectory())
			{
				files.addAll(listFilesRec0(subF));
			}
			else
			{
				files.add(subF);
			}
		}
		return files;
	}
	
	public static void main(String[] args) throws Exception
	{
		Set<String> classNames = new HashSet<>();
		addSrcClassNames(classNames);
		removeBinClassNames(classNames);
		
		System.out.println("\n--------------------\n");
		TreeSet<String> set = new TreeSet<>();
		for(String name : classNames)
		{
			if(name.startsWith("org.bouncycastle"))
			{
				set.add(name);
			}
		}
		
		for(String name : set)
		{
			File file = new File(SRC_DIR + "/" + name.replace(".", "/") + ".java");
			if(file.delete())
				System.out.println("[deleted]" + file);
		}
	}
	
	private static void addSrcClassNames(Set<String> classNames)
	{
		final int substrIndex0 = SRC_DIR.length() + 1;
		for(File file : listFilesRec(new File(SRC_DIR)))
		{
			String name = file.getPath();
			if(name.endsWith(".java"))
			{
				String className = name.substring(substrIndex0, name.length() - 5).replace("\\", ".");
				classNames.add(className);
			}
		}
	}
	
	private static void removeBinClassNames(Set<String> classNames)
	{
		final int substrIndex0 = BIN_DIR.length() + 1;
		for(File file : listFilesRec(new File(BIN_DIR)))
		{
			String name = file.getPath();
			if(name.endsWith(".class"))
			{
				String className = name.substring(substrIndex0, name.length() - 6).replace("\\", ".");
				if(!classNames.remove(className))
				{
//					System.err.println(className);
				}
				else
				{
					System.out.println("[removed]" + className);
				}
			}
		}
	}
}
