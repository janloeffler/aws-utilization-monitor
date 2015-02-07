package de.zalando.platform.awsutilizationmonitor.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.regions.Regions;

@RestController
final class AwsUtilizationMonitorController {
	public static final Logger LOG = LoggerFactory.getLogger(AwsUtilizationMonitorController.class);

	private AwsStatsCollector collector;

	@Autowired
	public AwsUtilizationMonitorController(AwsStatsCollector collector) {
		this.collector = collector;
	}

	@RequestMapping("/accounts/")
	@ResponseBody
	String[] accounts() {
		LOG.info("called /accounts/");

		return collector.getStats().getAccounts();
	}

	@RequestMapping("/accounts/{accountName}")
	@ResponseBody
	AwsResource[] accounts(@PathVariable String accountName) {
		LOG.info("called /accounts/" + accountName);

		AwsResource[] results = collector.getStats().getResourcesByAccount(accountName);

		if ((results == null) || (results.length == 0)) {
			LOG.info("No resource found for account \"" + accountName + "\"!");
		}

		return results;
	}

	@RequestMapping("/apps/")
	@ResponseBody
	String[] apps() {
		LOG.info("called /apps/");

		return collector.getStats().getApps();
	}

	@RequestMapping("/apps/{appName}")
	@ResponseBody
	AwsResource[] apps(@PathVariable String appName) {
		try {
			appName = URLDecoder.decode(appName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Cannot decode \"" + appName + "\": " + e.getMessage());
		}

		LOG.info("called /apps/" + appName);

		AwsResource[] res = collector.getStats().getAppInstances(appName);

		if ((res == null) || (res.length == 0)) {
			LOG.info("No app found with name \"" + appName + "\"!");
		}

		return res;
	}

	@RequestMapping("/clear/")
	@ResponseBody
	String clear() {
		LOG.info("called /clear/");

		collector.clearCache();

		return "Cache empty";
	}

	String encodeParam(String param) {
		try {
			// UrlEscapers.urlPathSegmentEscaper().escape(s1)
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Cannot encode \"" + param + "\": " + e.getMessage());
		}

