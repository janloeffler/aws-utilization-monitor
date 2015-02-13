package de.zalando.platform.awsutilizationmonitor.api;

import java.util.TreeMap;

import com.amazonaws.regions.Regions;

public class AwsResource extends TreeMap<String, Object> implements Comparable<AwsResource> {

	private static final long serialVersionUID = 1L;

	/**
	 * @param appName
	 *            Name to convert into app name by removing version information.
	 * @return the app name without version numbers.
	 */
	public static String RemoveVersionNumber(String appName) {
		appName = appName.replace("SNAPSHOT", "").replace("snapshot", "");
		appName = appName.replace("-instance", "");

		// app-zalanda-0.14 -> app-zalanda
		String removeChars = "0123456789-._ ";
		int i = appName.length();

		while ((i > 0) && removeChars.contains(String.valueOf(appName.charAt(i - 1)))) {
			i--;
		}

		if (i < appName.length())
			return appName.substring(0, i);

		return appName;
	}

	private String accountId = "";
	private String name = "";
	private Regions region = Regions.DEFAULT_REGION;
	private AwsResourceType resourceType = AwsResourceType.Unknown;

	/**
	 * @param name
	 * @param accountId
	 * @param resourceType
	 * @param region
	 */
	public AwsResource(String name, String accountId, AwsResourceType resourceType, Regions region) {
		setName(name);
		setAccountId(accountId);
		setResourceType(resourceType);
		setRegion(region);
	}

	/**
	 * @param key
	 *            the key to set
	 * @param value
	 *            the value to set
	 */
	public void addInfo(AwsTag key, Object value) {
		this.put(key.toString(), value);
	}

	/**
	 * @param key
	 *            the key to set
	 * @param value
	 *            the value to set
	 */
	public void addInfo(String key, Object value) {
		this.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AwsResource res) {
		if (this.getResourceType() == res.getResourceType())
			return this.getName().compareTo(res.getName());
		else
			return this.getResourceType().compareTo(res.getResourceType());
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean containsKey(AwsTag key) {
		return containsKey(key.toString());
	}

	/**
	 * @param pattern
	 *            the pattern to search
	 * @return true if pattern is found in one of the values
	 */
	public boolean containsPattern(String pattern) {
		pattern = pattern.toLowerCase();

		for (Object s : this.values()) {
			if ((s != null) && s.toString().toLowerCase().contains(pattern))
				return true;
		}

		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public Object get(AwsTag key) {
		return this.get(key.toString());
	}

	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		if (accountId == null)
			return "";
		else
			return accountId;
	}

	/**
	 * @return the name of an EC2 based app
	 */
	public String getAppName() {
		if ((resourceType == AwsResourceType.EC2) && this.containsKey(AwsTag.Name))
			return RemoveVersionNumber((String) this.get(AwsTag.Name));
		else
			return null;
	}

	/**
	 * @return the instance type of an EC2 based app
	 */
	public String getEC2InstanceType() {
		if ((resourceType == AwsResourceType.EC2) && this.containsKey(AwsTag.InstanceType))
			return (String) this.get(AwsTag.InstanceType);
		else
			return null;
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
	 * @return the team
	 */
	public String getTeam() {
		if (this.containsKey(AwsTag.Team))
			return (String) this.get(AwsTag.Team);
		else
			return null;
	}

	/**
	 * @param accountId
	 *            the accountId to set
	 */
	public void setAccountId(String accountId) {
		if (accountId == null) {
			accountId = "";
		}

		this.accountId = accountId;
		this.addInfo(AwsTag.AccountId, accountId);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		if (name == null) {
			name = "";
		}

		this.name = name;
		this.addInfo(AwsTag.Name, name);
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(Regions region) {
		this.region = region;
		this.addInfo(AwsTag.Region, region.getName());
	}

	/**
	 * @param resourceType
	 *            the resourceType to set
	 */
	public void setResourceType(AwsResourceType resourceType) {
		if (resourceType == null) {
			resourceType = AwsResourceType.Unknown;
		}

		this.resourceType = resourceType;
		this.addInfo(AwsTag.ResourceType, resourceType.name());
	}

	/**
	 * @param team
	 *            the team to set
	 */
	public void setTeam(String team) {
		if (team == null) {
			team = "";
		}

		this.addInfo(AwsTag.Team, team);
	}
}
