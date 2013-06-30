package custom;
import java.util.Collections;
import java.util.List;


public final class CustomMethod
{
	private String name;
	private CustomClass clazz;
	
	private List<CustomClass> params;
	private List<CustomClass> vars;
	private List<CustomMethod> calls;
	
	public CustomMethod(String name, CustomClass clazz,
			List<CustomClass> params, List<CustomClass> vars, List<CustomMethod> calls)
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
	
	public List<CustomClass> getParamTypeDeclarations()
	{
		return Collections.unmodifiableList(params);
	}
	
	public List<CustomClass> getTypeDeclarations()
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
