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
# Production-Grade Kinesis Data Firehose Ingestion

This example demonstrates best practices for ingesting data reliably and efficiently into Amazon Kinesis Data Firehose using the AWS SDK. It addresses common challenges around batching, error handling, monitoring, and infrastructure provisioning.

## Supporting Infrastructure

This example utilizes the following AWS services and resources:

- **Amazon Kinesis Data Firehose**: A fully managed service for automatically loading streaming data into AWS data stores and analytics tools.
- **AWS Cloud Development Kit (CDK)**: An open-source software development framework to define cloud infrastructure as code and provision it through AWS CloudFormation.

The CDK script for provisioning the necessary Kinesis Data Firehose resources is located in the `infrastructure/` directory.

## Prerequisites

To follow along with this example, you'll need:

- An AWS account and configured AWS credentials
- Node.js (version 12 or later) and npm installed
- AWS Cloud Development Kit (AWS CDK) installed

## Deployment Instructions

1. Clone the repository and navigate to the example directory:

   ```bash
   git clone https://github.com/aws-samples/aws-kinesis-data-firehose-production-ingestion.git
   cd aws-kinesis-data-firehose-production-ingestion
   ```

2. Install the required npm packages:

   ```bash
   npm install
   ```

3. Deploy the AWS infrastructure using the CDK script:

   ```bash
   npm run deploy
   ```

   This command will provision a Kinesis Data Firehose delivery stream and any other necessary resources.

## Resource Generation and Cleanup

This example includes a script (`generate-data.js`) for generating sample data to ingest into Kinesis Data Firehose. Run the script with:

```bash
node generate-data.js
```

To avoid incurring unnecessary charges, remember to clean up the resources created by this example when you're done. You can delete the CloudFormation stack with:

```bash
npm run destroy
```

## Example Implementation

The main implementation is provided in the `firehose-ingestion.js` file, written in Node.js using the AWS SDK for JavaScript. It demonstrates:

- Putting a single record into Kinesis Data Firehose with robust error handling and logging
- Batching and sending multiple records with `PutRecordBatch`
- Implementing an exponential backoff retry mechanism with jitter for failed operations
- Monitoring ingestion metrics like `IncomingBytes` and `IncomingRecords`

## Additional Resources and Reading

- [Amazon Kinesis Data Firehose Documentation](https://docs.aws.amazon.com/firehose/latest/dev/index.html)
- [AWS SDK for JavaScript Documentation](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/index.html)
- [AWS Cloud Development Kit (CDK) Documentation](https://docs.aws.amazon.com/cdk/latest/guide/home.html)

## Copyright and Licensing

This example is released under the [MIT License](LICENSE).
```