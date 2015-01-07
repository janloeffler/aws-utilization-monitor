FROM docker-registry2.hackweek.aws.zalando/zalando-java:8u25-1

ADD target/awsutilizationmonitor.jar /awsutilizationmonitor.jar

EXPOSE 8080

CMD java -jar /awsutilizationmonitor.jar
