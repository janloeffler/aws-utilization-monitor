/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.collector;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;

import de.zalando.platform.awsutilizationmonitor.api.AwsAccount;
import de.zalando.platform.awsutilizationmonitor.config.Config;
import de.zalando.platform.awsutilizationmonitor.stats.AwsResourceType;
import de.zalando.platform.awsutilizationmonitor.stats.AwsStats;

/**
 * @author jloeffler
 *
 */
public class AwsCollectorThread extends Thread {
	public static final Logger LOG = LoggerFactory.getLogger(AwsCollectorThread.class);

	private ArrayList<AwsAccount> accounts;
	private Config config;
	private boolean isRunning = false;
	private AwsStats stats;

	/**
	 * @param stats
	 * @param supportedRegions
	 * @param ignoredComponents
	 */
	public AwsCollectorThread(AwsStats stats, ArrayList<AwsAccount> accounts, Config config) {
		this.stats = stats;
		this.accounts = accounts;
		this.config = config;
	}

	private boolean isAllowed(AwsResourceType res) {
		String allow = Arrays.toString(config.getAllowedComponents()).toLowerCase();
		String ignore = Arrays.toString(config.getIgnoredComponents()).toLowerCase();

		return !ignore.contains(res.toString().toLowerCase())
				&& ((allow.length() == 0) || allow.contains(res.toString().toLowerCase()));
	}

	public boolean isRunning() {
		return isRunning;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		LOG.info("----------------------------------------------------------------------");
		LOG.info("Login to AWS and collect resource list");
		LOG.info("----------------------------------------------------------------------");

		isRunning = true;
		DateTime startTime = DateTime.now();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		/*
		 * Configuration
		 */
		LOG.info("Supported regions: " + Arrays.toString(config.getSupportedRegions()));
		ArrayList<Regions> regions = new ArrayList<Regions>();
		for (String s : config.getSupportedRegions()) {
			regions.add(Regions.valueOf(s));
		}

		String allow = Arrays.toString(config.getAllowedComponents()).toLowerCase();
		String ignore = Arrays.toString(config.getIgnoredComponents()).toLowerCase();

		if (allow.length() > 0) {
			LOG.info("Allowed recources: " + Arrays.toString(config.getAllowedComponents()));
		}
		if (ignore.length() > 0) {
			LOG.info("Ignored recources: " + Arrays.toString(config.getIgnoredComponents()));
		}

		ArrayList<AwsResourceType> resourceTypes = new ArrayList<AwsResourceType>();
		for (AwsResourceType resourceType : AwsResourceType.values()) {

			// exclude S3 and scan it afterwards separately
			if ((resourceType != AwsResourceType.Unknown) && (resourceType != AwsResourceType.S3) && isAllowed(resourceType)) {
				resourceTypes.add(resourceType);
			}
		}

		for (AwsAccount account : accounts) {
			try {
				if (account.getAccountId().length() == 0) {
					account.retrieveAccountIdFromAwsAPI();
				}
				LOG.info("Current AWS account ID: " + account.getAccountId());

				/*
				 * scan S3 only once
				 */
				if (isAllowed(AwsResourceType.S3)) {
					AwsScanThread thread = new AwsScanThread(stats, account, Regions.EU_WEST_1, AwsResourceType.S3);
					threads.add(thread);
					thread.start();
				}

				/*
				 * scan each resource type in each region
				 */
				for (Regions region : regions) {
					for (AwsResourceType resourceType : resourceTypes) {
						AwsScanThread thread = new AwsScanThread(stats, account, region, resourceType);
						threads.add(thread);
						thread.start();
					}
				}
			} catch (Exception ex) {
				LOG.error("Connect to AWS failed: " + ex.getMessage());
			}
		}

		/*
		 * wait for all collection threads to be finished
		 */
		try {
			for (Thread t : threads) {
				t.join();
			}
		} catch (Exception e) {
			LOG.error("Thread error: " + e.getMessage());
		}

		isRunning = false;
		DateTime duration = DateTime.now().minus(startTime.getMillis());
		LOG.info("----------------------------------------------------------------------");
		LOG.info("Collected " + stats.getSummary().getResources() + " resources in " + duration.getMillis() / 1000 + " sec");
		// LOG.info("Cache duration: " + cacheDuration / 1000 +
		// " sec -> expires " +
		// DateTime.now().plusMillis(cacheDuration).toString("MM/dd/yyyy HH:mm:ss"));
		LOG.info("----------------------------------------------------------------------");
	}
}
