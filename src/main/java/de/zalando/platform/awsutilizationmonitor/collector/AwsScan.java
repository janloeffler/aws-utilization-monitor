/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.collector;

import java.util.Hashtable;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SDKGlobalConfiguration;
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
import com.amazonaws.services.ec2.model.Image;
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

import de.zalando.platform.awsutilizationmonitor.api.AwsAccount;
import de.zalando.platform.awsutilizationmonitor.stats.AwsResource;
import de.zalando.platform.awsutilizationmonitor.stats.AwsResourceType;
import de.zalando.platform.awsutilizationmonitor.stats.AwsStats;
import de.zalando.platform.awsutilizationmonitor.stats.AwsTag;

/**
 * @author jloeffler
 *
 */
public class AwsScan {

	public static final Logger LOG = LoggerFactory.getLogger(AwsScan.class);
	private static final long PROGRESS_TICK = 30000;

	public static boolean S3_DETAILS = true;

	/**
	 * Collect data for CloudFront.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanCloudFront(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for CloudFront in region " + region.getName() + " in account " + account.getAccountId());

		try {
			/*
			 * AmazonCloudFrontClient cf = new
			 * AmazonCloudFrontClient(account.getCredentials());
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
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanCloudSearch(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for CloudSearch in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonCloudSearchClient cs = new AmazonCloudSearchClient(account.getCredentials());
			cs.setRegion(Region.getRegion(region));

			int totalDomains = 0;
			for (DomainStatus ds : cs.describeDomains().getDomainStatusList()) {
				AwsResource res = new AwsResource(ds.getDomainName(), account.getAccountId(), AwsResourceType.CloudSearch, region);
				res.addInfo("Endpoint", ds.getSearchService().getEndpoint());
				res.addInfo("SearchInstanceType", ds.getSearchInstanceType());
				res.addInfo("SearchInstanceCount", ds.getSearchInstanceCount());
				res.addInfo("ARN", ds.getARN());
				stats.add(res);
				totalDomains++;
			}

			LOG.info(totalDomains + " CloudSearch domains in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			if (ase.getErrorCode().contains("AccessDenied")) {
				LOG.info("Access denied for CloudSearch in region " + region.getName() + " in account " + account.getAccountId());
			} else {
				LOG.error("Exception of CloudSearch: " + ase.getMessage());
			}
		} catch (Exception ex) {
			LOG.error("Exception of CloudSearch: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for CloudWatch.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanCloudWatch(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for CloudWatch in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonCloudWatchClient cw = new AmazonCloudWatchClient(account.getCredentials());
			cw.setRegion(Region.getRegion(region));

			int totalMetrics = 0;

			for (Metric m : cw.listMetrics().getMetrics()) {
				AwsResource res = new AwsResource(m.getMetricName(), account.getAccountId(), AwsResourceType.CloudWatch, region);
				stats.add(res);
				totalMetrics++;
			}

			LOG.info(totalMetrics + " CloudWatch metrics in region " + region.getName() + " in account " + account.getAccountId());
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
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanDynamoDB(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for DynamoDB in region " + region.getName() + " in account " + account.getAccountId());

		/*
		 * Amazon DynamoDB
		 */
		try {
			AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(account.getCredentials());
			dynamoDB.setRegion(Region.getRegion(region));

			List<String> list = dynamoDB.listTables().getTableNames();

			int totalItems = list.size();
			for (String tableName : list) {
				AwsResource res = new AwsResource(tableName, account.getAccountId(), AwsResourceType.DynamoDB, region);
				stats.add(res);
			}

			LOG.info(totalItems + " DynamoDB tables in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of DynamoDB: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for EC2.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanEC2(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for EC2 in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonEC2 ec2 = new AmazonEC2Client(account.getCredentials());
			ec2.setRegion(Region.getRegion(region));

			DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
			LOG.info(availabilityZonesResult.getAvailabilityZones().size() + " Availability Zones in region " + region.getName() + " in account "
					+ account.getAccountId());

			/*
			 * Load AMI images.
			 */
			Hashtable<String, String> imageTable = new Hashtable<String, String>();
			try {
				List<Image> images = ec2.describeImages().getImages();
				LOG.info(images.size() + " EC2 images");
				for (Image image : images) {
					try {
						imageTable.put(image.getImageId(), image.getName());
					} catch (Exception e) {
					}
				}
			} catch (Exception e) {
				LOG.error("Exception in loading image list for EC2 in region " + region.getName() + " in account " + account.getAccountId() + ": "
						+ e.getMessage());
			}

			DescribeInstancesResult describeInstancesResult = ec2.describeInstances();
			List<Reservation> reservations = describeInstancesResult.getReservations();
			int totalInstances = 0;

			for (Reservation reservation : reservations) {
				int instancesAdded = 0;
				try {
					for (Instance instance : reservation.getInstances()) {
						AwsResource res = new AwsResource(instance.getKeyName(), account.getAccountId(), AwsResourceType.EC2, region);
						res.addInfo(AwsTag.OwnerId, reservation.getOwnerId());
						res.addInfo(AwsTag.InstanceType, instance.getInstanceType());
						res.addInfo(AwsTag.PrivateIpAddress, instance.getPrivateIpAddress());
						res.addInfo(AwsTag.PrivateDnsName, instance.getPrivateDnsName());

						try {
							String ami = instance.getImageId();
							if ((ami != null) && imageTable.containsKey(ami)) {
								ami = imageTable.get(ami);
							}
							res.addInfo(AwsTag.AMI, ami);
						} catch (Exception ex) {
						}

						try {
							res.addInfo(AwsTag.PublicIpAddress, instance.getPublicIpAddress());
							res.addInfo(AwsTag.PublicDnsName, instance.getPublicDnsName());
						} catch (Exception ex) {
							// no public IP and DNS name -> results in a null
							// pointer exception :-(
						}

						int days = (int) ((DateTime.now().getMillis() - instance.getLaunchTime().getTime()) / (24 * 60 * 60 * 1000));
						// keep getLaunchTime().toString(), since it will be
						// long millis instead
						res.addInfo(AwsTag.LaunchTime, instance.getLaunchTime().toString());
						res.addInfo(AwsTag.RunningSinceDays, days);
						res.addInfo(AwsTag.State, instance.getState().getName());
						res.addInfo(AwsTag.AvailabilityZone, instance.getPlacement().getAvailabilityZone());

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
					AwsResource res = new AwsResource(reservation.getReservationId(), account.getAccountId(), AwsResourceType.EC2, region);
					res.addInfo("OwnerId", reservation.getOwnerId());
					res.addInfo("info", "No instances of reservation found");
					stats.add(res);
					LOG.info("No instances of reservation found: " + res.getName());
				}

				totalInstances += instancesAdded;
			}

			LOG.info(totalInstances + " EC2 instances running in region " + region.getName() + " in account " + account.getAccountId());
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
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanElastiCache(AwsStats stats, AwsAccount account, Regions region) {
		if (region == Regions.EU_CENTRAL_1)
			return;

		LOG.debug("Scan for ElastiCache in region " + region.getName() + " in account " + account.getAccountId());

		/*
		 * Amazon ElastiCache
		 */
		try {
			AmazonElastiCache elastiCache = new AmazonElastiCacheClient(account.getCredentials());
			elastiCache.setRegion(Region.getRegion(region));

			List<CacheCluster> list = elastiCache.describeCacheClusters().getCacheClusters();

			int totalItems = list.size();
			for (CacheCluster cluster : list) {
				AwsResource res = new AwsResource(cluster.getCacheClusterId(), account.getAccountId(), AwsResourceType.ElastiCache, region);
				res.addInfo("Engine", cluster.getEngine());
				res.addInfo("EngineVersion", cluster.getEngineVersion());
				res.addInfo("NumCacheNodes", cluster.getNumCacheNodes());
				stats.add(res);
			}

			LOG.info(totalItems + " ElastiCache in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of ElastiCache: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for ElasticMapReduce.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanElasticMapReduce(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for MapReduce in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonElasticMapReduce elasticMapReduce = new AmazonElasticMapReduceClient(account.getCredentials());
			elasticMapReduce.setRegion(Region.getRegion(region));

			List<ClusterSummary> list = elasticMapReduce.listClusters().getClusters();

			int totalItems = list.size();
			for (ClusterSummary cs : list) {
				stats.add(new AwsResource(cs.getName(), account.getAccountId(), AwsResourceType.ElasticMapReduce, region));
			}

			LOG.info(totalItems + " ElasticMapReduce clusters in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			if (ase.getErrorCode().contains("AccessDenied")) {
				LOG.info("Access denied for ElasticMapReduce in region " + region.getName() + " in account " + account.getAccountId());
			} else {
				LOG.error("Exception of ElasticMapReduce: " + ase.getMessage());
			}
		}
	}

	/**
	 * Collect data for ElasticTranscoder.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanElasticTranscoder(AwsStats stats, AwsAccount account, Regions region) {
		if (region == Regions.EU_CENTRAL_1)
			return;

		LOG.debug("Scan for ElasticTranscoder in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonElasticTranscoder elasticTranscoder = new AmazonElasticTranscoderClient(account.getCredentials());
			elasticTranscoder.setRegion(Region.getRegion(region));

			List<Pipeline> list = elasticTranscoder.listPipelines().getPipelines();

			int totalItems = list.size();
			for (Pipeline pipeline : list) {
				AwsResource res = new AwsResource(pipeline.getName(), account.getAccountId(), AwsResourceType.ElasticTranscoder, region);
				res.addInfo(AwsTag.Arn, pipeline.getArn());
				stats.add(res);
			}

			LOG.info(totalItems + " Elastic Transcoder pipelines in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of ElasticTranscoder: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for Glacier.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanGlacier(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for Glacier in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonGlacier glacier = new AmazonGlacierClient(account.getCredentials());
			glacier.setRegion(Region.getRegion(region));

			// DescribeVaultRequest dvr = new DescribeVaultRequest();
			ListVaultsRequest lvr = new ListVaultsRequest();
			int totalItems = 0;
			for (DescribeVaultOutput dvo : glacier.listVaults(lvr).getVaultList()) {
				AwsResource res = new AwsResource(dvo.getVaultName(), account.getAccountId(), AwsResourceType.Glacier, region);
				res.addInfo("NumberOfArchives", dvo.getNumberOfArchives());
				res.addInfo("VaultARN", dvo.getVaultARN());
				res.addInfo(AwsTag.SizeInBytes, dvo.getSizeInBytes());
				stats.add(res);
				totalItems++;
			}

			LOG.info(totalItems + " Glacier in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			if (ase.getErrorCode().contains("AccessDenied")) {
				LOG.info("Access denied for Glacier in region " + region.getName() + " in account " + account.getAccountId());
			} else {
				LOG.error("Exception of Glacier: " + ase.getMessage());
			}
		}
	}

	/**
	 * Collect data for Kinesis.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanKinesis(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for Kinesis in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonKinesis kinesis = new AmazonKinesisClient(account.getCredentials());
			kinesis.setRegion(Region.getRegion(region));

			List<String> list = kinesis.listStreams().getStreamNames();

			int totalItems = list.size();
			for (String streamName : list) {
				stats.add(new AwsResource(streamName, account.getAccountId(), AwsResourceType.Kinesis, region));
			}

			LOG.info(totalItems + " Kinesis streams in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of Kinesis: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for RDS.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanRDS(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for RDS in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonRDS rds = new AmazonRDSClient(account.getCredentials());
			rds.setRegion(Region.getRegion(region));

			List<DBInstance> list = rds.describeDBInstances().getDBInstances();

			int totalItems = list.size();
			for (DBInstance dbInstance : list) {
				AwsResource res = new AwsResource(dbInstance.getDBName(), account.getAccountId(), AwsResourceType.RDS, region);
				res.addInfo("DBInstanceIdentifier", dbInstance.getDBInstanceIdentifier());
				stats.add(res);
			}

			LOG.info(totalItems + " RDS instances in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of RDS: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for Redshift.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanRedshift(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for Redshift in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonRedshift redshift = new AmazonRedshiftClient(account.getCredentials());
			redshift.setRegion(Region.getRegion(region));

			List<Cluster> list = redshift.describeClusters().getClusters();

			int totalItems = list.size();
			for (Cluster cluster : list) {
				AwsResource res = new AwsResource(cluster.getClusterIdentifier(), account.getAccountId(), AwsResourceType.Redshift, region);
				res.addInfo(AwsTag.DBName, cluster.getDBName());
				stats.add(res);
			}

			LOG.info(totalItems + " Redshift cluster in region " + region.getName() + " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of Redshift: " + ase.getMessage());
		}
	}

	/**
	 * Collect data for SimpleDB.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            region in which the resources should be searched
	 * @param resourceType
	 *            Type of resource to be searched
	 */
	public static void scanResources(AwsStats stats, AwsAccount account, Regions region, AwsResourceType resourceType) {
		switch (resourceType) {
		case CloudFront:
			scanCloudFront(stats, account, region);
			break;
		case CloudSearch:
			scanCloudSearch(stats, account, region);
			break;
		case CloudWatch:
			scanCloudWatch(stats, account, region);
			break;
		case DynamoDB:
			scanDynamoDB(stats, account, region);
			break;
		case EC2:
			scanEC2(stats, account, region);
			break;
		case ElastiCache:
			scanElastiCache(stats, account, region);
			break;
		case ElasticMapReduce:
			scanElasticMapReduce(stats, account, region);
			break;
		case ElasticTranscoder:
			scanElasticTranscoder(stats, account, region);
			break;
		case Glacier:
			scanGlacier(stats, account, region);
			break;
		case Kinesis:
			scanKinesis(stats, account, region);
			break;
		case RDS:
			scanRDS(stats, account, region);
			break;
		case Redshift:
			scanRedshift(stats, account, region);
			break;
		case S3:
			scanS3(stats, account, region);
			break;
		case SNS:
			scanSNS(stats, account, region);
			break;
		case SQS:
			scanSQS(stats, account, region);
			break;
		case SimpleDB:
			scanSimpleDB(stats, account, region);
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
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanS3(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for S3 in region " + region.getName() + " in account " + account.getAccountId());

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

			AmazonS3 s3 = new AmazonS3Client(account.getCredentials());
			// s3.setRegion(Region.getRegion(region));

			List<Bucket> buckets = s3.listBuckets();

			// track time because of long running task
			DateTime startTime = DateTime.now();
			DateTime lastHeartBeat = DateTime.now();
			long totalSize = 0;
			long totalItems = 0;
			for (Bucket bucket : buckets) {
				/*
				 * In order to save bandwidth, an S3 object listing does not
				 * contain every object in the bucket; after a certain point the
				 * S3ObjectListing is truncated, and further pages must be
				 * obtained with the AmazonS3Client.listNextBatchOfObjects()
				 * method.
				 */
				long size = 0;
				long items = 0;
				if (S3_DETAILS) {
					ObjectListing objects = s3.listObjects(bucket.getName());
					do {
						for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
							size += objectSummary.getSize();
							items++;

							if ((DateTime.now().getMillis() - lastHeartBeat.getMillis()) > PROGRESS_TICK) {
								lastHeartBeat = DateTime.now();
								DateTime duration = DateTime.now().minus(startTime.getMillis());
								LOG.info("     still crawling S3 since " + duration.getMillis() / 1000 + " sec: current bucket: " + bucket.getName());
							}
						}
						objects = s3.listNextBatchOfObjects(objects);
					} while (objects.isTruncated());

					totalItems += items;
					totalSize += size;
				}

				// get region of bucket
				Regions bucketRegion = region;
				try {
					bucketRegion = Regions.fromName(s3.getBucketLocation(bucket.getName()));
				} catch (Exception e) {
				}

				AwsResource res = new AwsResource(bucket.getName(), account.getAccountId(), AwsResourceType.S3, bucketRegion);
				res.addInfo(AwsTag.Owner, bucket.getOwner().getDisplayName());
				res.addInfo(AwsTag.SizeInBytes, size);
				res.addInfo(AwsTag.Objects, items);
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
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanSimpleDB(AwsStats stats, AwsAccount account, Regions region) {
		if (region == Regions.EU_CENTRAL_1)
			return;

		LOG.debug("Scan for SimpleDB in region " + region.getName() + " in account " + account.getAccountId());

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
			AmazonSimpleDB simpleDB = new AmazonSimpleDBClient(account.getCredentials());
			simpleDB.setRegion(Region.getRegion(region));

			ListDomainsRequest sdbRequest = new ListDomainsRequest().withMaxNumberOfDomains(100);
			ListDomainsResult sdbResult = simpleDB.listDomains(sdbRequest);

			int totalItems = 0;
			for (String domainName : sdbResult.getDomainNames()) {
				DomainMetadataRequest metadataRequest = new DomainMetadataRequest().withDomainName(domainName);
				DomainMetadataResult domainMetadata = simpleDB.domainMetadata(metadataRequest);
				int items = domainMetadata.getItemCount();
				totalItems += items;
				AwsResource res = new AwsResource(domainName, account.getAccountId(), AwsResourceType.SimpleDB, region);
				res.addInfo(AwsTag.Items, items);
				stats.add(res);
			}

			LOG.info(sdbResult.getDomainNames().size() + " SimpleDB domains containing a total of " + totalItems + " items in region " + region.getName()
					+ " in account " + account.getAccountId());
		} catch (AmazonServiceException ase) {
			LOG.error("Exception of SimpleDB: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of SimpleDB: " + ex.getMessage());
		}
	}

	/**
	 * Collect data for SNS.
	 *
	 * @param stats
	 *            current statistics object.
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanSNS(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for SNS in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonSNS sns = new AmazonSNSClient(account.getCredentials());
			sns.setRegion(Region.getRegion(region));

			int totalApps = 0;
			for (PlatformApplication app : sns.listPlatformApplications().getPlatformApplications()) {
				AwsResource res = new AwsResource(app.getPlatformApplicationArn(), account.getAccountId(), AwsResourceType.SNS, region);
				stats.add(res);
				totalApps++;
			}

			LOG.info(totalApps + " SNS applications in region " + region.getName() + " in account " + account.getAccountId());

			int totalSubscriptions = 0;
			for (Subscription subscription : sns.listSubscriptions().getSubscriptions()) {
				AwsResource res = new AwsResource(subscription.getSubscriptionArn(), account.getAccountId(), AwsResourceType.SNS, region);
				res.addInfo(AwsTag.Owner, subscription.getOwner());
				res.addInfo("Endpoint", subscription.getEndpoint());
				res.addInfo("TopicArn", subscription.getTopicArn());
				stats.add(res);
				totalSubscriptions++;
			}

			LOG.info(totalSubscriptions + " SNS subscriptions in region " + region.getName() + " in account " + account.getAccountId());

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
	 * @param account
	 *            currently used credentials object.
	 * @param region
	 *            currently used aws region.
	 */
	public static void scanSQS(AwsStats stats, AwsAccount account, Regions region) {
		LOG.debug("Scan for SQS in region " + region.getName() + " in account " + account.getAccountId());

		try {
			AmazonSQSClient sqs = new AmazonSQSClient(account.getCredentials());
			sqs.setRegion(Region.getRegion(region));

			int totalQueues = 0;
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				AwsResource res = new AwsResource(queueUrl, account.getAccountId(), AwsResourceType.SQS, region);
				stats.add(res);
				totalQueues++;
			}

			LOG.info(totalQueues + " SQS queues in region " + region.getName() + " in account " + account.getAccountId());

		} catch (AmazonServiceException ase) {
			LOG.error("Exception of SQS: " + ase.getMessage());
		} catch (Exception ex) {
			LOG.error("Exception of SQS: " + ex.getMessage());
		}
	}
}
