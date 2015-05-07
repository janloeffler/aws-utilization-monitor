/**
 * This class keeps the connection to AWS open and caches the result set for statistics.
 */
package de.zalando.platform.awsutilizationmonitor.collector;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;

import de.zalando.platform.awsutilizationmonitor.api.AwsAccount;
import de.zalando.platform.awsutilizationmonitor.config.Config;
import de.zalando.platform.awsutilizationmonitor.stats.AwsStats;

/**
 * @author jloeffler
 *
 *         This class collects usage of Amazon Webservices components such as
 *         EC2, SimpleDB, and S3.
 *
 *         In order to use the services in this sample, you need:
 *
 *         - A valid Amazon Web Services account. You can register for AWS at:
 *         https
 *         ://aws-portal.amazon.com/gp/aws/developer/registration/index.html
 *
 *         - Your account's Access Key ID and Secret Access Key:
 *         http://aws.amazon.com/security-credentials
 *
 *         - A subscription to Amazon EC2. You can sign up for EC2 at:
 *         http://aws.amazon.com/ec2/
 *
 *         - A subscription to Amazon SimpleDB. You can sign up for Simple DB
 *         at: http://aws.amazon.com/simpledb/
 *
 *         - A subscription to Amazon S3. You can sign up for S3 at:
 *         http://aws.amazon.com/s3/
 *
 *         Before running the code: Fill in your AWS access credentials in the
 *         provided credentials file template, and be sure to move the file to
 *         the default location (~/.aws/credentials) where the sample code will
 *         load the credentials from.
 *         https://console.aws.amazon.com/iam/home?#security_credential
 *
 *         WANRNING: To avoid accidental leakage of your credentials, DO NOT
 *         keep the credentials file in your source directory.
 *
 */
@Service
public final class AwsStatsCollector {

	private static DateTime lastCollectTime = DateTime.now();

	public static final Logger LOG = LoggerFactory.getLogger(AwsStatsCollector.class);

	private ArrayList<AwsAccount> accounts = new ArrayList<AwsAccount>();

	@Value("${connection.components.allow}")
	private String[] allowedComponents;

	@Value("${connection.cache.duration:3600000}")
	private int cacheDuration;

	private AwsCollectorThread collectorThread = null;
	@Value("${connection.components.ignore}")
	private String[] ignoredComponents;
	@Value("${connection.components.s3.details:true}")
	private boolean s3Details = true;

	private AwsStats stats = null;
	private AwsStats statsOld = null;

	@Value("${connection.regions:EU_WEST_1, EU_CENTRAL_1}")
	private String[] supportedRegions;

	/**
	 * Clear the cache with statistics about resource usage so that next request
	 * of getStats() loads resource information directly from AWS again.
	 */
	public void clearCache() {
		if (this.stats != null) {
			this.stats.clear();
		}
		this.stats = null;
		LOG.info("Cache cleared");
	}

	/**
	 * Start collector threads to collect resource usage data from AWS.
	 *
	 * @param currentStats
	 *            AwsStats object than will contain all found resources.
	 */
	private void collectDataFromAws(AwsStats currentStats) {
		if ((collectorThread != null) && collectorThread.isRunning())
			return;

		lastCollectTime = DateTime.now();
		AwsScan.S3_DETAILS = s3Details;
		LOG.info("Scan S3 bucket details: " + s3Details);

		loadAccounts();

		collectorThread = new AwsCollectorThread(currentStats, accounts, getConfig());
		collectorThread.start();

		this.stats = currentStats;
	}

	/**
	 * @return filled statistics object containing all used resources.
	 */
	public AwsStats forceAddStats() {
		if (stats == null) {
			stats = new AwsStats();
		}
		collectDataFromAws(stats);

		return stats;
	}

	/**
	 * @param maxItems
	 *            amount of items that should be created
	 */
	public void generateSampleData(int maxItems) {
		if (stats == null) {
			stats = new AwsStats();
		}

		stats.generateSampleData(maxItems);
	}

	public Config getConfig() {
		Config c = new Config();
		c.setAllowedComponents(allowedComponents);
		c.setCacheDuration(cacheDuration);
		c.setIgnoredComponents(ignoredComponents);
		c.setS3Details(s3Details);
		c.setSupportedRegions(supportedRegions);

		return c;
	}

	/**
	 * @return filled statistics object containing all used resources.
	 */
	public AwsStats getStats() {
		// return old chached stats object if collector thread is currently
		// collecting new statistics
		if ((collectorThread != null) && collectorThread.isRunning() && (statsOld != null) && !statsOld.isEmpty())
			return statsOld;
		else if ((stats == null) || stats.isEmpty() || ((DateTime.now().getMillis() - lastCollectTime.getMillis()) > cacheDuration)) {
			collectDataFromAws(new AwsStats());
		} else {
			statsOld = stats;
		}

		return stats;
	}

	/**
	 * Load default AWS accounts.
	 */
	private void loadAccounts() {
		if (accounts.isEmpty()) {
			accounts.add(new AwsAccount(new DefaultAWSCredentialsProviderChain().getCredentials()));
			accounts.add(new AwsAccount(new InstanceProfileCredentialsProvider().getCredentials()));
		}
	}

	/**
	 * Set list with specified accounts containing AWS credentials as AWS
	 * accounts.
	 *
	 * @param accounts
	 *            List of AWS accounts containing AWS credentails.
	 */
	public void setAccounts(AwsAccount[] accounts) {
		this.accounts.clear();

		for (AwsAccount account : accounts) {
			this.accounts.add(account);
			LOG.info("Added " + account.toString());
		}
	}

	public void setConfig(Config c) {
		allowedComponents = c.getAllowedComponents();
		cacheDuration = c.getCacheDuration();
		ignoredComponents = c.getIgnoredComponents();
		s3Details = c.isS3Details();
		supportedRegions = c.getSupportedRegions();
	}
}
