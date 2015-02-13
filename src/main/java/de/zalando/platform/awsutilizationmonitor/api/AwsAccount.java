/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;

/**
 * @author jloeffler
 *
 */
public class AwsAccount {
	public static final Logger LOG = LoggerFactory.getLogger(AwsAccount.class);

	private String accessKeyId;
	private String accountId;
	private AWSCredentials credentials;
	private String secretKey;
	private String sessionToken;

	/**
	 */
	public AwsAccount() {
	}

	/**
	 * @param credentials
	 */
	public AwsAccount(AWSCredentials credentials) {
		this("", credentials);
	}

	/**
	 * @param accountId
	 * @param credentials
	 */
	public AwsAccount(String accountId, AWSCredentials credentials) {
		this.credentials = credentials;
		this.accessKeyId = credentials.getAWSAccessKeyId();
		this.secretKey = credentials.getAWSSecretKey();
	}

	/**
	 * @param accountId
	 * @param accessKeyId
	 * @param secretKey
	 * @param sessionToken
	 */
	public AwsAccount(String accountId, String accessKeyId, String secretKey, String sessionToken) {
		this.accountId = accountId;
		this.accessKeyId = accessKeyId;
		this.secretKey = secretKey;
		this.sessionToken = sessionToken;
	}

	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return accessKeyId;
	}

	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	public AWSCredentials getCredentials() {
		if (credentials == null) {
			credentials = new BasicSessionCredentials(accessKeyId, secretKey, sessionToken);
		}

		return credentials;
	}

	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @return the sessionToken
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * Read AWS account ID
	 */
	public void retrieveAccountIdFromAwsAPI() {
		String accountId = "";

		try {
			AmazonIdentityManagementClient iamClient = new AmazonIdentityManagementClient(getCredentials());
			LOG.info("Current AWS user: " + iamClient.getUser().getUser().getUserId());
			accountId = iamClient.getUser().getUser().getArn();
		} catch (AmazonServiceException e) {
			if (e.getErrorCode().compareTo("AccessDenied") == 0) {
				String arn = null;
				String msg = e.getMessage();
				// User:
				// arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/Bob
				// is not authorized to perform: iam:GetUser on
				// resource:
				// arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/Bob
				// arn:aws:sts::123456789012:assumed-role/Shibboleth-PowerUser/username
				int arnIdx = msg.indexOf("arn:aws");
				if (arnIdx != -1) {
					int arnSpace = msg.indexOf(" ", arnIdx);
					arn = msg.substring(arnIdx, arnSpace);

					// Remove "arn:aws:sts::"
					arn = arn.substring(13, 13 + 12);
				}
				accountId = arn;
			}

			if (accountId.length() == 0) {
				LOG.warn("Cannot lookup account id: " + e.getMessage());
			}
		} catch (Exception ex) {
			LOG.error("Cannot lookup account id: " + ex.getMessage());
		}

		setAccountId(accountId);
	}

	/**
	 * @param accessKeyId
	 *            the accessKeyId to set
	 */

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	/**
	 * @param accountId
	 *            the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * @param secretKey
	 *            the secretKey to set
	 */

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @param sessionToken
	 *            the sessionToken to set
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	@Override
	public String toString() {
		return "accountId=" + accountId;
	}
}
