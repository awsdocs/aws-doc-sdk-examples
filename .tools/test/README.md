# Multi-Language Integration Test Runner
This directory contains the source and infrastructure code for running multi-language integration tests in AWS.

## Problem and solution statement
This repository contains example code written in [11 of the AWS-supported Software Development Kit (SDK) languages](../../README.md#how-this-repository-is-organized). 
This code must be tested to ensure its accuracy and functionality over time. 
However, while this repository contains comprehensive test coverage, each test must be manually triggered and can take many hours to complete.

This solution offers centralized triggering and language-agnostic test orchestration. In exchange, it requires ["automation-friendly" tests]() and the deployment of the following AWS Cloud Development Kit (AWS CDK) stacks.

## Design features
The following design features make this tool easy to use:
* **Serverless** - Requires no stateful infrastructure
* **No pipeline** - Relies on events, not manual interaction
* **All code** - The entire solution is deployable by using the AWS CDK

## Architecture
In addition to the source code in this repository, this solution consists of the following CDK stacks:

| Stack                                                | Function                                                          | Purpose                                                                                                                                                    |
|------------------------------------------------------|-------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Public Images](public_ecr_repositories)     | Holds versions of language-specialized Docker images.             | Event-based production of ready-to-run Docker images for each [supported SDK](https://docs.aws.amazon.com/sdkref/latest/guide/version-support-matrix.html). |
| [Producer](eventbridge_rule_with_sns_fanout) | Publishes a scheduled message to an Amazon Simple Notification Service (Amazon SNS) topic.                    | Centralized cron-based triggering of integration tests.                                                                                                    |
| [Consumer](sqs_lambda_to_batch_fargate)      | Consumes a message to trigger integration tests on AWS Batch with AWS Fargate. | Federated integration testing of example code for each [supported SDK](https://docs.aws.amazon.com/sdkref/latest/guide/version-support-matrix.html).       |

The following diagram shows the behavior of this GitHub repository and the preceding stacks: 

![weathertop-high-level-architecture.png](architecture_diagrams%2Fpng%2Fweathertop-high-level-architecture.png)

---

## How it works
On the surface, this solution orchestrates the execution of distributed integration testing for the 11 [supported SDKs](https://docs.aws.amazon.com/sdkref/latest/guide/version-support-matrix.html).
Under the hood, it relies on the source code in this repository and the following CDK stacks.

### 1. Image production
Image repositories are managed from an AWS account in which the [Public images stack]() is deployed.

Through a secure integration, a GitHub Workflow [configured in this repository](../../.github/workflows/docker-push.yml) produces Docker images containing pre-built SDK code and publishes them to the [AWS SDK Code Examples Images](https://gallery.ecr.aws/b4v4v1s0) public registry.

See [CDK stack](public_ecr_repositories).

### 2. Centralized eventing
Events are emitted from an AWS account in which the [Producer Stack](eventbridge_rule_with_sns_fanout) is deployed.

This stack contains a cron-based Amazon EventBridge rule that writes to a singular SNS topic. 
Through a cross-account integration, Amazon Simple Queue Service (Amazon SQS) queues in different AWS accounts can subscribe to this topic.

See [CDK stack](eventbridge_rule_with_sns_fanout).

### 3. Distributed testing
Testing is performed in AWS accounts in which the [Consumer Stack](sqs_lambda_to_batch_fargate) is deployed.

This stack contains an AWS Lambda function that submits jobs to AWS Batch. 
Through a secure integration, this Lambda function is triggered by an SQS queue that's subscribed to a cross-account topic.

See [CDK stack](sqs_lambda_to_batch_fargate).
