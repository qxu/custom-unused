import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassNameLinker
{
	private Map<String, Set<String>> linkMap;
	
	public ClassNameLinker()
	{
		this.linkMap = new HashMap<>();
	}
	
	public boolean containsLink(String src, String link)
	{
		Set<String> links = linkMap.get(src);
		return links == null ? false : links.contains(link);
	}
	
	public boolean containsSrcClass(String src)
	{
		return linkMap.containsKey(src);
	}
	
	public boolean addLink(String src, String link)
	{
		Set<String> links = linkMap.get(src);
		if(links == null)
		{
			links = new HashSet<>();
			linkMap.put(src, links);
		}
		return links.add(link);
	}
	
	public Set<String> getLinks(String src)
	{
		Set<String> links = linkMap.get(src);
		return links == null
				? new HashSet<String>()
				: new HashSet<String>(links);
	}
	
	public Set<String> removeSrcClass(String src)
	{
		Set<String> removedLinks = linkMap.remove(src);
		return removedLinks;
	}
	
	public boolean removeLink(String src, String link)
	{
		Set<String> links = linkMap.get(src);
		return links == null ? false : links.remove(link);
	}
	
	public Set<String> getAllClasses()
	{
		Set<String> allClasses = new HashSet<>();
		allClasses.addAll(getSrcClasses());
		allClasses.addAll(getLinkedClasses());
		return allClasses;
	}
	
	public Set<String> getSrcClasses()
	{
		return new HashSet<>(linkMap.keySet());
	}
	
	public Set<String> getLinkedClasses()
	{
		Set<String> linkedClasses = new HashSet<>();
		for(Set<String> linkSet : linkMap.values())
		{
			linkedClasses.addAll(linkSet);
		}
		return linkedClasses;
	}
	
	public Set<Map.Entry<String, Set<String>>> getLinkEntries()
	{
		return new HashSet<>(linkMap.entrySet());
	}
}
