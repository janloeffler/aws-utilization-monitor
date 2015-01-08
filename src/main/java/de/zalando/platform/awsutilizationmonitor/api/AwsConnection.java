/**
 * This class keeps the connection to AWS open and caches the result set for statistics.
 */
package de.zalando.platform.awsutilizationmonitor.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.elasticache.AmazonElastiCache;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.Pipeline;
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

/**
 * @author jloeffler
 *
 * This class collects usage of Amazon Webservices components such as EC2,
 * SimpleDB, and S3.
 *
 * In order to use the services in this sample, you need:
 *
 *  - A valid Amazon Web Services account. You can register for AWS at:
 *       https://aws-portal.amazon.com/gp/aws/developer/registration/index.html
 *
 *  - Your account's Access Key ID and Secret Access Key:
 *       http://aws.amazon.com/security-credentials
 *
 *  - A subscription to Amazon EC2. You can sign up for EC2 at:
 *       http://aws.amazon.com/ec2/
 *
 *  - A subscription to Amazon SimpleDB. You can sign up for Simple DB at:
 *       http://aws.amazon.com/simpledb/
 *
 *  - A subscription to Amazon S3. You can sign up for S3 at:
 *       http://aws.amazon.com/s3/
 */
public final class AwsConnection {
    /**
	 * @return instance of connection to AWS that caches results set for a certain time.
	 */
	public static AwsConnection getInstance () {
		synchronized (lockObject) {
			if (instance == null) {
				instance = new AwsConnection();
			}		
		}
		
		return instance;
	}

	/*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (~/.aws/credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WANRNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */
    
    public static final Logger LOG = LoggerFactory.getLogger(AwsConnection.class);
 
	// set caching time to 1 hour - afterwards the aws api will be called again to collect resource usage
    private final static int CACHE_DURATION = 60 * 60 * 1000;
	private static AwsConnection instance = null; 	
	private static DateTime lastCollectTime = DateTime.now();  
	
    private static Object lockObject = new Object();
	
	private AwsStats stats = null;    
	
    /**
     * Clear the cache with statistics about resource usage so that next request of getStats() loads resource information directly from AWS again.
     */
    public void clearCache() {
        this.stats.clear();
        this.stats = null;
        LOG.info("Cache cleared");
    }	
	
    /**
     * Amazon EC2
     *
     * The AWS EC2 client allows you to create, delete, and administer
     * instances programmatically.
     *
     * In this sample, we use an EC2 client to get a list of all the
     * availability zones, and all instances sorted by reservation id.
     */
	public void collectDataFromAws() {
        LOG.info("Login to AWS and collect resource list");
        
        AwsStats currentStats = new AwsStats(); 
        
        try {
	        /*
	         * The ProfileCredentialsProvider will return your [default]
	         * credential profile by reading from the credentials file located at
	         * (~/.aws/credentials).
	         */
	        AWSCredentials credentials = null;
	        try {
	            credentials = new ProfileCredentialsProvider().getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (~/.aws/credentials), and is in valid format.",
	                    e);
	        }
	    	        
	    	lastCollectTime = DateTime.now();
			
	    	ArrayList<Region> regions = new ArrayList<Region>();
	    	regions.add(Region.getRegion(Regions.EU_WEST_1));
	    	regions.add(Region.getRegion(Regions.EU_CENTRAL_1));
	    	// regions.add(Region.getRegion(Regions.US_WEST_1));
	    
	    	for (Region region : regions) {
		    	collectEC2Data(currentStats, credentials, region);
		    	collectS3Data(currentStats, credentials, region);
		    	collectSimpleDBData(currentStats, credentials, region);
		    	collectDynamoDBData(currentStats, credentials, region);	
		    	collectElastiCacheData(currentStats, credentials, region);	
		    	collectElasticTranscoderData(currentStats, credentials, region);	
		    	collectKinesisData(currentStats, credentials, region);	
		    	collectRedshiftData(currentStats, credentials, region);	
		    	collectRDSData(currentStats, credentials, region);	
		    	collectElasticTranscoderData(currentStats, credentials, region);	
		    	collectGlacierData(currentStats, credentials, region);	
		    	collectElasticMapReduceData(currentStats, credentials, region);
	    	}
        } catch (Exception e) {
        	LOG.error("Connect to AWS failed: " + e.getMessage());
        }		      
                
