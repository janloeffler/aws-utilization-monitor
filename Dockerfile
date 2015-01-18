FROM docker-registry2.hackweek.aws.zalando/zalando-java:8u25-1

MAINTAINER Jan Löffler <jan.loeffler@zalando.de>

ADD target/aws-utilization-monitor.jar /aws-utilization-monitor.jar

EXPOSE 8080

CMD java -jar /aws-utilization-monitor.jar
