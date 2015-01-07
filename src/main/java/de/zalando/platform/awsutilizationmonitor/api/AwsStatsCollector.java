package de.zalando.platform.awsutilizationmonitor.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jloeffler
 * This class collects all statistics data from Amazon Webservices and manages the cache for it.
 */
public class AwsStatsCollector {
    public static final Logger LOG = LoggerFactory.getLogger(AwsStatsCollector.class);

    private AwsStats stats = null;
    
    /**
     * Clear the cache with statistics about resource usage so that next request of getStats() loads resource information directly from AWS again.
     */
    public void clearCache() {
        getStats().clear();
        AwsConnection.getInstance().clearCache();
    }
    
    /**
     * Generate some sample data to test application.
     */
    public void generateSampleData(int maxItems) {
        if (this.stats == null) {
    		this.stats = AwsConnection.getInstance().getStats(false);       	
        }

        this.stats.generateSampleData(maxItems);
    }

    /**
     * @return filled statistics object containing all used resources.
     */
    public AwsStats getStats() {
        if (this.stats == null) {
    		this.stats = AwsConnection.getInstance().getStats();       	
        }
    	
    	return this.stats;
    }
}