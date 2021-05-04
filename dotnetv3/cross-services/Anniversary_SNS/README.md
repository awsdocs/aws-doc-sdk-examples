# AWK SDK for .NET Example of using Amazon CloudWatch scheduled events to invoke AWS Lambda functions

This example show you how to create an AWS Lambda function that runs
on a defined frequency using Amazon CloudWatch sheculed events. When the
application runs, the AWS Lambda function checks the DynamoDB table to
find employees who are having their one-year anniversary.

Those having a work anniversary will receive a text message, sent using
Amazon Simple Notification Service (Amazon SNS).

## Setting up the environment

Create a role with permissions to run a Lambda expression. The role should also
include permissions to work with Amazon DynamoDB and Amazon
SimpleNotificationService.

## Cleaning up the environment

When you are done working with the application, you need to clean up the
resources you created to avoid possible charges.

## Solution Components

| Solution | Description |
|----------|-------------|
| CreateTable | A console application that creates the table EmployeeTable. |
| DeleteTable | A console application that deletes the table created for this example. Run it when you are done to remove the table you created. |
| SendSnsMessage | An AWS Lambda function that checks for employees having a work anniversary and send an anniversary message to the employee's phone. |
