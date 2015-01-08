/**
 * 
 */
package de.zalando.platform.awsutilizationmonitor.test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
	public void testInsertData() {		
		AwsResource res = new AwsResource("Testname", "Testowner", AwsResourceType.Redshift, "EU-WEST-2", "Infotext");
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
		assertTrue(stats.searchResource("FeedTheWorld").length > 0);
		assertTrue(stats.searchResource("Mickey Mouse").length > 0);
		assertTrue(stats.searchResource("bucket_").length > 0);
		assertTrue(stats.searchResource(AwsResourceType.ElasticTranscoder.toString()).length > 0);		
	}
}
