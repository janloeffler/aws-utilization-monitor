[![Build Status](https://travis-ci.org/zalando/aws-utilization-monitor.svg?branch=master)](https://travis-ci.org/zalando/aws-utilization-monitor)
[![Coverage Status](https://coveralls.io/repos/zalando/aws-utilization-monitor/badge.svg?branch=master)](https://coveralls.io/r/zalando/aws-utilization-monitor?branch=master)

# AWS Utilization Monitor build & deploy guidelines

The AWS Utilization Monitor is a RESTful service that scans AWS accounts for all used resources and generates a statistic for it. To get stats about several accounts you either change the AWS credentials file or deploy this service to each AWS account you want to monitor and collect data via its RESTful API.

This project shows everything necessary, to deploy a fully working Java Spring web application to Amazon EC2 with the help of Docker and AWS Minion.

# Requirements

* JDK 8
* Maven 3
* Docker (see Boot2Docker for Mac users)
* AWS Minion (https://github.com/zalando/aws-minion)

# Make sure aws-minion is up-to-date

    $ sudo pip3 install --upgrade aws-minion

# Login to AWS by creating a valid aws credetials file in ~/.aws/credentials

    $ alias awslogin="minion login -r %YOUR_AWS_ACCOUNT_ID% --overwrite-credentials"
    $ awslogin

# Prepare docker when using boot2docker on MacOS

    $ $(boot2docker shellinit)
    $ VBoxManage controlvm boot2docker-vm natpf1 "aws-utilization-monitor,tcp,127.0.0.1,8080,,8080"

# Build the project

    $ mvn clean package
    $ docker build -t %YOUR_DOCKER_REGISTRY%/aws-utilization-monitor:1.0 .
    
# Check that our Docker image works

    $ docker run -p 8080:8080 -it %YOUR_DOCKER_REGISTRY%/aws-utilization-monitor:1.0

Visit [http://localhost:8080/](http://localhost:8080/)! Stop your server with **Ctrl+C**.

# Deploy it in the cloud!

    $ docker push %YOUR_DOCKER_REGISTRY%/aws-utilization-monitor:1.0

If you did not set up AWS Minion before, go and visit "How to use the AWS Minion tool":
[https://techwiki.zalando.net/display/ZHW/AWS](https://techwiki.zalando.net/display/ZHW/AWS)

Create your new application (if you chose not to rename this project, this will already exist and you can skip this
step):

    $ minion app create aws-utilization-monitor.yaml

Add our Docker image as a new version to our application:

    $ minion version create aws-utilization-monitor 1.0 %YOUR_DOCKER_REGISTRY%/aws-utilization-monitor:1.0

This step might take a long(tm) time (minutes). Afterwards you will be able to directly go to your deployed version:
[https://aws-utilization-monitor-1.0.%YOUR_DOMAIN%/](https://aws-utilization-monitor-1.0.%YOUR_DOMAIN%/)

The application's main domain will still not show the deployed version. Now you can switch traffic of your main domain
to the new version:

    $ minion version traffic aws-utilization-monitor 1.0 100

Have fun with [https://aws-utilization-monitor.%YOUR_DOMAIN%/](https://aws-utilization-monitor.%YOUR_DOMAIN%/)!

    $ aws-utilization-monitor.%YOUR_DOMAIN%
    
Observe your logs (remember that the Load Balancer checks spam your HTTP):

    $ minion version logs aws-utilization-monitor 1.0
