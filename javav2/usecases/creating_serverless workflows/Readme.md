#  Create AWS Serverless workflows using the Java SDK

You can create an AWS Serverless workflow by using the AWS Java SDK and AWS Step Functions. 
Each workflow step is implemented by using a Lambda function. AWS Lambda is a compute service that lets you run 
code without provisioning or managing servers.

**Note**: You can create Lambda functions in various programming languages. For this tutorial, Lambda functions are 
implemented by using the Lambda Java API. For more information about Lambda, see 
[What is AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html).

In this tutorial, you create a workflow that creates support tickets for an organization. Each workflow step performs an operation on the ticket. This tutorial teaches you how process workflow data by using Java. For example, you will learn how to read data that is passed to the workflow, how to pass data between steps, and how to invoke AWS Services from a workflow. 

#### Prerequisites
To follow along with the tutorial, you need the following:
+ An AWS Account.
+ A Java IDE (for this tutorial, the IntelliJ IDE is used).
+ Java 1.8 JDK. 
+ Maven 3.6 or higher.
