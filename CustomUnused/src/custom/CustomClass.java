package custom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomClass
{
	private String name;
	
	private List<CustomClass> fields;
	private List<CustomMethod> methods;
	
	private static Map<String, CustomClass> primitiveMap;
	private static Map<Class<?>, CustomClass> reflectMap;
	private static Map<String, CustomClass> classMap;
	
	public static CustomClass forReflectionClass(Class<?> clazz)
	{
		CustomClass cc = reflectMap.get(clazz);
		if(cc == null)
		{
			cc = new CustomClass(clazz.getName(), false);
			reflectMap.put(clazz, cc);
		}
		return cc;
	}
	
	public static CustomClass forName(String className)
	{
		CustomClass primitive = primitiveMap.get(className);
		if(primitive != null)
			return primitive;
		
		try
		{
			Class<?> refClass = Class.forName(className);
			return forReflectionClass(refClass);
		}
		catch(ClassNotFoundException e)
		{
		}
		
		return null;
	}
	
//	public static CustomClass loadClass(String name, List<CustomClass> fields, List<CustomMethod> methods)
//	{
//		if(classMap.containsKey(name))
//			throw new IllegalStateException("CustomClass with name " + name + " already loaded");
//		CustomClass cc = new CustomClass(name, new ArrayList<>(fields), new ArrayList<>(methods));
//		classMap.put(name, cc);
//		return cc;
//	}
	
	public static CustomClass loadClass(CustomClass clazz)
	{
		classMap.put(clazz.getName(), clazz);
		return clazz;
	}
	
	public CustomClass(String name, List<CustomClass> fields, List<CustomMethod> methods)
	{
		this.name = name;
		this.fields = fields;
		this.methods = methods;
	}
	
	private CustomClass(String name, boolean primitive)
	{
		this.name = name;
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public List<CustomClass> getFields()
	{
		return Collections.unmodifiableList(fields);
	}
	
	public List<CustomMethod> getMethods()
	{
		return Collections.unmodifiableList(methods);
	}
	
	@Override
	public String toString()
	{
		return "{CustomClass name=" + getName() + "}";
	}
	
	static
	{
		primitiveMap = new HashMap<>();
		primitiveMap.put("boolean", new CustomClass("boolean", true));
		primitiveMap.put("byte", new CustomClass("byte", true));
		primitiveMap.put("short", new CustomClass("short", true));
		primitiveMap.put("int", new CustomClass("int", true));
		primitiveMap.put("long", new CustomClass("long", true));
		primitiveMap.put("float", new CustomClass("float", true));
		primitiveMap.put("double", new CustomClass("double", true));
		primitiveMap.put("char", new CustomClass("char", true));
		
		reflectMap = new HashMap<>();
	}
}
