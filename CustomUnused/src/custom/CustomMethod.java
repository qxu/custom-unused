package custom;
import java.util.Collections;
import java.util.List;


public final class CustomMethod
{
	private String name;
	private CustomClass clazz;
	
	private List<CustomType> params;
	private List<CustomType> vars;
	private List<CustomMethod> calls;
	
	public CustomMethod(String name, CustomClass clazz,
			List<CustomType> params, List<CustomType> vars, List<CustomMethod> calls)
	{
		this.name = name;
		this.params = params;
		this.clazz = clazz;
		this.vars = vars;
		this.calls = calls;
	}
	
	public String getName()
	{
		return name;
	}
	
	public CustomClass getEnclosedClass()
	{
		return this.clazz;
	}
	
	public List<CustomType> getParamTypeDeclarations()
	{
		return Collections.unmodifiableList(params);
	}
	
	public List<CustomType> getTypeDeclarations()
	{
		return Collections.unmodifiableList(vars);
	}
	
	public List<CustomMethod> getMethodCalls()
	{
		return Collections.unmodifiableList(calls);
	}
	
	@Override
	public String toString()
	{
		return "{CustomMethod: name=" + getName() + " params=" + getParamTypeDeclarations() + "}";
	}
}
