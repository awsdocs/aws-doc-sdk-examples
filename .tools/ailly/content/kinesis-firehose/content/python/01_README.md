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
```markdown
# Reliable and High-Performance Kinesis Firehose PutRecord and PutRecordBatch Operations

This example demonstrates how to use the AWS Kinesis Firehose service for PutRecord and PutRecordBatch operations, focusing on production-grade reliability and performance. It addresses batch limits, pricing, and throughput considerations, and incorporates robust error handling, logging, pagination, and retry mechanisms.

## Supporting Infrastructure

This example uses the following AWS services and resources:

- **AWS Kinesis Firehose**: A fully managed service for delivering real-time streaming data to destinations such as Amazon S3, Amazon Redshift, and more.
- **AWS CloudFormation**: A service that helps you model and set up your AWS resources.

The necessary AWS resources are provisioned using a TypeScript CDK script located at `./infrastructure/kinesis-firehose-example.ts`.

## Prerequisites

To follow along with this example, you'll need:

- An AWS account and the AWS CLI configured with appropriate credentials.
- Node.js and npm (or yarn) installed for running the TypeScript CDK script.
- Familiarity with TypeScript (for the CDK script) and the programming language used in the implementation examples.

## Deployment Instructions

1. Clone the repository:

```
git clone https://github.com/aws-samples/aws-kinesis-firehose-example.git
cd aws-kinesis-firehose-example
```

2. Install the CDK dependencies:

```
npm install
```

3. Deploy the CloudFormation stack using the CDK:

```
npx aws-cdk deploy
```

## Resource Generation and Cleanup

The CDK script (`infrastructure/kinesis-firehose-example.ts`) creates the necessary AWS resources, including a Kinesis Firehose Delivery Stream and an S3 bucket for data delivery.

To clean up and delete the resources created during the example, run:

```
npx aws-cdk destroy
```

## Example Implementation

- [TypeScript implementation](./src/kinesis-firehose-example.ts)
- [Python implementation](./src/kinesis-firehose-example.py)

Each implementation demonstrates how to use the Kinesis Firehose service for PutRecord and PutRecordBatch operations, incorporating best practices for reliability and performance.

## Additional Resources and Reading

- [AWS Kinesis Firehose Documentation](https://docs.aws.amazon.com/firehose/latest/dev/what-is-this-service.html)
- [AWS Kinesis Firehose Data Delivery Pricing](https://aws.amazon.com/kinesis/data-firehose/pricing/)
- [AWS Kinesis Firehose Limits](https://docs.aws.amazon.com/firehose/latest/dev/limits.html)

## Copyright and Licensing

This example is available under the MIT-0 license. See the [LICENSE](LICENSE) file for more information.
```