package custom;


public abstract class AbstractCustomType implements CustomType
{
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("ClassFactory: " + getName());
		for(CustomType t : getFields())
		{
			sb.append("\n\t" + t.getName());
		}
		for(CustomMethod m : getMethods())
		{
			sb.append("\n\t" + m.toString().replace("\n", "\n\t"));
		}
		return sb.toString();
	}
}
