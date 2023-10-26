# Build and manage a resilient service using the SDK for Java

## Overview

This example shows how to use the AWS SDK for Java (v2) to create a load-balanced
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

Each of these components is created and managed with the SDK for Java as part of
an interactive demo that runs at a command prompt.

### Amazon EC2 Auto Scaling and EC2 instances

An Auto Scaling group starts EC2 instances in a specified set of Availability Zones. 
This example uses an Auto Scaling group to keep the number of running instances 
within a specified range and to make them available across multiple Availability Zones. The Auto Scaling group
is set as a load balancer target so that HTTP requests are handled by a single endpoint
and dispatched equally to the instances in the group.

An [AWS Identity and Access Management (IAM)](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html) 
instance profile specifies permissions that are granted to EC2 instances created 
during the demo. When you associate an instance profile with an instance, AWS SDK code 
that runs on the instance can assume the profile's role to get the permissions that are specified
by the role's attached policies.

An Amazon EC2 launch template specifies how instances are created. This example creates
a launch template that specifies the instance type, Amazon Machine Image (AMI), instance 
profile, and a Bash script that runs when the instance is started. The Bash
script installs required Python packages and starts a demo Python web server that listens
for HTTP requests on port 80. The Python web server uses the SDK for Python to get 
recommendation data from a DynamoDB table and to get parameter values from Systems 
Manager to control the flow of the demonstration.

### Elastic Load Balancing

Elastic Load Balancing is used to distribute incoming HTTP traffic across multiple instances.
This example creates an 
[Application Load Balancer](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html). 
It also adds a listener that forwards requests
from the load balancer endpoint to the EC2 instances that are managed by the Auto Scaling
group. The target group performs health checks on the instances and pulls unhealthy
instances out of the rotation.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

All the components used in this example need to access one another. Therefore, you must
create all components in the same virtual private cloud (VPC). A VPC is a logically isolated network provided by
[Amazon Virtual Private Cloud (Amazon VPC)](https://docs.aws.amazon.com/vpc/latest/userguide/what-is-amazon-vpc.html).
You can use the default VPC that's included with your account, or 
[create a new VPC](https://docs.aws.amazon.com/vpc/latest/userguide/create-vpc.html).

To access the load balancer endpoint, you must allow inbound traffic
on port 80 from your computer's IP address to your VPC. If this rule doesn't exist, the 
example tries to add it. Alternately, you can 
[add a rule to the default security group for your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/security-group-rules.html) 
and specify your computer's IP address as a source.

To complete the tutorial, you need the following:

+ An AWS account
+ A Java IDE (this example uses IntelliJ)
+ Java 17 SDK and Maven


### Instructions

This starts an interactive scenario that walks you through several aspects of creating a 
resilient web service and lets you send requests to the load balancer endpoint and verify
instance health along the way. You can run this example in the Java IDE. 

##### Deploy resources

Use the SDK for Java to create the following AWS resources: 

1. A DynamoDB table that acts as a service that recommends books, movies, and songs.
2. An instance profile and an associated role and policy that grants permission to
   instances to access DynamoDB and Systems Manager.
3. A launch template that specifies the instance profile and a startup script
   that starts a Python web server on each instance.
4. An Auto Scaling group that starts EC2 instances, one in each of three 
   Availability Zones.
5. An Application Load Balancer that handles HTTP requests to a single endpoint.
6. A target group that connects the load balancer to instances in the Auto Scaling group.
7. A listener that is added to the load balancer and forwards requests to the target group.  

##### Demonstrate resiliency

This part of the example demonstrates resiliency by simulating several kinds of failures.
It uses Systems Manager parameters to update how the web server responds to requests. It
also uses health checks to show how to make your web server more resilient to failure.

Along with recommendations returned by the DynamoDB table, the web service includes the
instance ID and Availability Zone so you can see how the load balancer distributes
requests among the instances in the Auto Scaling group.

The scenario takes the following steps:  

1. **Initial state: healthy** — Sends requests to the endpoint to get recommendations and verify that instances 
   are healthy.  
2. **Broken dependency** — Sets a parameter that specifies a nonexistent DynamoDB table name. This simulates a
   failure of the recommendation service. Requests for recommendations now return a failure
   code. All instances still report as healthy because they only implement shallow health checks. For this
   example, a shallow health check means the web server always reports itself as healthy as long as the
   load balancer can connect to it.
3. **Static response** — Updates a parameter that prompts the web server to return a static response when the
   recommendation service fails. Requests for recommendations now return a static response, 
   which is a better customer experience.
4. **Bad credentials** — Sets the table name parameter so the recommendations service succeeds, but also
   updates one of the instances to use an instance profile that contains bad credentials.
   Now, when the load balancer selects the bad instance to serve a request, it returns
   a static response because it cannot access the recommendation service, but the other
   instances return real recommendations.
5. **Deep health checks** — Sets a parameter that instructs the web server to perform a deep health check.
   For this example, a deep health check means that the web server reports itself as unhealthy if it can't 
   access the recommendations service. The instance with bad credentials reports as unhealthy and the load 
   balancer takes it out of rotation. Now, requests are forward only to healthy instances.
6. **Replace the failing instance** — Terminates the unhealthy instance and lets Amazon EC2 Auto Scaling start 
   a new instance in its place. During this process, the stopping and starting instances are unhealthy so they
   don't receive any requests, but the load balancer continues to forward requests to healthy
   instances. When the new instance is ready, it is added to the rotation and starts receiving
   requests.
7. **Fail open** — Sets the table name parameter so the recommendations service fails for all instances.
   Because all instances are using deep health checks, they all report as unhealthy. In this
   case, the load balancer continues to forward requests to all instances. This lets the
   system fail open and lets the instances return static responses, rather than fail closed
   and report failure.

##### Destroy resources

Use the SDK for Java to clean up all resources created for this example.

1. Delete the load balancer and target group.
2. Stop all instances and delete the Auto Scaling group.
3. Delete the launch template and instance profile.
4. Delete the DynamoDB recommendations table.

## Additional resources

* [Application Load Balancers user guide](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html)
* [Amazon EC2 Auto Scaling user guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
* [Amazon Elastic Compute Cloud (Amazon EC2) user guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
* [SDK for Java Elastic Load Balancing v2 reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/elasticloadbalancingv2/ElasticLoadBalancingV2Client.html)
* [SDK for Java Amazon EC2 Auto Scaling reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/autoscaling/AutoScalingClient.html)
* [SDK for Java Amazon EC2 reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ec2/Ec2Client.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0