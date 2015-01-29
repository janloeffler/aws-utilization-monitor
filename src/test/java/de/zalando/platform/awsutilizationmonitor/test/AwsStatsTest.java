/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.amazonaws.regions.Regions;

import de.zalando.platform.awsutilizationmonitor.api.AwsResource;
import de.zalando.platform.awsutilizationmonitor.api.AwsResourceType;
import de.zalando.platform.awsutilizationmonitor.api.AwsStats;

/**
 * @author jloeffler
 *
 */
public class AwsStatsTest {

	private static AwsResource defaultResource = new AwsResource("Testname", "Testowner", AwsResourceType.Redshift, Regions.EU_WEST_1);

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
	public void testGetOwners() {
		AwsResource res = defaultResource;
		AwsStats stats = new AwsStats();
		stats.add(res);

		assertTrue(stats.getOwners().length == 1);
		assertTrue(stats.getOwners()[0] == res.getOwner());
	}

	@Test
	public void testGetRegions() {
		AwsResource res = defaultResource;
		AwsStats stats = new AwsStats();
		stats.add(res);

		assertTrue(stats.getRegions().length == 1);
		assertTrue(stats.getRegions()[0] == res.getRegion());
	}

	@Test
	public void testGetResourceTypes() {
		AwsResource res = defaultResource;
		AwsStats stats = new AwsStats();
		stats.add(res);

		assertTrue(stats.getUsedResourceTypes().length == 1);
		assertTrue(stats.getUsedResourceTypes()[0] == res.getResourceType());
	}

	@Test
	public void testInsertData() {
		AwsResource res = defaultResource;
		AwsStats stats = new AwsStats();
		stats.add(res);

		assertTrue(stats.getItemCount() == 1);

		AwsResource res2 = stats.getAllResources()[0];

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