		return param;
	}

	@RequestMapping("/force/")
	@ResponseBody
	AwsStats force() {
		LOG.info("called /force/");

		return collector.forceAddStats();
	}

	@RequestMapping("/health/")
	@ResponseBody
	String health() {
		return "OK";
	}

	@RequestMapping("/")
	@ResponseBody
	String home() {
		LOG.info("called /");

		return "<html><header><style>p, li, ul, a { font-family:'Courier New', Arial; }</style></header><body><h1>AWS Utilization Statistics</h1><p><ul>"
				+ "<li><a href=/apps/>/apps/</a> List EC2 based apps</li>"
				+ "<li><a href=/apps/app_1/>/apps/{app_name}/</a> Show EC2 based apps with name \"app_1\"</li>"
				+ "<li><a href=/instancetypes/>/instancetypes/</a> List used EC2 instance types</li>"
				+ "<li><a href=/instancetypes/"
				+ encodeParam("t2.micro")
				+ "/>/instancetypes/{instance_type}/</a> Show EC2 based apps with instance type \"t2.micro\"</li>"
				+ "<li><a href=/resources/>/resources/</a> List resources</li>"
				+ "<li><a href=/resources/app_1/>/resources/{resource_name}/</a> Show resources with name \"app_1\"</li>"
				+ "<li><a href=/keys/>/keys/</a> List keys</li>"
				+ "<li><a href=/keys/PublicDnsName/>/keys/{key_name}/</a> Show resources that contain a value with the key \"PublicDnsName\"</li>"
				+ "<li><a href=/keys/Team/>/keys/Team/</a> List team names if \"team\" tag was specified</li>"
				+ "<li><a href=/accounts/>/accounts/</a> List accounts</li>"
				+ "<li><a href=/accounts/123456789012/>/accounts/{account_name}/</a> Show resources used by account with name \"123456789012\"</li>"
				+ "<li><a href=/regions/>/regions/</a> List regions</li>"
				+ "<li><a href=/regions/EU_WEST_1/>/regions/{region_name}/</a> Show resources used by region with name \"EU_WEST_1\"</li>"
				+ "<li><a href=/search/banana/>/search/{search_pattern}/</a> Show app with name \"banana\"</li>"
				+ "<li><a href=/values/Team/Platform/>/values/{key_name}/{value_pattern}/</a> Show resources that contain a value with the key \"Team\" and the pattern \"Platform\"</li>"
				+ "<li><a href=/statistics/>/statistics/</a> Show statistics about resource usage</li>"
				+ "<li><a href=/summary/>/summary/</a> Show summary KPIs only about resource usage</li>"
				+ "<li><a href=/test/>/test/</a> Generate test data</li>" + "<li><a href=/test/30>/test/{maxItems}</a> Generate test data with 30 items</li>"
				+ "<li><a href=/clear/>/clear/</a> Clear data cache</li>" + "<li><a href=/health/>/health/</a> Show health</li>" + "</ul></p></body></html>";
	}

	@RequestMapping("/instancetypes/")
	@ResponseBody
	String[] instancetypes() {
		LOG.info("called /instancetypes/");

		return collector.getStats().getUsedEC2InstanceTypes();
	}

	@RequestMapping("/instancetypes/{instanceType}")
	@ResponseBody
	AwsResource[] instancetypes(@PathVariable String instanceType) {
		LOG.info("called /instancetypes/" + instanceType);

		AwsResource[] res = collector.getStats().getResourcesByEC2InstanceType(instanceType);

		if ((res == null) || (res.length == 0)) {
			LOG.info("No app found with instance type \"" + instanceType + "\"!");
		}

		return res;
	}

	@RequestMapping("/keys/")
	@ResponseBody
	String[] keys() {
		LOG.info("called /keys/");

		return collector.getStats().getKeys();
	}

	@RequestMapping("/keys/{keyName}")
	@ResponseBody
	Object[] keys(@PathVariable String keyName) {
		LOG.info("called /keys/" + keyName);

		collector.getStats().getValues(keyName);
		Object[] results = collector.getStats().getValues(keyName);

		if ((results == null) || (results.length == 0)) {
			LOG.info("No resource found with key \"" + keyName + "\"!");
		}

		return results;
	}

	@RequestMapping("/regions/")
	@ResponseBody
	Regions[] regions() {
		LOG.info("called /regions/");

		return collector.getStats().getRegions();
	}

	@RequestMapping("/regions/{region}")
	@ResponseBody
	AwsResource[] regions(@PathVariable String region) {
		LOG.info("called /regions/" + region);

		AwsResource[] results = collector.getStats().getResourcesByRegion(Regions.valueOf(region));

		if ((results == null) || (results.length == 0)) {
			LOG.info("No resource found for region \"" + region + "\"!");
		}

		return results;
	}

	@RequestMapping("/resources/")
	@ResponseBody
	AwsStats resources() {
		LOG.info("called /resources/");

		return collector.getStats();
	}

	@RequestMapping("/resources/{resourceName}")
	@ResponseBody
	AwsResource resources(@PathVariable String resourceName) {
		LOG.info("called /resources/" + resourceName);

		AwsResource res = collector.getStats().getResource(resourceName);

		if (res == null) {
			LOG.info("No resource found with name \"" + resourceName + "\"!");
		}

		return res;
	}

	@RequestMapping("/search/{searchPattern}")
	@ResponseBody
	AwsResource[] search(@PathVariable String searchPattern) {
		try {
			searchPattern = URLDecoder.decode(searchPattern, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Cannot decode \"" + searchPattern + "\": " + e.getMessage());
		}

		LOG.info("called /search/" + searchPattern);

		AwsResource[] results = collector.getStats().searchResources(searchPattern);

		if ((results == null) || (results.length == 0)) {
			LOG.info("No resource found with pattern \"" + searchPattern + "\"!");
		}

		return results;
	}

	@RequestMapping("/statistics/")
	@ResponseBody
	String statistics() {
		LOG.info("called /statistics");

		AwsStats stats = collector.getStats();
		StringBuilder s = new StringBuilder();

		AwsResource[] resources = stats.getResources();
		String[] accounts = stats.getAccounts();
		Regions[] regions = stats.getRegions();
		AwsResourceType[] resourceTypes = stats.getUsedResourceTypes();
		String[] teams = stats.getTeams();
		String[] apps = stats.getApps();
		String[] instanceTypes = stats.getUsedEC2InstanceTypes();

		s.append("<html><header><style>p, li, ul, a { font-family:'Courier New', Arial; }</style></header><body><h1>AWS Utilization Statistics</h1><p>"
				+ "<a href=/>Back to overview</a><ul>" + "<li><a href=/resources/>" + resources.length + "</a> resources used</li>");

		/* regions */
		s.append("<li><a href=/regions/>" + regions.length + "</a> regions</li>" + "<ul>");

		for (Regions region : regions) {
			int amount = stats.getResourcesByRegion(region).length;
			s.append("<li><a href=/regions/" + region + ">" + amount + "</a> resources in \"" + region + "\"</li>");
		}

		s.append("</ul>");

		/* resource types */
		s.append("<li>" + resourceTypes.length + " AWS components used</li>" + "<ul>");

		for (AwsResourceType resourceType : resourceTypes) {
			int amount = stats.getResources(resourceType).length;
			s.append("<li><a href=/types/" + resourceType + ">" + amount + "</a> " + resourceType + "</li>");
		}

		s.append("</ul>");

		/* accounts */
		s.append("<li><a href=/accounts/>" + accounts.length + "</a> accounts</li>" + "<ul>");

		for (String accountName : accounts) {
			int amount = stats.getResourcesByAccount(accountName).length;
			s.append("<li><a href=/accounts/" + accountName + ">" + amount + "</a> resources by \"" + accountName + "\"</li>");
		}

		s.append("</ul>");

		/* teams */
		s.append("<li><a href=/keys/Team/>" + teams.length + "</a> teams</li>" + "<ul>");

		for (String teamName : teams) {
			int amount = stats.searchResources("Team", teamName).length;
			s.append("<li><a href=/values/Team/" + encodeParam(teamName) + ">" + amount + "</a> resources by \"" + teamName + "\"</li>");
		}

		s.append("</ul>");

		/* EC2 apps */
		s.append("<li><a href=/apps/>" + apps.length + "</a> EC2 based apps</li>" + "<ul>");

		for (String appName : apps) {
			int amount = stats.getAppInstances(appName).length;
			s.append("<li><a href=/apps/" + encodeParam(appName) + ">" + amount + "</a> instances of \"" + appName + "\"</li>");
		}

		s.append("</ul>");

		/* EC2 instance types */
		s.append("<li><a href=/instancetypes/>" + instanceTypes.length + "</a> used EC2 instance types</li>" + "<ul>");

		for (String instanceType : instanceTypes) {
			int amount = stats.getResourcesByEC2InstanceType(instanceType).length;
			s.append("<li><a href=/instancetypes/" + encodeParam(instanceType) + ">" + amount + "</a> instances with \"" + instanceType + "\"</li>");
		}

		s.append("</ul>");

		s.append("</ul></p></body></html>");

		return s.toString();
	}

	@RequestMapping("/summary/")
	@ResponseBody
	AwsStatsSummary summary() {
		LOG.info("called /summary/");

		return collector.getStats().getSummary();
	}

	@RequestMapping("/test/")
	@ResponseBody
	AwsStats test() {
		LOG.info("called /test/");

		collector.generateSampleData(30);

		return collector.getStats();
	}

	@RequestMapping("/test/{maxItems}")
	@ResponseBody
	AwsStats test(@PathVariable int maxItems) {
		LOG.info("called /test/" + maxItems);

		collector.generateSampleData(maxItems);

		return collector.getStats();
	}

	@RequestMapping("/types/")
	@ResponseBody
	AwsResourceType[] types() {
		LOG.info("called /types/");

		return collector.getStats().getUsedResourceTypes();
	}

	@RequestMapping("/types/{resourceType}")
	@ResponseBody
	AwsResource[] types(@PathVariable String resourceType) {
		LOG.info("called /types/" + resourceType);

		AwsResource[] results = collector.getStats().getResources(AwsResourceType.valueOf(resourceType));

		if ((results == null) || (results.length == 0)) {
			LOG.info("No resource found of type \"" + resourceType + "\"!");
		}

		return results;
	}

	@RequestMapping("/values/{key}/{value}")
	@ResponseBody
	AwsResource[] values(@PathVariable String key, @PathVariable String value) {
		try {
			value = URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Cannot decode \"" + value + "\": " + e.getMessage());
		}

		LOG.info("called /values/" + key + "/" + value);

		AwsResource[] results = collector.getStats().searchResources(key, value);

		if ((results == null) || (results.length == 0)) {
			LOG.info("No resource found with key \"" + key + "\" and value \"" + value + "\"!");
		}

		return results;
	}
}