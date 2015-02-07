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
	private TreeMap<String, Integer> instancesByType = new TreeMap<String, Integer>();
	private int regions;
	private int resources;
	private TreeMap<AwsResourceType, Integer> resourcesByType = new TreeMap<AwsResourceType, Integer>();
	private int resourceTypes;
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
	 * @param teams
	 *            the teams to set
	 */
	public void setTeams(int teams) {
		this.teams = teams;
	}
}
