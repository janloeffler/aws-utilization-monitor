package de.zalando.platform.awsutilizationmonitor.api;

import java.util.Hashtable;

import com.amazonaws.regions.Regions;

public class AwsResource extends Hashtable<String, String> implements Comparable<AwsResource> {
   
	private static final long serialVersionUID = 1L;
	private String name = "";
    private String owner = "";
    private AwsResourceType resourceType = AwsResourceType.Unknown;
    private Regions region = Regions.DEFAULT_REGION;
        
    public AwsResource (String name, String owner, AwsResourceType resourceType) { 
        this (name, owner, resourceType, Regions.DEFAULT_REGION);
    }

	public AwsResource (String name, String owner, AwsResourceType resourceType, Regions region) {
        setName(name);
        setOwner(owner);
        setResourceType(resourceType);
        setRegion(region);
    }
    
	/**
	 * @param key the key to set
	 * @param value the value to set
	 */
	public void addInfo(String key, String value) {
		this.put(key, value);
	}
	
	/**
	 * @param pattern the pattern to search
	 * @return true if pattern is found in one of the values
	 */
	public boolean containsPattern(String pattern) {
		pattern = pattern.toLowerCase();
		
		for (String s : this.values()) {
			if ((s != null) && s.toLowerCase().contains(pattern))
				return true;
		}
		
		return false;
	}

	/**
	 * @return the name of an EC2 based app
	 */
	public String getAppName() {
		if ((resourceType == AwsResourceType.EC2)
				&& this.containsKey("Name")) {
			return RemoveVersionNumber(this.get("Name"));
		} else {		
			return null;
		}
	}

	/**
	 * @param appName Name to convert into app name by removing version information.
	 * @return the app name without version numbers.
	 */
	public static String RemoveVersionNumber(String appName) {
		appName = appName.replace("SNAPSHOT", "").replace("snapshot", "");

		// app-zalanda-0.14 -> app-zalanda
		String removeChars = "0123456789-._ ";
		int i = appName.length();
		
		while ((i > 0) && removeChars.contains(String.valueOf(appName.charAt(i-1)))) {
			i--;
		}
		
		if (i < appName.length()) {
			return appName.substring(0, i); 
		}
		
		return appName;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		if (name == null)
			return "";
		else 
			return name;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		if (owner == null)
			return "";
		else 
			return owner;
	}

	/**
	 * @return the region
	 */
	public Regions getRegion() {
		return region;
	}

	/**
	 * @return the resourceType
	 */
	public AwsResourceType getResourceType() {
		return resourceType;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if (name == null)
			name = "";
		
		this.name = name;
		this.put("Name", name);
	}

    /**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		if (owner == null)
			owner = "";

		this.owner = owner;
		this.put("Owner", owner);
	}

    /**
	 * @param region the region to set
	 */
	public void setRegion(Regions region) {
		this.region = region;
		this.put("Region", region.getName());
	}
    
    /**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(AwsResourceType resourceType) {
		this.resourceType = resourceType;
		this.put("ResourceType", resourceType.name());
	}
	
	@Override
	public int compareTo(AwsResource res) {
		if (this.getResourceType() == res.getResourceType()) {
			return this.getName().compareTo(res.getName());
		} else {
			return this.getResourceType().compareTo(res.getResourceType());
		}
	}	
}
