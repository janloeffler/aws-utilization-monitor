FROM docker-registry.zalando/tsarnowski/zalando-java:8u31-1

MAINTAINER Jan Löffler <jan.loeffler@zalando.de>

ADD target/aws-utilization-monitor.jar /aws-utilization-monitor.jar

EXPOSE 8080

CMD java -jar /aws-utilization-monitor.jar
