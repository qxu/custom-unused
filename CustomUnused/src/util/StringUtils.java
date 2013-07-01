package util;

import java.util.Arrays;
import java.util.Iterator;

public final class StringUtils
{
	public static void main(String[] args)
	{
		String s = fencePostConcat(Arrays.asList(), "a");
		System.out.println(s);
	}
	
	public static String fencePostConcat(Iterable<?> elems, String delim)
	{
		Iterator<?> iter = elems.iterator();
		if(iter.hasNext())
		{
			if(delim == null)
				delim = "";
			StringBuilder sb = new StringBuilder();
			sb.append(iter.next());
			while(iter.hasNext())
			{
				sb.append(delim);
				sb.append(iter.next());
			}
			return sb.toString();
		}
		else
		{
			return "";
		}
	}
	
	private StringUtils()
	{
	}
}
