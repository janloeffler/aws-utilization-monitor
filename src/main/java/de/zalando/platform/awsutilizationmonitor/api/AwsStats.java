package de.zalando.platform.awsutilizationmonitor.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.TreeMap;

import com.amazonaws.regions.Regions;

/**
 * @author jloeffler
 *
 */
public class AwsStats {

	private ArrayList<AwsResource> resources = new ArrayList<AwsResource>();

	/**
	 * Add a new resource to statistics set.
	 *
	 * @param res
	 *            AWS resource to be added to statistics result set.
	 */
	public void add(AwsResource res) {
		if ((res != null) && !resources.contains(res)) {
			resources.add(res);
		}
	}

	/**
	 * Remove all collected information about resources.
	 */
	public void clear() {
		resources.clear();
	}

	/**
	 * Generate some sample data to test application.
	 *
	 * @param maxItems
	 *            amount of test items inserted in collection.
	 */
	public void generateSampleData(int maxItems) {
		String[] appNames = new String[] { "FeedTheWorld", "EmpireStrikesBack", "Imperator", "HelloWorld", "ShoppingCart", "AwsChecker" };
		String[] accounts = new String[] { "Mickey Mouse", "Max Mustermann", "Ironman", "Hackweek", "Superman", "Mr. Bond", "Overlord" };
		Regions[] regions = new Regions[] { Regions.EU_WEST_1, Regions.EU_CENTRAL_1, Regions.US_EAST_1 };

		Hashtable<AwsResourceType, String> prefixes = new Hashtable<AwsResourceType, String>();
		prefixes.put(AwsResourceType.EC2, "app_");
		prefixes.put(AwsResourceType.SimpleDB, "db_");
		prefixes.put(AwsResourceType.RDS, "db_");
		prefixes.put(AwsResourceType.S3, "bucket_");
		prefixes.put(AwsResourceType.DynamoDB, "db_");
		prefixes.put(AwsResourceType.ElastiCache, "cache_");

		Random r = new Random();

		for (int i = 0; i < maxItems; i++) {
			AwsResourceType resourceType = AwsResourceType.values()[r.nextInt(AwsResourceType.values().length - 1)];
			String prefix = "";
			if (prefixes.containsKey(resourceType)) {
				prefix = prefixes.get(resourceType);
			}
			String appName = prefix + appNames[r.nextInt(appNames.length - 1)];
			String account = accounts[r.nextInt(accounts.length - 1)];
			Regions region = regions[r.nextInt(regions.length - 1)];

			add(new AwsResource(appName, account, resourceType, region));
		}
	}

	/**
	 * Returns all accounts sorted alphabetically.
	 *
	 * @return sorted list of all accounts.
	 */
	public String[] getAccounts() {
		ArrayList<String> results = new ArrayList<String>();

		for (AwsResource res : resources) {
			if (!results.contains(res.getAccountId())) {
				results.add(res.getAccountId());
			}
		}

		results.sort(null);

		return results.toArray(new String[results.size()]);
	}

