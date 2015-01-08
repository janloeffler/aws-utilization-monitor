package de.zalando.platform.awsutilizationmonitor.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class AwsUtilizationMonitorController {
    public static final Logger LOG = LoggerFactory.getLogger(AwsUtilizationMonitorController.class);
    
    @RequestMapping("/apps/")
    @ResponseBody
    AwsStats apps() {
        LOG.info("called /apps");
        
        AwsStatsCollector collector = new AwsStatsCollector();        
        return collector.getStats();
    }
    
    @RequestMapping("/apps/{appName}")
    @ResponseBody
    AwsResource apps(@PathVariable String appName) {
        LOG.info("called /apps/" + appName);
        
        AwsStatsCollector collector = new AwsStatsCollector();      
        AwsResource res = collector.getStats().getResource(appName);
               
        if (res == null) {
        	LOG.info("No resource found with name \"" + appName + "\"!");
        }        	
        
        return res;
    }    
    
    @RequestMapping("/clear/")
    @ResponseBody
    String clear() {
        LOG.info("called /clear");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        collector.clearCache();
       
        return "Cache empty";        
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
        		+ "<li><a href=/apps/>/apps/</a> List apps</li>"
        		+ "<li><a href=/apps/app_1/>/apps/{app_name}/</a> Show app with name \"app_1\"</li>"
        		+ "<li><a href=/owners/>/owners/</a> List owners</li>"
        		+ "<li><a href=/owners/Jan%20Löffler/>/owners/{owner_name}/</a> Show resources used by owner with name \"Jan Löffler\"</li>"
        		+ "<li><a href=/regions/>/regions/</a> List regions</li>"
        		+ "<li><a href=/regions/eu-west-1/>/regions/{region_name}/</a> Show resources used by region with name \"eu-west-1\"</li>"
           		+ "<li><a href=/search/banana/>/search/{search_pattern}/</a> Show app with name \"banana\"</li>"
           	    + "<li><a href=/statistics/>/statistics/</a> Show statistics about resource usage</li>"
           	    + "<li><a href=/test/>/test/</a> Generate test data</li>"
           	    + "<li><a href=/test/30>/test/{maxItems}</a> Generate test data with 30 items</li>"
           	    + "<li><a href=/clear/>/clear/</a> Clear data cache</li>"
           	    + "<li><a href=/health/>/health/</a> Show health</li>"
        		+ "</ul></p></body></html>";        		
    }
    
    @RequestMapping("/owners/")
    @ResponseBody
    String[] owners() {
        LOG.info("called /owners");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        return collector.getStats().getOwners();
    }
    
    @RequestMapping("/owners/{ownerName}")
    @ResponseBody
    AwsResource[] owners(@PathVariable String ownerName) {
        LOG.info("called /owners/" + ownerName);
        
        AwsStatsCollector collector = new AwsStatsCollector();      
        AwsResource[] results = collector.getStats().getResourcesByOwner(ownerName);
        
        if ((results == null) || (results.length == 0)) {
        	LOG.info("No resource found for owner \"" + ownerName + "\"!");
        } 

        return results;
    }
    
    @RequestMapping("/regions/")
    @ResponseBody
    String[] regions() {
        LOG.info("called /regions");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        return collector.getStats().getRegions();
    }
    
    @RequestMapping("/regions/{ownerName}")
    @ResponseBody
    AwsResource[] regions(@PathVariable String region) {
        LOG.info("called /regions/" + region);
        
        AwsStatsCollector collector = new AwsStatsCollector();      
        AwsResource[] results = collector.getStats().getResourcesByRegion(region);
        
        if ((results == null) || (results.length == 0)) {
        	LOG.info("No resource found for region \"" + region + "\"!");
        } 

        return results;
    }
    
    @RequestMapping("/search/{searchPattern}")
    @ResponseBody
    AwsResource[] search(@PathVariable String searchPattern) {
        LOG.info("called /search/" + searchPattern);
        
        AwsStatsCollector collector = new AwsStatsCollector();      
        AwsResource[] results = collector.getStats().searchResource(searchPattern);
        
        if ((results == null) || (results.length == 0)) {
        	LOG.info("No resource found with pattern \"" + searchPattern + "\"!");
        } 
        
        return results;
    }
   
    @RequestMapping("/statistics/")
    @ResponseBody
    String statistics() {
        LOG.info("called /statistics");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        AwsStats stats = collector.getStats();
       
        AwsResource[] resources = stats.getAllResources();
        String[] owners = stats.getOwners();
        String[] regions = stats.getRegions();
        AwsResourceType[] resourceTypes = stats.getUsedResourceTypes();
        
        StringBuilder s = new StringBuilder();
        
        s.append("<html><header><style>p, li, ul, a { font-family:'Courier New', Arial; }</style></header><body><h1>AWS Utilization Statistics</h1><p>"
        		+ "<a href=/>Back to overview</a><ul>" 
				+ "<li><a href=/apps/>" + resources.length + "</a> resources used</li>"
				+ "<li><a href=/owners/>" + owners.length + "</a> owners</li>"
				+ "<ul>");

        for (String ownerName : owners) {        	
        	int amount = stats.getResourcesByOwner(ownerName).length;
        	try {
				s.append("<li><a href=/owners/" + URLEncoder.encode(ownerName, "UTF-8") + ">" + amount + "</a> resources by \"" + ownerName + "\"</li>");
			} catch (UnsupportedEncodingException e) {
				LOG.error("Cannot encode \"" + ownerName + "\": " + e.getMessage());
			}
        }
        
        s.append("</ul>"
        		+ "<li><a href=/regions/>" + regions.length + "</a> regions</li>"
        		+ "<ul>");

		for (String region : regions) {        	
			int amount = stats.getResourcesByRegion(region).length;
			s.append("<li><a href=/regions/" + region + ">" + amount + "</a> resources in \"" + region + "\"</li>");
		}

        s.append("</ul>"
				+ "<li>" + resourceTypes.length + " AWS components used</li>"
				+ "<ul>");
        
        for (AwsResourceType resourceType : resourceTypes) {        	
        	int amount = stats.getResources(resourceType).length;
        	s.append("<li><a href=/search/" + resourceType + ">" + amount + "</a> " + resourceType + "</li>");
        }
        
        s.append("</ul>"
				+ "</ul></p></body></html>");
        
        return s.toString();
    }
   
    @RequestMapping("/test/")
    @ResponseBody
    AwsStats test() {
        LOG.info("called /test");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        collector.generateSampleData(30);
        
        return collector.getStats();        
    }
   
    @RequestMapping("/test/{maxItems}")
    @ResponseBody
    AwsStats test(@PathVariable int maxItems) {
        LOG.info("called /test/" + maxItems);
        
        AwsStatsCollector collector = new AwsStatsCollector();
        collector.generateSampleData(maxItems);
       
        return collector.getStats();        
    }    
}