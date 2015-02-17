/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.amazonaws.regions.Regions;

import de.zalando.platform.awsutilizationmonitor.stats.AwsResource;
import de.zalando.platform.awsutilizationmonitor.stats.AwsResourceType;
import de.zalando.platform.awsutilizationmonitor.stats.AwsStats;

/**
 * @author jloeffler
 *
 */
public class AwsStatsTest {

	private static AwsResource defaultResource = new AwsResource("Testname", "TestAccountId", AwsResourceType.EC2, Regions.EU_WEST_1);

	@Test
	public void testClearData() {
		AwsStats stats = new AwsStats();
		stats.generateSampleData(15);
		stats.clear();

		assertTrue(stats.getItemCount() == 0);
	}

	@Test
	public void testGenerateSampleData() {
		AwsStats stats = new AwsStats();
		stats.generateSampleData(15);

		assertTrue(stats.getItemCount() >= 15);
	}

	@Test
	public void testGetAccounts() {
		AwsStats stats = new AwsStats();
		AwsResource res = defaultResource;
		stats.add(res);

		assertTrue(stats.getAccounts().length == 1);
		assertTrue(stats.getAccounts()[0] == res.getAccountId());
	}

	@Test
	public void testGetAppInstances() {
		AwsStats stats = new AwsStats();

		int max = 5;
		String appName = "my-app";

		for (int i = 0; i < max; i++) {
			AwsResource res = (AwsResource) defaultResource.clone();
			res.setName(appName + "-1." + i);
			stats.add(res);
		}

		assertTrue(stats.getItemCount() == max);
		assertTrue(stats.getApps().length == 1);
		assertTrue(stats.getApps()[0].equalsIgnoreCase(appName));
		assertTrue(stats.getAppInstances(appName).length == max);
	}

	@Test
	public void testGetRegions() {
		AwsStats stats = new AwsStats();
		AwsResource res = defaultResource;
		stats.add(res);

		assertTrue(stats.getRegions().length == 1);
		assertTrue(stats.getRegions()[0] == res.getRegion());
	}

	@Test
	public void testGetResource() {
		AwsStats stats = new AwsStats();

		int max = 5;
		String appName = "my-app";

		for (int i = 0; i < max; i++) {
			AwsResource res = (AwsResource) defaultResource.clone();
			res.setName(appName + "-1." + i);
			stats.add(res);
		}

		assertTrue(stats.getItemCount() == max);

		for (int i = 0; i < max; i++) {
			assertTrue(stats.getResource(appName + "-1." + i) != null);
			assertTrue(stats.getResource(appName + "-1." + i).getName().equalsIgnoreCase(appName + "-1." + i));
		}
	}

	@Test
	public void testGetResourceTypes() {
		AwsStats stats = new AwsStats();
		AwsResource res = defaultResource;
		stats.add(res);

		assertTrue(stats.getUsedResourceTypes().length == 1);
		assertTrue(stats.getUsedResourceTypes()[0] == res.getResourceType());
	}

	@Test
	public void testGetSummary() {
		AwsStats stats = new AwsStats();

		assertTrue(stats.getItemCount() == stats.getSummary().getResources());
		assertTrue(stats.getSummary().getApps() == 0);
		assertTrue(stats.getSummary().getResources() == 0);

		stats.generateSampleData(100);

		assertTrue(stats.getSummary().getApps() > 0);
		assertTrue(stats.getSummary().getResources() > 10);
		assertTrue(stats.getApps().length == stats.getSummary().getApps());
		assertTrue(stats.getAccounts().length == stats.getSummary().getAccounts());
		assertTrue(stats.getRegions().length == stats.getSummary().getRegions());
		assertTrue(stats.getItemCount() == stats.getSummary().getResources());
		assertTrue(stats.getUsedResourceTypes().length == stats.getSummary().getResourceTypes());
		assertTrue(stats.getTeams().length == stats.getSummary().getTeams());
	}

	@Test
	public void testGetTeams() {
		AwsStats stats = new AwsStats();

		int items = 20;
		for (int i = 0; i < items; i++) {
			AwsResource res = new AwsResource("res_" + i, "account_" + i, AwsResourceType.EC2, Regions.EU_WEST_1);
			res.addInfo("Team", "team_" + i);
			stats.add(res);
		}

		assertTrue(stats.getItemCount() == items);
		assertTrue(stats.getTeams().length == items);
		assertTrue(stats.getValues("Team").length == items);
		assertTrue(stats.searchResources("team_1").length > 0);
	}

	@Test
	public void testInsertData() {
		AwsStats stats = new AwsStats();
		AwsResource res = defaultResource;
		stats.add(res);
		stats.add(null);

		assertTrue(stats.getItemCount() == 1);

		AwsResource res2 = stats.getResources()[0];

		assertSame(res, res2);
	}

	@Test
	public void testRemoveVersionNumber() {
		assertTrue(AwsResource.RemoveVersionNumber("").equals(""));
		assertTrue(AwsResource.RemoveVersionNumber("myApp").equals("myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("myApp1.0").equals("myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("myApp-1.0").equals("myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("myApp-1.0-SNAPSHOT").equals("myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("myApp1.0.SNAPSHOT").equals("myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("myApp1.0.2.100").equals("myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("myApp-1.0.0.444").equals("myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("123myApp").equals("123myApp"));
		assertTrue(AwsResource.RemoveVersionNumber("123-myApp-2").equals("123-myApp"));
	}

	@Test
	public void testSearch() {
		AwsStats stats = new AwsStats();

		stats.generateSampleData(100);

		assertTrue(stats.getItemCount() > 10);
		assertTrue(stats.searchResources("FeedTheWorld").length > 0);
		assertTrue(stats.searchResources("Mickey Mouse").length > 0);
		assertTrue(stats.searchResources("bucket_").length > 0);
		assertTrue(stats.searchResources(AwsResourceType.ElasticTranscoder.toString()).length > 0);
	}
}
