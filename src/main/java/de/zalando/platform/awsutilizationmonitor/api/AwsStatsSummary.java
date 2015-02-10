/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.api;

import java.util.TreeMap;

/**
 * @author jloeffler
 *
 */
public class AwsStatsSummary {
	private int accounts;
	private int apps;
	private int ec2Instances;
	private TreeMap<String, Integer> instancesByType = new TreeMap<String, Integer>();
	private int regions;
	private int resources;
	private TreeMap<AwsResourceType, Integer> resourcesByType = new TreeMap<AwsResourceType, Integer>();
	private int resourceTypes;
	private long s3DataSizeInGb;
	private long s3Objects;
	private int teams;

	/**
	 * @return the accounts
	 */
	public int getAccounts() {
		return accounts;
	}

	/**
	 * @return the apps
	 */
	public int getApps() {
		return apps;
	}

	/**
	 * @return the ec2Instances
	 */
	public int getEc2Instances() {
		return ec2Instances;
	}

	/**
	 * @return the instancesByType
	 */
	public TreeMap<String, Integer> getInstancesByType() {
		return instancesByType;
	}

	/**
	 * @return the regions
	 */
	public int getRegions() {
		return regions;
	}

	/**
	 * @return the resources
	 */
	public int getResources() {
		return resources;
	}

	/**
	 * @return the resourcesByType
	 */
	public TreeMap<AwsResourceType, Integer> getResourcesByType() {
		return resourcesByType;
	}

	/**
	 * @return the resourceTypes
	 */
	public int getResourceTypes() {
		return resourceTypes;
	}

	/**
	 * @return the s3DataSizeInGb
	 */
	public long getS3DataSizeInGb() {
		return s3DataSizeInGb;
	}

	/**
	 * @return the s3Objects
	 */
	public long getS3Objects() {
		return s3Objects;
	}

	/**
	 * @return the teams
	 */
	public int getTeams() {
		return teams;
	}

	/**
	 * @param accounts
	 *            the accounts to set
	 */
	public void setAccounts(int accounts) {
		this.accounts = accounts;
	}

	/**
	 * @param apps
	 *            the apps to set
	 */
	public void setApps(int apps) {
		this.apps = apps;
	}

	/**
	 * @param ec2Instances
	 *            the ec2Instances to set
	 */
	public void setEc2Instances(int ec2Instances) {
		this.ec2Instances = ec2Instances;
	}

	/**
	 * @param instancesByType
	 *            the instancesByType to set
	 */
	public void setInstancesByType(TreeMap<String, Integer> instancesByType) {
		this.instancesByType = instancesByType;
	}

	/**
	 * @param regions
	 *            the regions to set
	 */
	public void setRegions(int regions) {
		this.regions = regions;
	}

	/**
	 * @param resources
	 *            the resources to set
	 */
	public void setResources(int resources) {
		this.resources = resources;
	}

	/**
	 * @param resourcesByType
	 *            the resourcesByType to set
	 */
	public void setResourcesByType(TreeMap<AwsResourceType, Integer> resourcesByType) {
		this.resourcesByType = resourcesByType;
	}

	/**
	 * @param resourceTypes
	 *            the resourceTypes to set
	 */
	public void setResourceTypes(int resourceTypes) {
		this.resourceTypes = resourceTypes;
	}

	/**
	 * @param s3DataSizeInGb
	 *            the s3DataSizeInGb to set
	 */
	public void setS3DataSizeInGb(long s3DataSizeInGb) {
		this.s3DataSizeInGb = s3DataSizeInGb;
	}

	/**
	 * @param s3Objects
	 *            the s3Objects to set
	 */
	public void setS3Objects(long s3Objects) {
		this.s3Objects = s3Objects;
	}

	/**
	 * @param teams
	 *            the teams to set
	 */
	public void setTeams(int teams) {
		this.teams = teams;
	}
}
