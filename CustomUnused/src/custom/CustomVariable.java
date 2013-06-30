package custom;

public final class CustomVariable
{
	private CustomClass clazz;
	private String name;
	
	public CustomVariable(String className, String name)
	{
		this.clazz = CustomClass.forName(className);
		this.name = name;
	}
	
	public CustomVariable(CustomClass clazz, String name)
	{
		this.clazz = clazz;
		this.name = name;
	}
	
	public CustomClass getType()
	{
		return clazz;
	}
	
	public String getName()
	{
		return name;
	}
}
