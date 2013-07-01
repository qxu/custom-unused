package custom;

import java.util.List;

public interface CustomType
{
	String getName();
	List<CustomType> getFields();
	List<CustomMethod> getMethods();
}
