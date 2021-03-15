# About the UploadFileSaveAttributes project

This document describes the cross-service UploadFileSaveAttributes project in this directory.

## Workflow

This project has the following workflow:

1. The user opens the app.
1. The app authenticates them using an Amazon Cognito user pool
1. The user selects a local file
1. The app uploads the file to an Amazon Simple Storage Service (Amazon S3) bucket
1. This action triggers a notification from Amazon S3 to an AWS Lambda (Lambda) function
   (we may insert an Amazon Simple Queue Service (Amazon SQS) queue between the notification and the Lambda function)
1. The Lambda function calls an AWS service to get some attributes from the file
1. The Lambda function saves those attributes in an Amazon DynamoDB (DynamoDB) table

## Resources

This project creates the following resources:

- An Amazon Cognito user pool
- An Amazon S3 bucket
- A Lambda function
- A DynamoDB table
- An Amazon SQS queue

The app includes a teardown script that the user can run to delete any resources that the app creates.
If the teardown script cannot delete a resource, it displays enough information about the resource
so that the user can manually delete the resource.

## Invoking the app

To invoke this app, ...

## Deleting resources

To delete the resources created by this app, ...