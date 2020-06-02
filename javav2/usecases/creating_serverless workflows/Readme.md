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

**Cost to complete**: The AWS Services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc). 

**Note**: Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re no longer charged.

#### Topics

+ Understand the workflow.
+ Create an IAM role that is used to execute Lambda functions.
+	Create a workflow by using AWS Step functions.
+	Create an IntelliJ project named LambdaFunctions.
+	Add the POM dependencies to your project.
+	Create Lambda functions by using the Java Lambda API.
+	Package the project that contains Lambda functions. 
+	Deploy Lambda functions.
+	Add Lambda functions to workflows.
+ Invoke the workflow from the AWS Console.

**Note**: Before following this tutorial, create an Amazon DynamoDB table named Cases with a key named Id. To learn how to create a DynamoDB table, see [Create a Table](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/getting-started-step-1.html).

## Understand the workflow

The following figure shows the workflow that is created by following this tutorial. 

![AWS Tracking Application](images/lambda1.png)




