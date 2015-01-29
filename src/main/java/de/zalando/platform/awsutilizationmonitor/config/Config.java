/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author jloeffler
 *
 */
@Configuration
public class Config {
	// set caching time to 1 hour - afterwards the aws api will be called again
	// to collect resource usage
	public final static int DEFAULT_CACHE_DURATION = 60 * 60 * 1000;

	@Value("${connection.cache.duration}")
	private int cacheDuration = DEFAULT_CACHE_DURATION;

	/**
	 * @return the cacheDuration
	 */
	public int getCacheDuration() {
		return cacheDuration;
	}

	/**
	 * @param cacheDuration
	 *            the cacheDuration to set
	 */
	public void setCacheDuration(int cacheDuration) {
		this.cacheDuration = cacheDuration;
	}
}
