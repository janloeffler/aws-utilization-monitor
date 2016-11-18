/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.stats;

import java.text.DecimalFormat;
import java.util.TreeMap;

/**
 * @author jloeffler
 *
 */
public class AwsStatsSummary {
	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] descriptions = new String[] { "B", "kB", "MB", "GB", "TB" };
		int sizeType = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, sizeType)) + " " + descriptions[sizeType];
	}

	public static String readableLong(long number) {
		return new DecimalFormat("###,###.###").format(number);
	}

	private int accounts;
	private TreeMap<String, Integer> amis = new TreeMap<String, Integer>();
	private int apps;
	private int ec2Instances;
	private TreeMap<String, Integer> instancesByType = new TreeMap<String, Integer>();
	private int regions;
	private int resources;
	private TreeMap<String, Integer> resourcesByAccount = new TreeMap<String, Integer>();
	private TreeMap<AwsResourceType, Integer> resourcesByType = new TreeMap<AwsResourceType, Integer>();

	private int resourceTypes;

	private long s3DataSizeInBytes;
	private long s3Objects;

	private int teams;

	/**
	 * @return the accounts
	 */
	public int getAccounts() {
		return accounts;
	}

	/**
	 * @return the amis
	 */
	public TreeMap<String, Integer> getAMIs() {
		return amis;
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
	 * @return the resourcesByAccount
	 */
	public TreeMap<String, Integer> getResourcesByAccount() {
		return resourcesByAccount;
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
	 * @return the s3DataSizeInBytes
	 */
	public long getS3DataSizeInBytes() {
		return s3DataSizeInBytes;
	}

	/**
	 * @return the s3DataSizeInGB
	 */
	public long getS3DataSizeInGB() {
		if (s3DataSizeInBytes > 0)
			return s3DataSizeInBytes / (1024 * 1024 * 1024);
		else
			return 0;
	}

	/**
	 * @return the s3DataSize as readable text
	 */
	public String getS3DataSizeText() {
		return readableFileSize(s3DataSizeInBytes);
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
	 * @param amis
	 *            the amis to set
	 */
	public void setAMIs(TreeMap<String, Integer> amis) {
		this.amis = amis;
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
	 * @param resourcesByAccount
	 *            the resourcesByAccount to set
	 */
	public void setResourcesByAccount(TreeMap<String, Integer> resourcesByAccount) {
		this.resourcesByAccount = resourcesByAccount;
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
	 * @param s3DataSizeInBytes
	 *            the s3DataSizeInBytes to set
	 */
	public void setS3DataSizeInBytes(long s3DataSizeInBytes) {
		this.s3DataSizeInBytes = s3DataSizeInBytes;
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
