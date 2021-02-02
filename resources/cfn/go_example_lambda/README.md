# Creating resources to run AWS Lambda functions in Go

The AWS CloudFormation template **GoCdkStack.template.json** 
contains definitions that you can use to create the following resources:

- An Amazon S3 bucket
- An Amazon DynamoDB table
- An Amazon SNS topic
- An Amazon SQS queue

In addition, the template creates AWS Lambda functions,
in Go, to detect the following events:

- An object uploaded to the Amazon S3 bucket
- An item added to the Amazon DynamoDB table
- A message sent to the Amazon SNS topic
- A message sent to the Amazon SQS queue

## Creating the resources using the AWS CLI

You can use the
AWS Command Line Interface (AWS CLI)
to run the CloudFormation template and create the resources.
You can get the AWS CLI from
[here](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html).

To create these resources from this template:

1. Copy the template to your computer.

1. Run the following AWS CLI command to create the resources,
   where *STACK-NAME* is the name of the CloudFormation stack to create.
   It displays the names of the resources when it finishes:

   `aws cloudformation create-stack --stack-name STACK-NAME --template-body file://GoCdkStack.template.json`

If you forget any of the names of the resources, 
you can use the AWS CLI to get information about the resources created by
the resulting CloudFormation template by running the following command,
where *STACK-NAME* is the name of the CloudFormation stack to get information from:

`aws cloudformation describe-stacks --stack-name STACK-NAME --query Stacks[0].Outputs --output text`

## Customizing the template

The template was created using the 
[AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/latest/guide/home.html) 
project in
[go_example_lambda](../../cdk/go_example_lambda).
If you want to customize the template, use that AWS CDK project.
See the **README.md** file in that directory for details.

## Testing the notifications

The [go_example_lambda](../../cdk/go_example_lambda) 
project contains Windows batch and Bash script files that you can use 
to test the AWS Lambda functions.
See the **README.md** file in that project for details.
