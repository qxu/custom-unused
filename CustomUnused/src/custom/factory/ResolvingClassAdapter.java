package custom.factory;

import custom.CustomType;

public class ResolvingClassAdapter extends ClassFactory
{
	private ClassFactoryResolver resolver;
	private String resolveName;
	
	public ResolvingClassAdapter(String resolveName, ClassFactoryResolver resolver)
	{
		this.resolveName = resolveName;
		this.resolver = resolver;
	}
	
	public String getName()
	{
		return resolveName;
	}
	
	public CustomType build()
	{
		ClassFactory cf = resolver.resolve(resolveName);
		if(cf == null)
			throw new IllegalStateException("Unable to resolve " + resolveName);
		return cf.build();
	}
}
