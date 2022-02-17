# Amazon Lambda function code examples in Go

This code example uses the 
[AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/latest/guide/home.html) 
to create the following resources:

- An Amazon S3 bucket
- An Amazon DynamoDB table
- An Amazon SNS topic
- An Amazon SQS queue

In addition, this project creates AWS Lambda functions,
in Go, to detect the following events:

- An object uploaded to the Amazon S3 bucket
- An item added to the Amazon DynamoDB table
- A message sent to the Amazon SNS topic
- A message sent to the Amazon SQS queue

The go functions are in their respective sub-folders in **src**.

## Using this code

Copy the contents of this directory to your computer.
If you want to change CloudFormation stack name from the
current value **GoLambdaCdkStack**,
change that value in **cdk.json**, **bin/go-lambda-cdk.ts** and **lib/go-lambda-cdk-stack.ts**
to the value you prefer.

You must run the following command to get the packages
that this CDK app requires:

`npm install`

You'll know you have all of the packages you need
if you can successfully execute the following command
to create a CloudFormation stack from this CDK app:

`cdk synth`

This creates the template **GoLambdaCdkStack.template.json**
(unless you've changed the stack name) in **cdk.out**.

If you encounter any errors running CDK commands,
see the
[Troubleshooting common AWS CDK issues](https://docs.aws.amazon.com/cdk/latest/guide/troubleshooting.html#troubleshooting_toolkit)
topic in the CDK developer guide.

## Working with the CDK app

If you aren't familiar with the CDK, here are some common commands:

- `cdk deploy` deploy this stack to your default AWS account/region
- `cdk diff`   compare deployed stack with current state
- `cdk ls`     lists your CloudFormation stacks
- `cdk synth`  create a CloudFormation template in 

See [CDK command](https://docs.aws.amazon.com/cdk/latest/guide/cli.html)
topic in the CDK developer guide for details.

## Getting information about the new resources

Once you deploy the application, it display the following information
that you can use to work with the created resources:

- The name of the resource
- The name of the Lambda function that handles the events from the resource
- The name of the Amazon CloudWatch log group to which
  print statements from the AWS Lambda function are sent
  
You can use the CLI to get information about the resources created by
the resulting CloudFormation template by running the following command,
where *STACK-NAME* is the name of your CloudFormation stack:

`aws cloudformation describe-stacks --stack-name STACK-NAME --query Stacks[0].Outputs --output text`

## Testing the notifications

This project contains the following Windows batch and Bash script files that you can use 
to test the AWS Lambda functions by sending a JSON payload to the function specified on the command line:

- **DynamoDBRecord.bat**, **DynamoDBRecord.sh**: these scripts send the data in **dynamodb-payload.json**.
- **S3Record.bat**, **S3Record.sh**: these scripts send the data in **s3-payload.json**.
- **SNSRecord.bat**, **SNSRecord.sh**: these scripts send the data in **sns-payload.json**.
- **SQSRecord.sh**, **SQSRecord.bat**: these scripts send the data in **sqs-payload.json**.
