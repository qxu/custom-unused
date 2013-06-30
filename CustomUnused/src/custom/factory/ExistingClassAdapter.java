package custom.factory;

import custom.CustomClass;

public class ExistingClassAdapter extends ClassFactory
{
	private CustomClass clazz;
	
	public ExistingClassAdapter(CustomClass clazz)
	{
		this.clazz = clazz;
	}

	public String getName()
	{
		return clazz.getName();
	}
	
	public CustomClass build()
	{
		return clazz;
	}
}
