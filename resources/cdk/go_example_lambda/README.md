# Amazon Lambda function code examples in Go

This project uses the CDK to create the following resources:

- An Amazon S3 bucket
- An Amazon DynamoDB table
- An Amazon SNS topic
- An Amazon SQS queue

In addition, this project creates AWS Lambda functions,
in Go,
to detect the following events:

- An object uploaded to the Amazon S3 bucket
- An item added to the Amazon DynamoDB table
- A message sent to the Amazon SNS topic
- A message sent to the Amazon SQS queue

## Deploying the CDK application

Once you deploy the application, it display the following information
that you can use to work with the created resources:

- The name of the resource
- The name of the Lambda function that handles the events from the resource
- The name of the Amazon CloudWatch log group to which
  print statements from the AWS Lambda function are sent

## Testing the notifications

This project contains the following files you can use to test the AWS Lambda functions:

- DynamoDBRecord.bat, DynamoDBRecord.sh: use this Windows batch or Bash script to send
  to send the data in **dynamodb-payload.json** to the specified AWS Lambda function.
- S3Record.bat, S3Record.sh: use this Windows batch or Bash script to send
  to send the data in **s3-payload.json** to the specified AWS Lambda function.
- SNSRecord.bat, SNSRecord.sh: use this Windows batch or Bash script to send
  to send the data in **sns-payload.json** to the specified AWS Lambda function.
- SQSRecord.sh, SQSRecord.bat: use this Windows batch or Bash script to send
  to send the data in **sqs-payload.json** to the specified AWS Lambda function.
