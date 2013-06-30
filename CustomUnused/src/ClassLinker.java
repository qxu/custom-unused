import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassLinker
{
	private Map<Class<?>, Set<Class<?>>> linkMap;
	
	public ClassLinker()
	{
		this.linkMap = new HashMap<>();
	}
	
	public boolean containsLink(Class<?> src, Class<?> link)
	{
		Set<Class<?>> links = linkMap.get(src);
		return links == null ? false : links.contains(link);
	}
	
	public boolean containsSrcClass(Class<?> src)
	{
		return linkMap.containsKey(src);
	}
	
	public boolean addLink(Class<?> src, Class<?> link)
	{
		Set<Class<?>> links = linkMap.get(src);
		if(links == null)
		{
			links = new HashSet<>();
			linkMap.put(src, links);
		}
		return links.add(link);
	}
	
	public Set<Class<?>> getLinks(Class<?> src)
	{
		Set<Class<?>> links = linkMap.get(src);
		return links == null
				? new HashSet<Class<?>>()
				: new HashSet<Class<?>>(links);
	}
	
	public Set<Class<?>> removeSrcClass(Class<?> src)
	{
		Set<Class<?>> removedLinks = linkMap.remove(src);
		return removedLinks;
	}
	
	public boolean removeLink(Class<?> src, Class<?> link)
	{
		Set<Class<?>> links = linkMap.get(src);
		return links == null ? false : links.remove(link);
	}
	
	public Set<Class<?>> getAllClasses()
	{
		Set<Class<?>> allClasses = new HashSet<>();
		allClasses.addAll(getSrcClasses());
		allClasses.addAll(getLinkedClasses());
		return allClasses;
	}
	
	public Set<Class<?>> getSrcClasses()
	{
		return new HashSet<>(linkMap.keySet());
	}
	
	public Set<Class<?>> getLinkedClasses()
	{
		Set<Class<?>> linkedClasses = new HashSet<>();
		for(Set<Class<?>> linkSet : linkMap.values())
		{
			linkedClasses.addAll(linkSet);
		}
		return linkedClasses;
	}
	
	public Set<Map.Entry<Class<?>, Set<Class<?>>>> getLinkEntries()
	{
		return new HashSet<>(linkMap.entrySet());
	}
}
