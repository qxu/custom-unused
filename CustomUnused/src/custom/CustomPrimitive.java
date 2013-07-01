package custom;

import java.util.List;

public enum CustomPrimitive implements CustomType
{
	BOOLEAN,
	SHORT,
	INT,
	LONG,
	FLOAT,
	DOUBLE,
	CHAR;

	@Override
	public String getName()
	{
		return name().toLowerCase();
	}

	@Override
	public List<CustomType> getFields()
	{
		return null;
	}

	@Override
	public List<CustomMethod> getMethods()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
}