	/**
	 * Get the EC2 instances of the specified app.
	 *
	 * @param appName
	 *            Name of the app
	 * @return EC2 instances that belong to the specified app.
	 */
	public AwsResource[] getAppInstances(String appName) {
		AwsResource[] list = getResources(AwsResourceType.EC2);
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();

		for (AwsResource res : list) {
			String appName2 = res.getAppName();
			if ((appName2 != null) && appName2.equalsIgnoreCase(appName)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}

	/**
	 * Returns all app names sorted alphabetically.
	 *
	 * @return sorted list of all app names.
	 */
	public String[] getApps() {
		AwsResource[] list = getResources(AwsResourceType.EC2);
		ArrayList<String> results = new ArrayList<String>();

		for (AwsResource res : list) {
			String appName = res.getAppName();
			if ((appName != null) && (appName.length() > 0) && !results.contains(appName)) {
				results.add(appName);
			}
		}

		results.sort(null);

		return results.toArray(new String[results.size()]);
	}

	/**
	 * @return amount of resource items.
	 */
	public int getItemCount() {
		return resources.size();
	}

	/**
	 * Returns all keys sorted alphabetically.
	 *
	 * @return sorted list of all keys.
	 */
	public String[] getKeys() {
		ArrayList<String> results = new ArrayList<String>();

		for (AwsResource res : resources) {
			for (String key : res.keySet()) {
				if (!results.contains(key)) {
					results.add(key);
				}
			}
		}

		results.sort(null);

		return results.toArray(new String[results.size()]);
	}

	/**
	 * Returns all app names sorted alphabetically that are externally reachable
	 * via public dns name.
	 *
	 * @return sorted list of all app names that are externally reachable via
	 *         public dns name.
	 */
	public String[] getPublicApps() {
		AwsResource[] list = getResources(AwsResourceType.EC2);
		ArrayList<String> results = new ArrayList<String>();

		for (AwsResource res : list) {
			String appName = res.getAppName();
			if ((appName != null) && (appName.length() > 0) && !results.contains(appName) && res.containsKey(AwsTag.PublicDnsName)
					&& (res.get(AwsTag.PublicDnsName) != null) && (res.get(AwsTag.PublicDnsName).toString().length() > 0)) {
				results.add(appName);
			}
		}

		results.sort(null);

		return results.toArray(new String[results.size()]);
	}

	/**
	 * Returns all regions sorted alphabetically.
	 *
	 * @return sorted list of all regions.
	 */
	public Regions[] getRegions() {
		ArrayList<Regions> results = new ArrayList<Regions>();

		for (AwsResource res : resources) {
			if (!results.contains(res.getRegion())) {
				results.add(res.getRegion());
			}
		}

		results.sort(null);

		return results.toArray(new Regions[results.size()]);
	}

	/**
	 * Get the resource with the specified resource name.
	 *
	 * @param resourceName
	 *            Name of the resource that should be returned
	 * @return Resource that matches the specified resource name. Returns null
	 *         if resource cannot be found.
	 */
	public AwsResource getResource(String resourceName) {
		for (AwsResource res : resources) {
			if (res.getName().equalsIgnoreCase(resourceName))
				return res;
		}

		return null;
	}

	/**
	 * @return all resources in one list.
	 */
	public AwsResource[] getResources() {
		resources.sort(null);

		return resources.toArray(new AwsResource[resources.size()]);
	}

	/**
	 * @return all resources in one list for a given resource type.
	 *
	 * @param resourceType
	 *            resourceType e.g. EC2, S3, SimpleDB
	 */
	public AwsResource[] getResources(AwsResourceType resourceType) {
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();

		for (AwsResource res : resources) {
			if (res.getResourceType().equals(resourceType)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}

	/**
	 * Get the resources of the specified account.
	 *
	 * @param accountName
	 *            Name of the account
	 * @return Resources that belong to the specified account.
	 */
	public AwsResource[] getResourcesByAccount(String accountName) {
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();

		for (AwsResource res : resources) {
			if (res.getAccountId().equalsIgnoreCase(accountName)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}

	/**
	 * Get the resources of the specified EC2 instance type.
	 *
	 * @param instanceType
	 *            Instance type of the EC2 based app
	 * @return Resources that have a specified EC2 instance type.
	 */
	public AwsResource[] getResourcesByEC2InstanceType(String instanceType) {
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();

		for (AwsResource res : resources) {
			String type = res.getEC2InstanceType();
			if ((type != null) && type.equalsIgnoreCase(instanceType)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}

	/**
	 * Get the resources of the specified region.
	 *
	 * @param region
	 *            Name of the region e.g. "eu-west-1"
	 * @return Resources that belong to the specified region.
	 */
	public AwsResource[] getResourcesByRegion(Regions region) {
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();

		for (AwsResource res : resources) {
			if (res.getRegion().equals(region)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}

	/**
	 * Get the resources of the specified team.
	 *
	 * @param teamName
	 *            Name of the team
	 * @return Resources that belong to the specified team.
	 */
	public AwsResource[] getResourcesByTeam(String teamName) {
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();

		for (AwsResource res : resources) {
			if ((res.getTeam() != null) && res.getTeam().equalsIgnoreCase(teamName)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}

	/**
	 * @return all resources in one list grouped by AccountId, ResourceType each
	 *         containing a list of resources.
	 */
	public Hashtable<String, Hashtable<AwsResourceType, ArrayList<AwsResource>>> getResourceTree() {
		Hashtable<String, Hashtable<AwsResourceType, ArrayList<AwsResource>>> results = new Hashtable<String, Hashtable<AwsResourceType, ArrayList<AwsResource>>>();

		for (AwsResource res : resources) {
			Hashtable<AwsResourceType, ArrayList<AwsResource>> resourcesByAccount;
			if (results.containsKey(res.getAccountId())) {
				resourcesByAccount = results.get(res.getAccountId());
			} else {
				resourcesByAccount = new Hashtable<AwsResourceType, ArrayList<AwsResource>>();
				results.put(res.getAccountId(), resourcesByAccount);
			}

			ArrayList<AwsResource> resourcesByResourceType;
			if (resourcesByAccount.containsKey(res.getResourceType())) {
				resourcesByResourceType = resourcesByAccount.get(res.getResourceType());
			} else {
				resourcesByResourceType = new ArrayList<AwsResource>();
				resourcesByAccount.put(res.getResourceType(), resourcesByResourceType);
			}

			resourcesByResourceType.add(res);
		}

		return results;
	}

	/**
	 * Returns statistics as summary.
	 *
	 * @return statistic summary.
	 */
	public AwsStatsSummary getSummary() {
		AwsStatsSummary summary = new AwsStatsSummary();

		summary.setResources(resources.size());
		summary.setAccounts(getAccounts().length);
		summary.setRegions(getRegions().length);
		summary.setResourceTypes(getUsedResourceTypes().length);
		summary.setTeams(getTeams().length);
		summary.setApps(getApps().length);
		summary.setEc2Instances(getResources(AwsResourceType.EC2).length);

		long s3Objects = 0;
		long s3DataSizeInBytes = 0;
		long s3DataSizeInGb = 0;

		for (AwsResource res : getResources(AwsResourceType.S3)) {
			if (res.containsKey(AwsTag.Objects)) {
				s3Objects += (long) res.get(AwsTag.Objects);
			}

			if (res.containsKey(AwsTag.SizeInBytes)) {
				s3DataSizeInBytes += (long) res.get(AwsTag.SizeInBytes);
			}
		}

		if (s3DataSizeInBytes > 0) {
			s3DataSizeInGb = s3DataSizeInBytes / 1024 / 1024;
		}
		summary.setS3Objects(s3Objects);
		summary.setS3DataSizeInGb(s3DataSizeInGb);

		TreeMap<AwsResourceType, Integer> resourcesByType = new TreeMap<AwsResourceType, Integer>();
		for (AwsResourceType resourceType : getUsedResourceTypes()) {
			int amount = getResources(resourceType).length;
			resourcesByType.put(resourceType, amount);
		}
		summary.setResourcesByType(resourcesByType);

		TreeMap<String, Integer> instancesByType = new TreeMap<String, Integer>();
		for (String instanceType : getUsedEC2InstanceTypes()) {
			int amount = getResourcesByEC2InstanceType(instanceType).length;
			instancesByType.put(instanceType, amount);
		}
		summary.setInstancesByType(instancesByType);

		return summary;
	}

	/**
	 * Returns all teams sorted alphabetically.
	 *
	 * @return sorted list of all teams.
	 */
	public String[] getTeams() {
		String[] teams = (String[]) getValues("Team");
		Arrays.sort(teams);
		return teams;
	}

	/**
	 * Returns all used resource types (e.g. EC2, S3) of used resources.
	 *
	 * @return sorted list of all used resource types.
	 */
	public String[] getUsedEC2InstanceTypes() {
		ArrayList<String> results = new ArrayList<String>();

		for (AwsResource res : resources) {
			String instanceType = res.getEC2InstanceType();
			if ((instanceType != null) && (instanceType.length() > 0) && !results.contains(instanceType)) {
				results.add(instanceType);
			}
		}

		results.sort(null);

		return results.toArray(new String[results.size()]);
	}

	/**
	 * Returns all used resource types (e.g. EC2, S3) of used resources.
	 *
	 * @return sorted list of all used resource types.
	 */
	public AwsResourceType[] getUsedResourceTypes() {
		ArrayList<AwsResourceType> results = new ArrayList<AwsResourceType>();

		for (AwsResource res : resources) {
			if (!results.contains(res.getResourceType())) {
				results.add(res.getResourceType());
			}
		}

		results.sort(null);

		return results.toArray(new AwsResourceType[results.size()]);
	}

	/**
	 * Returns all values sorted alphabetically of the specified key.
	 *
	 * @param key
	 *            key to look for
	 * @return sorted list of all keys.
	 */
	public Object[] getValues(String key) {
		ArrayList<Object> results = new ArrayList<Object>();

		for (AwsResource res : resources) {
			if (res.containsKey(key)) {
				Object value = res.get(key);

				if ((value != null) && (value.toString().length() > 0) && !results.contains(value)) {
					results.add(value);
				}
			}
		}

		results.sort(null);

		return results.toArray(new String[results.size()]);
	}

	/**
	 * Searches all resources that match the specified pattern.
	 *
	 * @param searchPattern
	 *            pattern to search for
	 * @return list of all matching resources
	 */
	public AwsResource[] searchResources(String searchPattern) {
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();
		String s = searchPattern.toLowerCase();

		for (AwsResource res : resources) {
			if (res.containsPattern(s)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}

	/**
	 * Searches all resources that match the specified pattern.
	 *
	 * @param key
	 *            key to search for
	 * @param value
	 *            value to search for
	 * @return list of all matching resources
	 */
	public AwsResource[] searchResources(String key, String value) {
		ArrayList<AwsResource> results = new ArrayList<AwsResource>();

		for (AwsResource res : resources) {
			if (res.containsKey(key) && res.get(key).toString().equalsIgnoreCase(value)) {
				results.add(res);
			}
		}

		results.sort(null);

		return results.toArray(new AwsResource[results.size()]);
	}
}
