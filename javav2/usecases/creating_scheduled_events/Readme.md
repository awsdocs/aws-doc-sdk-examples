#  Creating scheduled events to invoke Lambda functions

You can create a scheduled event that invokes AWS Lambda functions by using Amazon CloudWatch Events. You can configure CloudWatch Events to use Cron expressions to schedule when a Lambda function is invoked. For example, you can schedule a CloudWatch Event to invoke an AWS Lambda function every weekday. AWS Lambda is a compute service that enables you to run code without provisioning or managing servers.

**Note**: You can create Lambda functions in various programming languages. For this tutorial, Lambda functions are implemented by using the AWS Lambda Java runtime API. For more information about AWS Lambda, see
[What is AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html).

In this tutorial, you create a Lambda function by using the AWS Lambda Java runtime API. This example invokes different AWS services to perform a specific use case. For example, assume that an organization sends out a mobile text message to its employees that congratulates them at the one year anniversary date, as shown in this illustration.

![AWS Tracking Application](images/picPhone.png)

This tutorial shows you how to use Java logic to create a solution that performs this use case.  For example, you'll learn how to read a database to determine which employees have reached the one year anniversary date, how to process the data, and send out a text message all by using a Lambda function. Then you’ll learn how to use a Cron expression to invoke the Lambda function every weekday. 

This AWS tutorial uses an Amazon DynamoDB table named **Employee** that contains these fields. 
-	**Id** – the key for the table.
-	**first** – the employee’s first name.
-	**phone** – the employee’s phone number.
-	**startDate** – the employee’s start date.
