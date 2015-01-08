package de.zalando.platform.awsutilizationmonitor.api;

public class AwsResource {
    
    private String name = "";
    private String owner = "";
    private AwsResourceType resourceType = AwsResourceType.Unknown;
    private String region = "";
    private String info = "";
        
    public AwsResource (String name, String owner, AwsResourceType resourceType) { 
        this (name, owner, resourceType, "");
    }

	public AwsResource (String name, String owner, AwsResourceType resourceType, String region) {
        this (name, owner, resourceType, region, "");    
    }

	public AwsResource (String name, String owner, AwsResourceType resourceType, String region, String info) {
        this.name = name;
        this.owner = owner;
        this.resourceType = resourceType;
        this.region = region;
        this.info = info;
    }

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @return the resourceType
	 */
	public AwsResourceType getResourceType() {
		return resourceType;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

    /**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

    /**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}
    
    /**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(AwsResourceType resourceType) {
		this.resourceType = resourceType;
	}
}
