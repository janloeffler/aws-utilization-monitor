# AWS Utilization Monitor build & deploy guidelines

This project show everything necessary, to deploy a fully working Java Spring web application to Amazon EC2 with the help of Docker and AWS Minion.

# Further references

* https://techwiki.zalando.net/display/ZHW/AWS

# Requirements

* JDK 8
* Maven 3
* Docker (see Boot2Docker for Mac users)
* AWS Minion (https://github.com/zalando/aws-minion)

# Make sure aws-minion is uptodate

    $ sudo pip3 install --upgrade aws-minion

# Login to AWS

    $ alias awslogin="minion login -r PowerUser --overwrite-credentials"
    $ awslogin

# Prepare docker when using boot2docker on MacOS

    $ $(boot2docker shellinit)
    $ VBoxManage controlvm boot2docker-vm natpf1 "awsutilizationmonitor,tcp,127.0.0.1,8080,,8080"

# Build the project

    $ mvn clean package
    $ docker build -t docker-registry2.hackweek.aws.zalando/awsutilizationmonitor:1.0 .
    
# Check that our Docker image works

    $ docker run -p 8080:8080 -it docker-registry2.hackweek.aws.zalando/awsutilizationmonitor:1.0

Visit [http://localhost:8080/](http://localhost:8080/)! Stop your server with **Ctrl+C**.

# Deploy it in the cloud!

In order to push you'll need to add our CA cert to the VM box:

    $ boot2docker ssh
    $ sudo su
    $ mkdir -p /etc/docker/certs.d/docker-registry2.hackweek.aws.zalando
    $ curl https://static.zalando.de/ca/zalando-service.ca > /etc/docker/certs.d/docker-registry2.hackweek.aws.zalando/ca.crt

Remember to perform this step after every restart of boot2docker.

    $ docker push docker-registry2.hackweek.aws.zalando/awsutilizationmonitor:1.0

If you did not set up AWS Minion before, go and visit "How to use the AWS Minion tool":
[https://techwiki.zalando.net/display/ZHW/AWS](https://techwiki.zalando.net/display/ZHW/AWS)

Create your new application (if you chose not to rename this project, this will already exist and you can skip this
step):

    $ minion app create awsutilizationmonitor.yaml

Add our Docker image as a new version to our application:

    $ minion version create awsutilizationmonitor 1.0 docker-registry2.hackweek.aws.zalando/awsutilizationmonitor:1.0

This step might take a long(tm) time (minutes). Afterwards you will be able to directly go to your deployed version:
[https://awsutilizationmonitor-1.0.hackweek.aws.zalando/](https://awsutilizationmonitor-1.0.hackweek.aws.zalando/)

The application's main domain will still not show the deployed version. Now you can switch traffic of your main domain
to the new version:

    $ minion version traffic awsutilizationmonitor 1.0 100

Have fun with [https://awsutilizationmonitor.hackweek.aws.zalando/](https://awsutilizationmonitor.hackweek.aws.zalando/)!

    $ awsutilizationmonitor.platform.aws.zalando
    
Observe your logs (remember that the Load Balancer checks spam your HTTP):

    $ minion version logs awsutilizationmonitor 1.0
