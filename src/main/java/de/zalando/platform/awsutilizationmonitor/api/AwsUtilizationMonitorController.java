package de.zalando.platform.awsutilizationmonitor.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
final class AwsUtilizationMonitorController {
    public static final Logger LOG = LoggerFactory.getLogger(AwsUtilizationMonitorController.class);
    
    @RequestMapping("/")
    @ResponseBody
    String home() {
        LOG.info("called /");

        return "<html><header><style>p, li, ul, a { font-family:'Courier New', Arial; }</style></header><body><h1>AWS Utilization Statistics</h1><p><ul>" 
        		+ "<li><a href=/apps/>/apps/</a> List apps</li>"
        		+ "<li><a href=/apps/app_1/>/apps/{app_name}/</a> Show app with name \"app_1\"</li>"
        		+ "<li><a href=/owners/>/owners/</a> List owners</li>"
        		+ "<li><a href=/owners/Jan%20Löffler/>/owners/{owner_name}/</a> Show resources used by owner with name \"Jan Löffler\"</li>"
           		+ "<li><a href=/search/banana/>/search/{search_pattern}/</a> Show app with name \"banana\"</li>"
           	    + "<li><a href=/statistics/>/statistics/</a> Show statistics about resource usage</li>"
           	    + "<li><a href=/test/>/test/</a> Generate test data</li>"
           	    + "<li><a href=/test/30>/test/{maxItems}</a> Generate test data with 30 items</li>"
           	    + "<li><a href=/clear/>/clear/</a> Clear data cache</li>"
           	    + "<li><a href=/health/>/health/</a> Show health</li>"
        		+ "</ul></p></body></html>";        		
    }
    
    @RequestMapping("/apps/")
    @ResponseBody
    String apps() {
        LOG.info("called /apps");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        
        Gson gson = new Gson();        
        return gson.toJson(collector.getStats());
    }    
    
    @RequestMapping("/apps/{appName}")
    @ResponseBody
    String apps(@PathVariable String appName) {
        LOG.info("called /apps/" + appName);
        
        AwsStatsCollector collector = new AwsStatsCollector();      
        AwsResource res = collector.getStats().getResource(appName);
        
        if (res == null) {
        	return "ERROR: No resource found with name \"" + appName + "\"!";
        } else {        	
	        Gson gson = new Gson();	        
	        return gson.toJson(res);       
        }
    }
    
    @RequestMapping("/apps2/")
    @ResponseBody
    AwsStats apps2() {
        LOG.info("called /apps2");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        collector.generateSampleData(30);
        
        return collector.getStats();
    }

    @RequestMapping("/owners/")
    @ResponseBody
    String owners() {
        LOG.info("called /owners");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        
        Gson gson = new Gson();        
        return gson.toJson(collector.getStats().getOwners());
    }    
    
    @RequestMapping("/owners/{ownerName}")
    @ResponseBody
    String owners(@PathVariable String ownerName) {
        LOG.info("called /owners/" + ownerName);
        
        AwsStatsCollector collector = new AwsStatsCollector();      
        AwsResource[] results = collector.getStats().getResourcesByOwner(ownerName);
        
        if ((results == null) || (results.length == 0)) {
        	return "ERROR: No resource found for owner \"" + ownerName + "\"!";
        } else {        	
	        Gson gson = new Gson();	        
	        return gson.toJson(results);       
        }
    }
    
    @RequestMapping("/search/{searchPattern}")
    @ResponseBody
    String search(@PathVariable String searchPattern) {
        LOG.info("called /search/" + searchPattern);
        
        AwsStatsCollector collector = new AwsStatsCollector();      
        AwsResource[] results = collector.getStats().searchResource(searchPattern);
        
        if ((results == null) || (results.length == 0)) {
        	return "ERROR: No resource found with pattern \"" + searchPattern + "\"!";
        } else {        	
	        Gson gson = new Gson();	        
	        return gson.toJson(results);       
        }
    }
    
    @RequestMapping("/statistics/")
    @ResponseBody
    String statistics() {
        LOG.info("called /statistics");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        AwsStats stats = collector.getStats();
       
        AwsResource[] resources = stats.getAllResources();
        String[] owners = stats.getOwners();
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
    String test() {
        LOG.info("called /test");
        
        AwsStatsCollector collector = new AwsStatsCollector();
        collector.generateSampleData(30);
       
        Gson gson = new Gson();       
        return gson.toJson(collector.getStats());        
    }
   
    @RequestMapping("/test/{maxItems}")
    @ResponseBody
    String test(@PathVariable int maxItems) {
        LOG.info("called /test/" + maxItems);
        
        AwsStatsCollector collector = new AwsStatsCollector();
        collector.generateSampleData(maxItems);
       
        Gson gson = new Gson();       
        return gson.toJson(collector.getStats());        
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
}