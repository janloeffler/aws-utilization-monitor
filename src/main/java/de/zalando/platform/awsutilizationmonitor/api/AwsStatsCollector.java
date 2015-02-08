/**
 * This class keeps the connection to AWS open and caches the result set for statistics.
 */
package de.zalando.platform.awsutilizationmonitor.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClient;
import com.amazonaws.services.cloudsearchv2.model.DomainStatus;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.elasticache.AmazonElastiCache;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.Pipeline;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.redshift.AmazonRedshift;
import com.amazonaws.services.redshift.AmazonRedshiftClient;
import com.amazonaws.services.redshift.model.Cluster;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.DomainMetadataRequest;
import com.amazonaws.services.simpledb.model.DomainMetadataResult;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PlatformApplication;
import com.amazonaws.services.sns.model.Subscription;
import com.amazonaws.services.sqs.AmazonSQSClient;

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

	/**
	 * Collect data for CloudFront.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanCloudFront(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for CloudFront in region " + region.getName());

		try {
			/*
			 * AmazonCloudFrontClient cf = new
			 * AmazonCloudFrontClient(credentials);
			 * cf.setRegion(Region.getRegion(region));
			 */
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of CloudFront: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of CloudFront: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for CloudSearch.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanCloudSearch(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for CloudSearch in region " + region.getName());

		try {
			AmazonCloudSearchClient cs = new AmazonCloudSearchClient(credentials);
			cs.setRegion(Region.getRegion(region));

			int totalDomains = 0;
			for (DomainStatus ds : cs.describeDomains().getDomainStatusList()) {
				AwsResource res = new AwsResource(ds.getDomainName(), accountId, AwsResourceType.CloudSearch, region);
				res.addInfo("Endpoint", ds.getSearchService().getEndpoint());
				res.addInfo("SearchInstanceType", ds.getSearchInstanceType());
				res.addInfo("SearchInstanceCount", ds.getSearchInstanceCount());
				res.addInfo("ARN", ds.getARN());
				stats.add(res);
				totalDomains++;
			}

			LOG.info(totalDomains + " CloudSearch domains in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of CloudSearch: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of CloudSearch: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for CloudWatch.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanCloudWatch(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for CloudWatch in region " + region.getName());

		try {
			AmazonCloudWatchClient cw = new AmazonCloudWatchClient(credentials);
			cw.setRegion(Region.getRegion(region));

			int totalMetrics = 0;

			for (Metric m : cw.listMetrics().getMetrics()) {
				AwsResource res = new AwsResource(m.getMetricName(), accountId, AwsResourceType.CloudWatch, region);
				stats.add(res);
				totalMetrics++;
			}

			LOG.info(totalMetrics + " CloudWatch metrics in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of CloudWatch: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of CloudWatch: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for DynamoDB.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanDynamoDB(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for DynamoDB in region " + region.getName());

		/*
		 * Amazon DynamoDB
		 */
		try {
			AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(credentials);
			dynamoDB.setRegion(Region.getRegion(region));

			List<String> list = dynamoDB.listTables().getTableNames();

			int totalItems = list.size();
			for (String tableName : list) {
				AwsResource res = new AwsResource(tableName, accountId, AwsResourceType.DynamoDB, region);
				stats.add(res);
			}

			LOG.info(totalItems + " DynamoDB tables in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of DynamoDB: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for EC2.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanEC2(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for EC2 in region " + region.getName());

		try {
			AmazonEC2 ec2 = new AmazonEC2Client(credentials);
			ec2.setRegion(Region.getRegion(region));

			DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
			LOG.info(availabilityZonesResult.getAvailabilityZones().size() + " Availability Zones in region " + region.getName());

			// DescribeTagsResult tagsResult = ec2.describeTags();
			// for (TagDescription tag : tagsResult.getTags()) {
			// LOG.info("tag: key=" + tag.getKey() + "; value=" + tag.getValue()
			// + " resid=" + tag.getResourceId());
			// }

			// DescribeImagesResult imagesResult = ec2.describeImages();
			// LOG.info(imagesResult.getImages().size() + " EC2 images");

			// DescribeSecurityGroupsResult securityGroupsResult =
			// ec2.describeSecurityGroups();
			// LOG.info(securityGroupsResult.getSecurityGroups().size() +
			// " EC2 security groups in region " + region.getName());

			// DescribeVolumesResult volumesResult = ec2.describeVolumes();
			// LOG.info(volumesResult.getVolumes().size() +
			// " EC2 volumes in region " + region.getName());

			DescribeInstancesResult describeInstancesResult = ec2.describeInstances();
			List<Reservation> reservations = describeInstancesResult.getReservations();
			int totalInstances = 0;

			for (Reservation reservation : reservations) {
				int instancesAdded = 0;
				try {
					for (Instance instance : reservation.getInstances()) {
						AwsResource res = new AwsResource(instance.getKeyName(), accountId, AwsResourceType.EC2, region);
						res.addInfo("OwnerId", reservation.getOwnerId());
						res.addInfo("InstanceType", instance.getInstanceType());
						res.addInfo("PrivateIpAddress", instance.getPrivateIpAddress());
						res.addInfo("PrivateDnsName", instance.getPrivateDnsName());

						try {
							res.addInfo("PublicIpAddress", instance.getPublicIpAddress());
							res.addInfo("PublicDnsName", instance.getPublicDnsName());
						} catch (Exception ex) {
							// no public IP and DNS name -> results in a null
							// pointer exception :-(
						}

						res.addInfo("LauchTime", instance.getLaunchTime().toString());
						res.addInfo("State", instance.getState().getName());
						res.addInfo("AvailabilityZone", instance.getPlacement().getAvailabilityZone());

						for (Tag tag : instance.getTags()) {
							res.addInfo(tag.getKey(), tag.getValue());
						}

						stats.add(res);
						instancesAdded++;
					}
				} catch (Exception ex) {
					LOG.error("Error on reading instances of reservation: " + reservation.getReservationId() + ": " + ex.getMessage());
				}

				if (instancesAdded == 0) {
					AwsResource res = new AwsResource(reservation.getReservationId(), accountId, AwsResourceType.EC2, region);
					res.addInfo("OwnerId", reservation.getOwnerId());
					res.addInfo("info", "No instances of reservation found");
					stats.add(res);
					LOG.info("No instances of reservation found: " + res.getName());
				}

				totalInstances += instancesAdded;
			}

			LOG.info(totalInstances + " EC2 instances running in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of EC2: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of EC2: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for ElastiCache.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanElastiCache(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		if (region == Regions.EU_CENTRAL_1)
			return;

		LOG.debug("Scan for ElastiCache in region " + region.getName());

		/*
		 * Amazon ElastiCache
		 */
		try {
			AmazonElastiCache elastiCache = new AmazonElastiCacheClient(credentials);
			elastiCache.setRegion(Region.getRegion(region));

			List<CacheCluster> list = elastiCache.describeCacheClusters().getCacheClusters();

			int totalItems = list.size();
			for (CacheCluster cluster : list) {
				AwsResource res = new AwsResource(cluster.getCacheClusterId(), accountId, AwsResourceType.ElastiCache, region);
				res.addInfo("Engine", cluster.getEngine());
				res.addInfo("EngineVersion", cluster.getEngineVersion());
				res.addInfo("NumCacheNodes", cluster.getNumCacheNodes());
				stats.add(res);
			}

			LOG.info(totalItems + " ElastiCache in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of ElastiCache: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for ElasticMapReduce.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanElasticMapReduce(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for MapReduce in region " + region.getName());

		try {
			AmazonElasticMapReduce elasticMapReduce = new AmazonElasticMapReduceClient(credentials);
			elasticMapReduce.setRegion(Region.getRegion(region));

			List<ClusterSummary> list = elasticMapReduce.listClusters().getClusters();

			int totalItems = list.size();
			for (ClusterSummary cs : list) {
				stats.add(new AwsResource(cs.getName(), accountId, AwsResourceType.ElasticMapReduce, region));
			}

			LOG.info(totalItems + " ElasticMapReduce clusters in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of ElasticMapReduce: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for ElasticTranscoder.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanElasticTranscoder(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		if (region == Regions.EU_CENTRAL_1)
			return;

		LOG.debug("Scan for ElasticTranscoder in region " + region.getName());

		try {
			AmazonElasticTranscoder elasticTranscoder = new AmazonElasticTranscoderClient(credentials);
			elasticTranscoder.setRegion(Region.getRegion(region));

			List<Pipeline> list = elasticTranscoder.listPipelines().getPipelines();

			int totalItems = list.size();
			for (Pipeline pipeline : list) {
				AwsResource res = new AwsResource(pipeline.getName(), accountId, AwsResourceType.ElasticTranscoder, region);
				res.addInfo("Arn", pipeline.getArn());
				stats.add(res);
			}

			LOG.info(totalItems + " Elastic Transcoder pipelines in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of ElasticTranscoder: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for Glacier.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanGlacier(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for Glacier in region " + region.getName());

		try {
			AmazonGlacier glacier = new AmazonGlacierClient(credentials);
			glacier.setRegion(Region.getRegion(region));

			// DescribeVaultRequest dvr = new DescribeVaultRequest();
			ListVaultsRequest lvr = new ListVaultsRequest();
			int totalItems = 0;
			for (DescribeVaultOutput dvo : glacier.listVaults(lvr).getVaultList()) {
				AwsResource res = new AwsResource(dvo.getVaultName(), accountId, AwsResourceType.Glacier, region);
				res.addInfo("NumberOfArchives", dvo.getNumberOfArchives());
				res.addInfo("VaultARN", dvo.getVaultARN());
				res.addInfo("SizeInBytes", dvo.getSizeInBytes());
				stats.add(res);
				totalItems++;
			}

			LOG.info(totalItems + " Glacier in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of Glacier: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for Kinesis.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanKinesis(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for Kinesis in region " + region.getName());

		try {
			AmazonKinesis kinesis = new AmazonKinesisClient(credentials);
			kinesis.setRegion(Region.getRegion(region));

			List<String> list = kinesis.listStreams().getStreamNames();

			int totalItems = list.size();
			for (String streamName : list) {
				stats.add(new AwsResource(streamName, accountId, AwsResourceType.Kinesis, region));
			}

			LOG.info(totalItems + " Kinesis streams in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of Kinesis: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for RDS.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanRDS(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for RDS in region " + region.getName());

		try {
			AmazonRDS rds = new AmazonRDSClient(credentials);
			rds.setRegion(Region.getRegion(region));

			List<DBInstance> list = rds.describeDBInstances().getDBInstances();

			int totalItems = list.size();
			for (DBInstance dbInstance : list) {
				AwsResource res = new AwsResource(dbInstance.getDBName(), accountId, AwsResourceType.RDS, region);
				res.addInfo("DBInstanceIdentifier", dbInstance.getDBInstanceIdentifier());
				stats.add(res);
			}

			LOG.info(totalItems + " RDS instances in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of RDS: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for Redshift.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanRedshift(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for Redshift in region " + region.getName());

		try {
			AmazonRedshift redshift = new AmazonRedshiftClient(credentials);
			redshift.setRegion(Region.getRegion(region));

			List<Cluster> list = redshift.describeClusters().getClusters();

			int totalItems = list.size();
			for (Cluster cluster : list) {
				AwsResource res = new AwsResource(cluster.getClusterIdentifier(), accountId, AwsResourceType.Redshift, region);
				res.addInfo("DBName", cluster.getDBName());
				stats.add(res);
			}

			LOG.info(totalItems + " Redshift cluster in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of Redshift: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for SimpleDB.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 * @param region
	 *            region in which the resources should be searched
	 * @param resourceType
	 *            Type of resource to be searched
	 */
	public static void scanResources(AwsStats stats, AWSCredentials credentials, String accountId, Regions region, AwsResourceType resourceType) {
		switch (resourceType) {
		case CloudFront:
			scanCloudFront(stats, credentials, accountId, region);
			break;
		case CloudSearch:
			scanCloudSearch(stats, credentials, accountId, region);
			break;
		case CloudWatch:
			scanCloudWatch(stats, credentials, accountId, region);
			break;
		case DynamoDB:
			scanDynamoDB(stats, credentials, accountId, region);
			break;
		case EC2:
			scanEC2(stats, credentials, accountId, region);
			break;
		case ElastiCache:
			scanElastiCache(stats, credentials, accountId, region);
			break;
		case ElasticMapReduce:
			scanElasticMapReduce(stats, credentials, accountId, region);
			break;
		case ElasticTranscoder:
			scanElasticTranscoder(stats, credentials, accountId, region);
			break;
		case Glacier:
			scanGlacier(stats, credentials, accountId, region);
			break;
		case Kinesis:
			scanKinesis(stats, credentials, accountId, region);
			break;
		case RDS:
			scanRDS(stats, credentials, accountId, region);
			break;
		case Redshift:
			scanRedshift(stats, credentials, accountId, region);
			break;
		case S3:
			scanS3(stats, credentials, accountId, region);
			break;
		case SNS:
			scanSNS(stats, credentials, accountId, region);
			break;
		case SQS:
			scanSQS(stats, credentials, accountId, region);
			break;
		case SimpleDB:
			scanSimpleDB(stats, credentials, accountId, region);
			break;
		case Unknown:
			break;
		}
	}

	/**
	 * Collect data for S3.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanS3(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for S3 in region " + region.getName());

		/*
		 * Amazon S3
		 *
		 * The AWS S3 client allows you to manage buckets and programmatically
		 * put and get objects to those buckets.
		 *
		 * In this sample, we use an S3 client to iterate over all the buckets
		 * owned by the current user, and all the object metadata in each
		 * bucket, to obtain a total object and space usage count. This is done
		 * without ever actually downloading a single object -- the requests
		 * work with object metadata only.
		 */
		try {
			System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");

			AmazonS3 s3 = new AmazonS3Client(credentials);
			// s3.setRegion(Region.getRegion(region));

			List<Bucket> buckets = s3.listBuckets();

			long totalSize = 0;
			int totalItems = 0;
			for (Bucket bucket : buckets) {
				/*
				 * In order to save bandwidth, an S3 object listing does not
				 * contain every object in the bucket; after a certain point the
				 * S3ObjectListing is truncated, and further pages must be
				 * obtained with the AmazonS3Client.listNextBatchOfObjects()
				 * method.
				 */
				ObjectListing objects = s3.listObjects(bucket.getName());
				long size = 0;
				long items = 0;
				do {
					for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
						size += objectSummary.getSize();
						items++;
					}
					objects = s3.listNextBatchOfObjects(objects);
				} while (objects.isTruncated());

				totalItems += items;
				totalSize += size;
				AwsResource res = new AwsResource(bucket.getName(), accountId, AwsResourceType.S3, region);
				res.addInfo("Owner", bucket.getOwner().getDisplayName());
				res.addInfo("SizeInBytes", size);
				res.addInfo("Objects", items);
				stats.add(res);
			}

			LOG.info(buckets.size() + " S3 buckets containing " + totalItems + " objects with a total size of " + totalSize + " bytes");
		} catch (AmazonServiceException ase) {
			/*
			 * AmazonServiceExceptions represent an error response from an AWS
			 * services, i.e. your request made it to AWS, but the AWS service
			 * either found it invalid or encountered an error trying to execute
			 * it.
			 */
			LOG.error("Exception of S3: " + ase.getMessage());
		} catch (AmazonClientException ace) {
			/*
			 * AmazonClientExceptions represent an error that occurred inside
			 * the client on the local host, either while trying to send the
			 * request to AWS or interpret the response. For example, if no
			 * network connection is available, the client won't be able to
			 * connect to AWS to execute a request and will throw an
			 * AmazonClientException.
			 */
			LOG.error("Exception of S3: " + ace.getMessage());
		}
	}

	/**
	 * Collect data for SimpleDB.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanSimpleDB(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		if (region == Regions.EU_CENTRAL_1)
			return;

		LOG.debug("Scan for SimpleDB in region " + region.getName());

		/*
		 * Amazon SimpleDB
		 *
		 * The AWS SimpleDB client allows you to query and manage your data
		 * stored in SimpleDB domains (similar to tables in a relational DB).
		 *
		 * In this sample, we use a SimpleDB client to iterate over all the
		 * domains owned by the current user, and add up the number of items
		 * (similar to rows of data in a relational DB) in each domain.
		 */
		try {
			AmazonSimpleDB simpleDB = new AmazonSimpleDBClient(credentials);
			simpleDB.setRegion(Region.getRegion(region));

			ListDomainsRequest sdbRequest = new ListDomainsRequest().withMaxNumberOfDomains(100);
			ListDomainsResult sdbResult = simpleDB.listDomains(sdbRequest);

			int totalItems = 0;
			for (String domainName : sdbResult.getDomainNames()) {
				DomainMetadataRequest metadataRequest = new DomainMetadataRequest().withDomainName(domainName);
				DomainMetadataResult domainMetadata = simpleDB.domainMetadata(metadataRequest);
				int items = domainMetadata.getItemCount();
				totalItems += items;
				AwsResource res = new AwsResource(domainName, accountId, AwsResourceType.SimpleDB, region);
				res.addInfo("Items", items);
				stats.add(res);
			}

			LOG.info(sdbResult.getDomainNames().size() + " SimpleDB domains containing a total of " + totalItems + " items in region " + region.getName());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of SimpleDB: " + ase.getMessage());
			// } catch (java.net.UnknownHostException uhe) {
		} catch (Exception ex) {
			LOG.error("Exception of SimpleDB: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for SNS.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanSNS(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for SNS in region " + region.getName());

		try {
			AmazonSNS sns = new AmazonSNSClient(credentials);
			sns.setRegion(Region.getRegion(region));

			int totalApps = 0;
			for (PlatformApplication app : sns.listPlatformApplications().getPlatformApplications()) {
				AwsResource res = new AwsResource(app.getPlatformApplicationArn(), accountId, AwsResourceType.SNS, region);
				stats.add(res);
				totalApps++;
			}

			LOG.info(totalApps + " SNS applications in region " + region.getName());

			int totalSubscriptions = 0;
			for (Subscription subscription : sns.listSubscriptions().getSubscriptions()) {
				AwsResource res = new AwsResource(subscription.getSubscriptionArn(), accountId, AwsResourceType.SNS, region);
				res.addInfo("Owner", subscription.getOwner());
				res.addInfo("Endpoint", subscription.getEndpoint());
				res.addInfo("TopicArn", subscription.getTopicArn());
				stats.add(res);
				totalSubscriptions++;
			}

			LOG.info(totalSubscriptions + " SNS subscriptions in region " + region.getName());

		} catch (AmazonServiceException ase) {
			LOG.error("Exception of SNS: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of SNS: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for SQS.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param credentials
	 *            currently used credentials object.
	 */
	public static void scanSQS(AwsStats stats, AWSCredentials credentials, String accountId, Regions region) {
		LOG.debug("Scan for SQS in region " + region.getName());

		try {
			AmazonSQSClient sqs = new AmazonSQSClient(credentials);
			sqs.setRegion(Region.getRegion(region));

			int totalQueues = 0;
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				AwsResource res = new AwsResource(queueUrl, accountId, AwsResourceType.SQS, region);
				stats.add(res);
				totalQueues++;
			}

			LOG.info(totalQueues + " SQS queues in region " + region.getName());

		} catch (AmazonServiceException ase) {
			LOG.error("Exception of SQS: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of SQS: " + ex.getMessage());
		}
	}

	@Value("${connection.cache.duration:3600000}")
	private int cacheDuration;

	private AwsStats stats = null;

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
	 * Amazon EC2
	 *
	 * The AWS EC2 client allows you to create, delete, and administer instances
	 * programmatically.
	 *
	 * In this sample, we use an EC2 client to get a list of all the
	 * availability zones, and all instances sorted by reservation id.
	 */
	private void collectDataFromAws(AwsStats currentStats) {
		LOG.info("----------------------------------------------------------------------");
		LOG.info("Login to AWS and collect resource list");
		LOG.info("----------------------------------------------------------------------");

		DateTime startTime = DateTime.now();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		AWSCredentials credentials = null;
		String accountId = "";
		lastCollectTime = DateTime.now();

		try {
			/*
			 * The ProfileCredentialsProvider will return your [default]
			 * credential profile by reading from the credentials file located
			 * at (~/.aws/credentials).
			 */
			try {
				credentials = new ProfileCredentialsProvider().getCredentials();
			} catch (Exception e) {
				throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
						+ "Please make sure that your credentials file is at the correct " + "location (~/.aws/credentials), and is in valid format.", e);
			}

			LOG.info("Supported regions: " + Arrays.toString(supportedRegions));
			ArrayList<Regions> regions = new ArrayList<Regions>();
			for (String s : supportedRegions) {
				regions.add(Regions.valueOf(s));
			}

			try {
				AmazonIdentityManagementClient iamClient = new AmazonIdentityManagementClient(credentials);
				LOG.info("Current AWS user: " + iamClient.getUser().getUser().getUserId());
				accountId = iamClient.getUser().getUser().getArn();
			} catch (AmazonServiceException e) {
				LOG.info("Cannot lookup account id: " + e.getMessage());
				if (e.getErrorCode().compareTo("AccessDenied") == 0) {
					String arn = null;
					String msg = e.getMessage();
					// User:
					// arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/Bob
					// is not authorized to perform: iam:GetUser on resource:
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
			} catch (Exception ex) {
				LOG.error("Cannot lookup account id: " + ex.getMessage());
			}

			LOG.info("Current AWS account ID: " + accountId);

			ArrayList<AwsResourceType> resourceTypes = new ArrayList<AwsResourceType>();
			for (AwsResourceType resourceType : AwsResourceType.values()) {

				// exclude S3 and scan it afterwards separately
				if ((resourceType != AwsResourceType.Unknown) && (resourceType != AwsResourceType.S3)) {
					resourceTypes.add(resourceType);
				}
			}

			// scan S3 only once
			AwsCollectorThread thread = new AwsCollectorThread(currentStats, credentials, accountId, Regions.DEFAULT_REGION, AwsResourceType.S3);
			threads.add(thread);
			thread.start();

			// scan each resource type in each region
			for (Regions region : regions) {
				for (AwsResourceType resourceType : resourceTypes) {
					thread = new AwsCollectorThread(currentStats, credentials, accountId, region, resourceType);
					threads.add(thread);
					thread.start();
				}
			}

			// wait for all collection threads to be finished
			for (Thread t : threads) {
				t.join();
			}

		} catch (Exception ex) {
			LOG.error("Connect to AWS failed: " + ex.getMessage());
		}

		this.stats = currentStats;
		DateTime duration = DateTime.now().minus(startTime.getMillis());
		LOG.info("----------------------------------------------------------------------");
		LOG.info("Collected " + currentStats.getSummary().getResources() + " resources in " + duration.getMillis() / 1000 + " sec");
		LOG.info("Cache duration: " + cacheDuration / 1000 + " sec -> expires " + DateTime.now().plusMillis(cacheDuration).toString("MM/dd/yyyy HH:mm:ss"));
		LOG.info("----------------------------------------------------------------------");
	}

	/**
	 * @return filled statistics object containing all used resources.
	 */
	public AwsStats forceAddStats() {
		if (this.stats == null) {
			this.stats = new AwsStats();
		}
		collectDataFromAws(this.stats);

		return stats;
	}

	/**
	 * @param maxItems
	 *            amount of items that should be created
	 */
	public void generateSampleData(int maxItems) {
		stats.generateSampleData(maxItems);
	}

	/**
	 * @return filled statistics object containing all used resources.
	 */
	public AwsStats getStats() {
		if ((stats == null) || (stats.getItemCount() == 0) || ((DateTime.now().getMillis() - lastCollectTime.getMillis()) > cacheDuration)) {
			collectDataFromAws(new AwsStats());
		}

		return stats;
	}
}
