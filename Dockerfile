FROM zalando/openjdk:8u40-b09-1

ADD target/aws-utilization-monitor.jar /aws-utilization-monitor.jar

EXPOSE 8080

CMD java -jar /aws-utilization-monitor.jar
