# Build and manage a resilient service using an AWS SDK

## Overview

This example shows how to use AWS SDKs to create a load-balanced
web service that returns book, movie, and song recommendations. It shows
how the service responds to failures, and how to restructure the service for
more resilience when failures occur.

Several components are used to demonstrate the resilience of the example web service:

* [Amazon EC2 Auto Scaling](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html) 
  is used to create 
  [Amazon Elastic Compute Cloud (Amazon EC2)](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html) 
  instances based on a launch template and to keep the number of instances 
  in a specified range.
* [Elastic Load Balancing](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html) 
  handles HTTP requests, monitors the health of instances in the Auto Scaling group, and 
  dispatches requests to healthy instances. 
* A Python web server runs on each EC2 instance to handle HTTP requests. It responds
  with recommendations and health checks.
* An [Amazon DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html) 
  table simulates a recommendation service that the web server depends on to get recommendations.
* A set of [AWS Systems Manager](https://docs.aws.amazon.com/systems-manager/latest/userguide/what-is-systems-manager.html) 
  parameters control web server response to requests and health checks to 
  simulate failures and demonstrate resiliency. 

Each of these components is created and managed by using AWS SDKs as part of
an interactive demo that runs at a command prompt.

## Implementations

This example is implemented in the following languages:

* [Python](../../python/cross_service/resilient_service/README.md)

## Additional reading

* [Community.aws: How to build and manage a resilient service using AWS SDKs](https://community.aws/posts/build-and-manage-a-resilient-service-using-aws-sdks)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0