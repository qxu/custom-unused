package custom.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import custom.CustomClass;
import custom.CustomMethod;
import custom.CustomType;

public class ClassFactory
{
	private String name;
	
	private List<ClassFactory> fields;
	private List<MethodFactory> methods;
	
	private CustomClass build;
	
	private static Map<String, ClassFactory> classNameMap = new HashMap<>();
	
	public static ClassFactory findOrCreate(String className)
	{
		ClassFactory cf = find(className);
		if(cf != null)
			return cf;
		return create(className);
	}
	
	public static ClassFactory find(String className)
	{
		ClassFactory cf = classNameMap.get(className);
		if(cf != null)
			return cf;
		
		CustomClass existing = CustomClass.forName(className);
		if(existing != null)
			cf = new ExistingClassAdapter(existing);
		else
			return null;
		
		classNameMap.put(className, cf);
		return cf;
	}
	
	public static ClassFactory create(String className)
	{
		ClassFactory cf = new ClassFactory(className);
		classNameMap.put(className, cf);
		return cf;
	}

	public static String getLocalName(String className)
	{
		int index = className.lastIndexOf(".");
		className = className.replace('$', '.');
		return index > 0 ? className.substring(index + 1) : className;
	}
	
	protected ClassFactory()
	{
		this(null);
	}
	
	private ClassFactory(String name)
	{
		this.name = name;
		fields = new ArrayList<>();
		methods = new ArrayList<>();
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getLocalName()
	{
		return getLocalName(getName());
	}
	
	public void addField(ClassFactory c)
	{
		fields.add(c);
	}
	
	public void addMethod(MethodFactory m)
	{
		methods.add(m);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("ClassFactory: " + getName());
		for(ClassFactory cf : fields)
		{
			sb.append("\n\t" + cf.getLocalName());
		}
		for(MethodFactory mf : methods)
		{
			sb.append("\n\t" + mf.toString().replace("\n", "\n\t"));
		}
		return sb.toString();
	}
	
	public CustomType build()
	{
		if(build != null)
			return build;
		
		List<CustomType> cFields = new ArrayList<>(fields.size());
		List<CustomMethod> cMethods = new ArrayList<>(methods.size());
		build = new CustomClass(name, cFields, cMethods);
		for(ClassFactory cf : fields)
		{
			cFields.add(cf.build());
		}
		for(MethodFactory mf : methods)
		{
			cMethods.add(mf.build());
		}
		return build;
	}
}
