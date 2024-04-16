---
debug:
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
  finish: end_turn
  engine: bedrock
isolated: false
combined: true
prompt: |
  Write me a README.md (in GitHub markdown) for a new Workflow Example.

  This document will be informed by the Workflow Details and this template:

  # Title and Overview:
  Start with a concise title that captures the essence of the use case.
  Provide an overview section that briefly describes the use case, its relevance, and how it integrates with AWS services. Mention any specific AWS limitations or challenges this example addresses.

  # Supporting Infrastructure:
  Detail the AWS infrastructure components involved in the example (e.g., specific AWS services and resources).
  Include a link to an AWS CloudFormation template (if applicable) for setting up necessary resources, and mention its location.

  # Prerequisites:
  List prerequisites for users to follow along with the example. This might include AWS tooling, specific AWS service APIs, or programming languages.

  # Deployment Instructions:
  Provide step-by-step commands for deploying the infrastructure using AWS CLI or other tools. Highlight the CDK or CFN command to deploy the CloudFormation stack if relevant.

  # Resource Generation and Cleanup:
  If the example requires generating resources, provide scripts or instructions to do so. Explain the purpose and output of these scripts.
  Include instructions for cleaning up or deleting the resources created during the example to avoid unnecessary charges.

  # Example Implementation:
  Link to implementation code samples in one or more programming languages relevant to the use case.
  Provide brief descriptions of what each implementation does and how it contributes to solving the use case.

  # Additional Resources and Reading:
  Recommend further reading or documentation that can help users understand the concepts or AWS services used in the example.

  # Copyright and Licensing:
  Include a copyright notice and licensing information, specifying how users are permitted to use, modify, or distribute the example.'
---
# Kinesis Firehose PutRecord and PutRecordBatch Operations

This example demonstrates how to use the AWS Kinesis Firehose service for reliable and high-performance data ingestion using the PutRecord and PutRecordBatch operations. It addresses batch sizing considerations, robust error handling, logging, pagination, and exception handling with retry mechanisms featuring exponential back-off with jitter.

## Supporting Infrastructure

This example uses the following AWS services and resources:

- Kinesis Firehose Delivery Stream
- Amazon CloudWatch Logs (for logging)

A TypeScript CDK script is provided to create the necessary resources. The script is located in the `cdk` directory of this repository.

## Prerequisites

To follow along with this example, you'll need:

- An AWS account and AWS credentials configured on your local machine
- The AWS Command Line Interface (AWS CLI) installed and configured
- Node.js and npm (Node Package Manager) installed

## Deployment Instructions

1. Clone this repository to your local machine.
2. Navigate to the `cdk` directory.
3. Run `npm install` to install the required dependencies.
4. Run `npm run build` to compile the TypeScript code.
5. Run `cdk deploy` to deploy the CloudFormation stack and create the necessary resources.

## Resource Generation and Cleanup

The CDK script will create the Kinesis Firehose Delivery Stream and CloudWatch Log Group. To clean up these resources, run `cdk destroy` from the `cdk` directory.

## Example Implementation

The example implementation is provided in the `src` directory:

- `index.ts`: This is the main file that demonstrates the PutRecord and PutRecordBatch operations with Kinesis Firehose, incorporating error handling, logging, pagination, and retry mechanisms with exponential back-off and jitter.

## Additional Resources and Reading

- [AWS Kinesis Firehose Developer Guide](https://docs.aws.amazon.com/firehose/latest/dev/what-is-this-service.html)
- [AWS SDK for JavaScript Kinesis Firehose Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Firehose.html)
- [AWS SDK for JavaScript Retry Mechanism](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Config.html#retryPolicy)

## Copyright and Licensing

This example is provided under the [MIT No Attribution License](https://aws.amazon.com/code/license/mit-0).