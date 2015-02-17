/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;

import de.zalando.platform.awsutilizationmonitor.api.AwsAccount;
import de.zalando.platform.awsutilizationmonitor.stats.AwsResourceType;
import de.zalando.platform.awsutilizationmonitor.stats.AwsStats;

/**
 * @author jloeffler
 *
 */
public class AwsCollectorThread extends Thread {
	public static final Logger LOG = LoggerFactory.getLogger(AwsCollectorThread.class);

	private AwsAccount account;
	private Regions region;
	private AwsResourceType resourceType;
	private AwsStats stats;

	/**
	 * @param stats
	 * @param credentials
	 * @param region
	 * @param resourceType
	 */
	public AwsCollectorThread(AwsStats stats, AwsAccount account, Regions region, AwsResourceType resourceType) {
		this.stats = stats;
		this.account = account;
		this.region = region;
		this.resourceType = resourceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			LOG.debug("Start thread for " + resourceType + " in region " + region.getName() + " in account " + account.getAccountId());
			AwsStatsCollector.scanResources(stats, account, region, resourceType);
		} catch (Exception e) {
			LOG.error("Exception in thread for " + resourceType + " in region " + region.getName() + " in account " + account.getAccountId() + ": "
					+ e.getMessage());
		}
	}
}
