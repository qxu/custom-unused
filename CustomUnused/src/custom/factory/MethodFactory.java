package custom.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import custom.CustomClass;
import custom.CustomMethod;
import custom.CustomType;

public class MethodFactory
{
	private String name;
	private ClassFactory enclosingClass;
	
	private List<ClassFactory> params;
	private List<ClassFactory> vars;
	private List<MethodFactory> calls;
	
	private CustomMethod build;
	
	public MethodFactory()
	{
		this(null, null);
	}
	
	public MethodFactory(String name, ClassFactory enclosingClass)
	{
		this.name = name;
		this.enclosingClass = enclosingClass;

		params = new ArrayList<>();
		vars = new ArrayList<>();
		calls = new ArrayList<>();
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}

	public String getHeaderName()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getName() + "(");
		int nParams = params.size();
		if(nParams > 0)
		{
			Iterator<ClassFactory> iter = params.iterator();
			sb.append(iter.next().getLocalName());
			while(iter.hasNext())
			{
				sb.append(", " + iter.next().getLocalName());
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public void setEnclosingClass(ClassFactory c)
	{
		this.enclosingClass = c;
	}
	
	public ClassFactory getEnclosingClass()
	{
		return this.enclosingClass;
	}
	
	public void addParameter(ClassFactory c)
	{
		params.add(c);
	}
	
	public void addVariable(ClassFactory c)
	{
		vars.add(c);
	}
	
	public void addMethodCall(MethodFactory m)
	{
		calls.add(m);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("MethodFactory: " + getHeaderName());
		for(ClassFactory cf : vars)
		{
			sb.append("\n\t" + cf.getName());
		}
		for(MethodFactory mf : calls)
		{
			sb.append("\n\t" + mf.getHeaderName());
		}
		return sb.toString();
	}

	public CustomMethod build()
	{
		if(build != null)
			return build;
		
		List<CustomType> cParams = new ArrayList<>(params.size());
		List<CustomType> cVars = new ArrayList<>(vars.size());
		List<CustomMethod> cCalls = new ArrayList<>(calls.size());
		build = new CustomMethod(name, (CustomClass)enclosingClass.build(), cParams, cVars, cCalls);
		for(ClassFactory cf : params)
		{
			cParams.add(cf.build());
		}
		for(ClassFactory cf : vars)
		{
			cVars.add(cf.build());
		}
		for(MethodFactory mf : calls)
		{
			cCalls.add(mf.build());
		}
		return build;
	}
}
