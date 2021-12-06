# Amazon Route 53 Application Recovery Java code examples

This README discusses how to run and test Java code examples for Amazon Route 53 Application Recovery Controller.

## Running the Amazon Route 53 Application Recovery Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

To run these examples, setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).

### Prerequisites

To run these examples, you need the following:

+ An AWS account
+ A Java IDE (this tutorial uses the IntelliJ IDE)
+ Java JDK 1.8
+ Maven 3.6 or later

### Important

+ This code has not been tested in all AWS Regions. Some AWS services are available only in specific regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re not charged.

### Examples

You can find the following examples that use the **Route53RecoveryClusterClient** service client: 

+ **GetRoutingControlState** - Demonstrates how to get the state for a routing control.
+ **UpdateRoutingControlState** - Demonstrates how to set the state of the routing control to reroute traffic. 
