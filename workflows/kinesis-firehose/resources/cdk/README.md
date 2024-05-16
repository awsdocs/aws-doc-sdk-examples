# Kinesis Firehose Workflow CDK Setup
This CDK project will create a delivery stream, S3 Bucket, and role required for this workflow.

The `cdk.json` file tells the CDK Toolkit how to execute your app.

## Prerequisites

Before you begin, ensure you have the AWS CDK and Node.js installed.

```bash
npm install -g aws-cdk
```

## Install Dependencies

Run the following command to install the necessary dependencies:

```bash
npm install @aws-cdk/core @aws-cdk/aws-s3 @aws-cdk/aws-iam @aws-cdk/aws-kinesisfirehose source-map-support
```

## Useful commands

- `npm run build` compile TypeScript to JavaScript
- `npm run watch` watch for changes and compile
- `npm run test` perform the jest unit tests
- `cdk deploy` deploy this stack to your default AWS account/region
- `cdk diff` compare deployed stack with current state
- `cdk synth` emits the synthesized CloudFormation template

## Stack Description

The CDK stack defined in this project performs the following tasks:

1. **Creates an S3 bucket** with a unique name to store the data delivered by Kinesis Firehose.
2. **Creates an IAM role** with the necessary permissions for Kinesis Firehose to write to the S3 bucket.
3. **Creates a Kinesis Firehose delivery stream** configured to deliver data directly to the S3 bucket.

## Deployment

To deploy the stack, run:

```bash
cdk deploy
```

## Outputs

After deployment, the stack will output:

- The name of the Kinesis Firehose delivery stream
- The name of the S3 bucket
- The ARN of the IAM role used by Kinesis Firehose

These outputs will help you verify the resources created and their configurations.

## Cleanup

To delete the stack and its resources, run:

```bash
cdk destroy
```