        this.stats = currentStats;
    }
	
	/**
	 * Collect data for DynamoDB.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectDynamoDBData(AwsStats stats, AWSCredentials credentials, Region region) {
        /*
         * Amazon DynamoDB
         */
        try {
	    	AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(credentials);
	    	dynamoDB.setRegion(region);
	    	
	    	List<String> list = dynamoDB.listTables().getTableNames();

            int totalItems = list.size();
            for (String tableName : list) {
             	stats.add(new AwsResource(tableName, "", AwsResourceType.DynamoDB, region.getName()));                
            }

            LOG.info(totalItems + " DynamoDB tables");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonDynamoDB");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}
	
	/**
	 * Collect data for EC2.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectEC2Data(AwsStats stats, AWSCredentials credentials, Region region) {
		try {
			//AmazonEC2 ec2 = region.createClient(AmazonEC2Client.class, credentials, config)
			AmazonEC2 ec2 = new AmazonEC2Client(credentials);
			ec2.setRegion(region);
			
            DescribeRegionsResult regionsResult = ec2.describeRegions();
            LOG.info(regionsResult.getRegions().size() + " EC2 regions");
            
            DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
            LOG.info(availabilityZonesResult.getAvailabilityZones().size() + " Availability Zones");

            DescribeImagesResult imagesResult = ec2.describeImages();
            LOG.info(imagesResult.getImages().size() + " EC2 images");
           
            DescribeSecurityGroupsResult securityGroupsResult = ec2.describeSecurityGroups();
            LOG.info(securityGroupsResult.getSecurityGroups().size() + " EC2 security groups");
            
            DescribeVolumesResult volumesResult = ec2.describeVolumes();
            LOG.info(volumesResult.getVolumes().size() + " EC2 volumes");
            
            DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
            List<Reservation> reservations = describeInstancesRequest.getReservations();
            Set<Instance> instances = new HashSet<Instance>();
           
            for (Reservation reservation : reservations) {                
            	instances.addAll(reservation.getInstances());
            	String info = "instances=" + reservation.getInstances().size()
            			+ "; requester=" + reservation.getRequesterId();
    			stats.add(new AwsResource(reservation.getReservationId(), reservation.getOwnerId(), AwsResourceType.EC2, region.getName(), info));
            }

            LOG.info(instances.size() + " EC2 instances running");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonEC2");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}
	
	/**
	 * Collect data for ElastiCache.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectElastiCacheData(AwsStats stats, AWSCredentials credentials, Region region) {
        /*
         * Amazon ElastiCache
         */
        try {
	    	AmazonElastiCache elastiCache = new AmazonElastiCacheClient(credentials);
	    	elastiCache.setRegion(region);
	    	
	    	List<CacheCluster> list = elastiCache.describeCacheClusters().getCacheClusters();

            int totalItems = list.size();
            for (CacheCluster cluster : list) {
            	String info = 
            			", engine=" + cluster.getEngine() + " " + cluster.getEngineVersion()
            			+ "; NumCacheNodes=" + cluster.getNumCacheNodes();
             	stats.add(new AwsResource(cluster.getCacheClusterId(), "", AwsResourceType.ElastiCache, region.getName(), info));                
            }

            LOG.info(totalItems + " ElastiCache");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonElastiCache");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}
	
	/**
	 * Collect data for ElasticMapReduce.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectElasticMapReduceData(AwsStats stats, AWSCredentials credentials, Region region) {
        try {
	    	AmazonElasticMapReduce elasticMapReduce = new AmazonElasticMapReduceClient(credentials);
	    	elasticMapReduce.setRegion(region);
	    	
	    	List<ClusterSummary> list = elasticMapReduce.listClusters().getClusters();

            int totalItems = list.size();
            for (ClusterSummary cs : list) {
             	stats.add(new AwsResource(cs.getName(), "", AwsResourceType.ElasticMapReduce, region.getName()));                
            }

            LOG.info(totalItems + " ElasticMapReduce clusters");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonElasticMapReduce");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}

	/**
	 * Collect data for ElasticTranscoder.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectElasticTranscoderData(AwsStats stats, AWSCredentials credentials, Region region) {
        try {
	    	AmazonElasticTranscoder elasticTranscoder = new AmazonElasticTranscoderClient(credentials);
	    	elasticTranscoder.setRegion(region);
	    	
	    	List<Pipeline> list = elasticTranscoder.listPipelines().getPipelines();

            int totalItems = list.size();
            for (Pipeline pipeline : list) {
             	stats.add(new AwsResource(pipeline.getName(), "", AwsResourceType.ElasticTranscoder, region.getName(), pipeline.getArn()));                
            }

            LOG.info(totalItems + " Elastic Transcoder pipelines");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonElasticTranscoder");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}

	/**
	 * Collect data for Glacier.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectGlacierData(AwsStats stats, AWSCredentials credentials, Region region) {
/*
        try {
	    	AmazonGlacier glacier = new AmazonGlacierClient(credentials);
	    	glacier.setRegion(region);
	    	
	    	List<String> list = glacier..listTables().getTableNames();

            int totalItems = list.size();
            for (String tableName : list) {
             	stats.add(new AwsResource(tableName, "", AwsResourceType.Glacier, region.getName()));                
            }

            LOG.info(totalItems + " Glacier");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonGlacier");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
*/
	}

	/**
	 * Collect data for Kinesis.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectKinesisData(AwsStats stats, AWSCredentials credentials, Region region) {
        try {
	    	AmazonKinesis kinesis = new AmazonKinesisClient(credentials);
	    	kinesis.setRegion(region);
	    	
	    	List<String> list = kinesis.listStreams().getStreamNames();

            int totalItems = list.size();
            for (String streamName : list) {
             	stats.add(new AwsResource(streamName, "", AwsResourceType.Kinesis, region.getName()));                
            }

            LOG.info(totalItems + " Kinesis streams");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonKinesis");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}

	/**
	 * Collect data for RDS.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectRDSData(AwsStats stats, AWSCredentials credentials, Region region) {
        try {
	    	AmazonRDS rds = new AmazonRDSClient(credentials);
	    	rds.setRegion(region);
	    	
	    	List<DBInstance> list = rds.describeDBInstances().getDBInstances();

            int totalItems = list.size();
            for (DBInstance dbInstance : list) {            	
             	stats.add(new AwsResource(dbInstance.getDBName(), "", AwsResourceType.RDS, region.getName(), "identifier=" + dbInstance.getDBInstanceIdentifier()));                
            }

            LOG.info(totalItems + " RDS instances");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonRDS");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}

	/**
	 * Collect data for Redshift.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectRedshiftData(AwsStats stats, AWSCredentials credentials, Region region) {
        try {
	    	AmazonRedshift redshift = new AmazonRedshiftClient(credentials);
	    	redshift.setRegion(region);
	    	
	    	List<Cluster> list = redshift.describeClusters().getClusters();

            int totalItems = list.size();
            for (Cluster cluster : list) {            
             	stats.add(new AwsResource(cluster.getClusterIdentifier(), "", AwsResourceType.Redshift, region.getName(), "DBName=" + cluster.getDBName()));                
            }

            LOG.info(totalItems + " Redshift cluster");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonRedshift");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        }		
	}

	/**
	 * Collect data for S3.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectS3Data(AwsStats stats, AWSCredentials credentials, Region region) {
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
	    	AmazonS3 s3  = new AmazonS3Client(credentials);
	    	s3.setRegion(region);
	    	
	    	List<Bucket> buckets = s3.listBuckets();

            long totalSize  = 0;
            int  totalItems = 0;
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
                do {
                    for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                        size = objectSummary.getSize();
                    	totalSize += size; 
                        totalItems++;
                    }
                    objects = s3.listNextBatchOfObjects(objects);
                } while (objects.isTruncated());

                stats.add(new AwsResource(bucket.getName(), bucket.getOwner().getDisplayName(), AwsResourceType.S3, region.getName(), "size=" + size + " bytes"));
            }

            LOG.info(buckets.size() + " S3 buckets containing " + totalItems + " objects with a total size of " + totalSize + " bytes");
        } catch (AmazonServiceException ase) {
            /*
             * AmazonServiceExceptions represent an error response from an AWS
             * services, i.e. your request made it to AWS, but the AWS service
             * either found it invalid or encountered an error trying to execute
             * it.
             */
        	LOG.error("Exception of AmazonS3");
        	LOG.error("Error Message:    " + ase.getMessage());
        	LOG.error("HTTP Status Code: " + ase.getStatusCode());
        	LOG.error("AWS Error Code:   " + ase.getErrorCode());
        	LOG.error("Error Type:       " + ase.getErrorType());
        	LOG.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            /*
             * AmazonClientExceptions represent an error that occurred inside
             * the client on the local host, either while trying to send the
             * request to AWS or interpret the response. For example, if no
             * network connection is available, the client won't be able to
             * connect to AWS to execute a request and will throw an
             * AmazonClientException.
             */
        	LOG.error("Error Message: " + ace.getMessage());
        }  
	}

	/**
	 * Collect data for SimpleDB.
	 * 
	 * @param stats current statistics object.
	 * @param credentials currently used credentials object.
	 */
	private void collectSimpleDBData(AwsStats stats, AWSCredentials credentials, Region region) {		
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
	    	simpleDB.setRegion(region);
	    	
	    	ListDomainsRequest sdbRequest = new ListDomainsRequest().withMaxNumberOfDomains(100);
            ListDomainsResult sdbResult = simpleDB.listDomains(sdbRequest);

            int totalItems = 0;
            for (String domainName : sdbResult.getDomainNames()) {
                DomainMetadataRequest metadataRequest = new DomainMetadataRequest().withDomainName(domainName);
                DomainMetadataResult domainMetadata = simpleDB.domainMetadata(metadataRequest);
                int items = domainMetadata.getItemCount();
                totalItems += items;
             	stats.add(new AwsResource(domainName, "", AwsResourceType.SimpleDB, region.getName(), "items=" + items));                
            }

            LOG.info(sdbResult.getDomainNames().size() + " SimpleDB domains containing a total of " + totalItems + " items");
        } catch (AmazonServiceException ase) {
        	LOG.error("Exception of AmazonSimpleDB");
        	LOG.error("Caught Exception: " + ase.getMessage());
        	LOG.error("Reponse Status Code: " + ase.getStatusCode());
        	LOG.error("Error Code: " + ase.getErrorCode());
        	LOG.error("Request ID: " + ase.getRequestId());
        } catch (Exception ex) { 
        	LOG.error("Exception of AmazonSimpleDB: " + ex.getMessage());
        }
	}
		
    /**
     * @return filled statistics object containing all used resources.
     */
    public AwsStats getStats() {
    	return getStats(true);
    }
    
    /**
     * @param collectFromAws automatically collect usage data from AWS.
     * @return filled statistics object containing all used resources.
     */
    public AwsStats getStats(boolean collectFromAws) {
	    if ((stats == null) 
	    		|| (stats.getAllResources().length == 0) 
	    		|| ((DateTime.now().getMillis() - lastCollectTime.getMillis()) > CACHE_DURATION)) { 
	    	
	    	if (collectFromAws) {
	    		collectDataFromAws();
	    	}
        }
	    
	    if (stats == null) {
	    	stats = new AwsStats();
	    }
	    
    	return stats;
    }	
}
