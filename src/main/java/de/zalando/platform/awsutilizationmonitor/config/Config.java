/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.config;

import java.util.Arrays;

/**
 * @author jloeffler
 *
 */
public class Config {

	private String[] allowedComponents = new String[] {};
	private int cacheDuration = 7200000;
	private String[] ignoredComponents = new String[] { "CloudWatch", "CloudFront" };
	private boolean s3Details = true;
	private String[] supportedRegions = new String[] { "EU_WEST_1", "EU_CENTRAL_1" };

	public Config() {
	}

	/**
	 * @return the allowedComponents
	 */
	public String[] getAllowedComponents() {
		return allowedComponents;
	}

	/**
	 * @return the cacheDuration
	 */
	public int getCacheDuration() {
		return cacheDuration;
	}

	/**
	 * @return the ignoredComponents
	 */
	public String[] getIgnoredComponents() {
		return ignoredComponents;
	}

	/**
	 * @return the supportedRegions
	 */
	public String[] getSupportedRegions() {
		return supportedRegions;
	}

	/**
	 * @return the s3Details
	 */
	public boolean isS3Details() {
		return s3Details;
	}

	/**
	 * @param allowedComponents
	 *            the allowedComponents to set
	 */
	public void setAllowedComponents(String[] allowedComponents) {
		if (allowedComponents == null) {
			this.allowedComponents = new String[] {};
		} else {
			this.allowedComponents = allowedComponents;
		}
	}

	/**
	 * @param cacheDuration
	 *            the cacheDuration to set
	 */
	public void setCacheDuration(int cacheDuration) {
		this.cacheDuration = cacheDuration;
	}

	/**
	 * @param ignoredComponents
	 *            the ignoredComponents to set
	 */
	public void setIgnoredComponents(String[] ignoredComponents) {
		if (ignoredComponents == null) {
			this.ignoredComponents = new String[] {};
		} else {
			this.ignoredComponents = ignoredComponents;
		}
	}

	/**
	 * @param s3Details
	 *            the s3Details to set
	 */
	public void setS3Details(boolean s3Details) {
		this.s3Details = s3Details;
	}

	/**
	 * @param supportedRegions
	 *            the supportedRegions to set
	 */
	public void setSupportedRegions(String[] supportedRegions) {
		if (supportedRegions == null) {
			this.supportedRegions = new String[] {};
		} else {
			this.supportedRegions = supportedRegions;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Config [allowedComponents=" + Arrays.toString(allowedComponents) + ", cacheDuration=" + cacheDuration + ", ignoredComponents="
				+ Arrays.toString(ignoredComponents) + ", s3Details=" + s3Details + ", supportedRegions=" + Arrays.toString(supportedRegions) + "]";
	}
}
