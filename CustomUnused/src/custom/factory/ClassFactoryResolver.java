package custom.factory;

import java.util.HashMap;
import java.util.Map;

public class ClassFactoryResolver
{
	private Map<String, ClassFactory> localClassMap;
	private Map<String, String> importMap;
	private Map<String, String> packageMap;
	
	public ClassFactoryResolver()
	{
		this.localClassMap = new HashMap<>();
		this.importMap = new HashMap<>();
		this.packageMap = new HashMap<>();
	}
	
	public void addPackage(String className)
	{
		packageMap.put(ClassFactory.getLocalName(className), className);
	}
	
	public void addImport(String className)
	{
		importMap.put(ClassFactory.getLocalName(className), className);
	}
	
	public void addLocalClass(ClassFactory clazz)
	{
		localClassMap.put(clazz.getLocalName(), clazz);
	}
	
	public ClassFactory resolve(String localName)
	{
		ClassFactory cf = localClassMap.get(localName);
		if(cf != null)
			return cf;
		String className = importMap.get(localName);
		if(className != null)
			return ClassFactory.findOrCreate(className);
		className = packageMap.get(localName);
		if(className != null)
			return ClassFactory.findOrCreate(className);
		cf = ClassFactory.find(localName);
		if(cf != null)
			return cf;
		cf = ClassFactory.find("java.lang." + localName);
		if(cf != null)
			return cf;
		cf = ClassFactory.create(localName);
		return cf;
	}
}
