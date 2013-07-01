package custom;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomClass extends AbstractCustomType
{
	private String name;

	private List<CustomType> fields;
	private List<CustomMethod> methods;

	private static Map<Class<?>, CustomClass> reflectMap;
	private static Map<String, CustomClass> classMap;

	public static CustomClass forReflectionClass(Class<?> clazz)
	{
		CustomClass cc = reflectMap.get(clazz);
		if(cc == null)
		{
			cc = new CustomClass(clazz.getName());
			reflectMap.put(clazz, cc);
		}
		return cc;
	}

	public static CustomClass forName(String className)
	{
		CustomClass existing = classMap.get(className);
		if(existing != null)
			return existing;

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

	protected CustomClass(String name)
	{
		this(name, Collections.<CustomType>emptyList(), Collections.<CustomMethod>emptyList());
	}

	public CustomClass(String name, List<CustomType> fields, List<CustomMethod> methods)
	{
		if(classMap.containsKey(name))
			throw new IllegalStateException(name + " already initialized");
		
		this.name = name;
		this.fields = fields;
		this.methods = methods;
		classMap.put(name, this);
	}

	public String getName()
	{
		return this.name;
	}

	public List<CustomType> getFields()
	{
		return Collections.unmodifiableList(fields);
	}

	public List<CustomMethod> getMethods()
	{
		return Collections.unmodifiableList(methods);
	}
}
