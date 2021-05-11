# Using the Amazon API Gateway to invoke Lambda Functions

This example shows you how to create an AWS Lambda function that runs
on a defined frequency using Amazon CloudWatch sheculed events. When the
application runs, the AWS Lambda function checks the DynamoDB table to
find employees who are having their one-year anniversary.

Those having a work anniversary will receive a text message, sent using
Amazon Simple Notification Service (Amazon SNS).

## Solution Components

| Solution | Description |
|----------|-------------|
| CreateTable | A console application that creates a DynamoDB table called EmployeeTable. Edit the code to give at least one of the emplloyees a valid phone mobile phone number for testing purposes. |
| DeleteTable | A console application that deletes the table created for this example. Run it when you are done to remove the table you created. |
| SendSnsMessage | An AWS Lambda function that checks for employees having a work anniversary and send an anniversary message to the employee's phone. |

## Prerequisites

- An AWS Account with proper credentials.
- A C# .NET IDE
- AWS Toolkit for .NET
- .NET Core 3.1 or later

The example also uses a Dynamo DB table that contains the following information:

| ID | FirstName | Phone | HireDate |
|----|-----------|-------|----------|
| 101 | Jadwiga | 11234567890 | current date - one year |
| 102 | Denis | 11234567890 | 2017-3-9 |
| 103 | Sean | 11234567890 | 2013-5-13 |

## Setting up the environment

There are several steps you will need to perform in order to use the example
Lambda function included with this code example:

### 1. Create IAM Role

Create a role with permissions to run a Lambda expression. The role should also
include permissions to work with Amazon DynamoDB and Amazon
SimpleNotificationService.

Create the following IAM role:

    lambda-support - Used to invoke Lamdba functions.

This tutorial uses Amazon DynamoDB and Amazon SNS services. The lambda-support role
must have policies that enable it to invoke these services from a Lambda function.

To create the IAM role:

    1. Open the AWS Management Console. When the page loads, enter IAM in the search box, and then choose IAM to open the IAM console.

    2. In the navigation pane, choose Roles, and on the Roles page, choose Create Role.

    3. Choose AWS service, and then choose Lambda.

### 2. Create the Lambda Function




## 3. Cleaning up the environment

When you are done working with the application, you need to clean up the
resources you created to avoid possible charges. You can use the application
DeleteTable to delete the DynamoDB table.

