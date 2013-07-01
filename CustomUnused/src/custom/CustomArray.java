package custom;

import java.util.Collections;
import java.util.List;

public class CustomArray extends CustomClass
{
	private static final List<CustomType> fields = Collections.emptyList();
	private static final List<CustomMethod> methods = Collections.emptyList();

	public CustomArray(CustomClass wrap)
	{
		super(wrap.getName() + "[]", fields, methods);
	}
}
