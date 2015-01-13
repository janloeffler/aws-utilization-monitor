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

	@Test
	public void testClearData() {		
		AwsStats stats = new AwsStats();
		
		stats.generateSampleData(15);
		stats.clear();
		
		assertTrue(stats.getAllResources().length == 0);
	}

	@Test
	public void testGenerateSampleData() {		
		AwsStats stats = new AwsStats();
		
		stats.generateSampleData(15);
		
		assertTrue(stats.getAllResources().length >= 15);
	}

	@Test
	public void testGetOwnersData() {		
		AwsResource res = new AwsResource("Testname", "Testowner", AwsResourceType.Redshift, Regions.EU_WEST_1);
		AwsStats stats = new AwsStats();		
		stats.add(res);
		
		assertTrue(stats.getOwners().length == 1);
		assertTrue(stats.getOwners()[0] == res.getOwner());
	}

	@Test
	public void testInsertData() {		
		AwsResource res = new AwsResource("Testname", "Testowner", AwsResourceType.Redshift, Regions.EU_WEST_1);
		AwsStats stats = new AwsStats();		
		stats.add(res);
		
		assertTrue(stats.getAllResources().length == 1);
		
		AwsResource res2 = stats.getAllResources()[0];
		
		assertSame(res, res2);
	}

	@Test
	public void testSearchData() {		
		AwsStats stats = new AwsStats();
		
		stats.generateSampleData(100);
		
		assertTrue(stats.getAllResources().length > 10);
		assertTrue(stats.searchResources("FeedTheWorld").length > 0);
		assertTrue(stats.searchResources("Mickey Mouse").length > 0);
		assertTrue(stats.searchResources("bucket_").length > 0);
		assertTrue(stats.searchResources(AwsResourceType.ElasticTranscoder.toString()).length > 0);		
	}
}
