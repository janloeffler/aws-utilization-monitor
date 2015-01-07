package de.zalando.platform.awsutilizationmonitor.api;

public class AwsResource {
    
    String name = "";
    String owner = "";
    AwsResourceType resourceType = AwsResourceType.Unknown;
    String info = "";
    
    public AwsResource (String name, String owner, AwsResourceType resourceType) { 
        this (name, owner, resourceType, "");
    }
    
    public AwsResource (String name, String owner, AwsResourceType resourceType, String info) {
        this.name = name;
        this.owner = owner;
        this.resourceType = resourceType;
        this.info = info;
    }
}
