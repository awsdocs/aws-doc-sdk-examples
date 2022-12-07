# AWS Glue code examples for the Rust SDK

## Overview

These examples demonstrate how to perform several AWS Glue operations using the developer preview version of the AWS SDK for Rust.

AWS Glue is a serverless data integration service that makes it easier to discover, prepare, move, and integrate data from multiple sources for analytics, machine learning (ML), and application development.

## ⚠ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Prepare a new crawler, configure its job, and make sure an s3 bucket is prepared](src/pepare.rs) (CreateDatabase, CreateCrawler, GetCrmake sureawler, GetDatabase, GetTable, CreateJob, GetJob)
- [Start a new job run, wait for the job run to complete](src/run.rs) (StartJobRun, GetJobRun, ListJobs)
- [Clean up the resources created for the run](src/cleanup.rs) ()

## Run the examples

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

These examples run with the Rust minimum compiler version as supported by the Rust SDK; at the time of this writing, that is Rust 1.61.0. Executables can run from cargo with additional command line arguments documented following and in the binary main functions.

Running these scenarios requires providing an IAM role (as a command line flag) with the [AWSGlueServiceRole](https://us-east-1.console.aws.amazon.com/iam/home#/policies/arn:aws:iam::aws:policy/service-role/AWSGlueServiceRole$serviceLevelSummary) policy.
If the role has the name `GlueRole`, you can retrieve the Amazon Resource Name (ARN) from the AWS Command Line Interface (AWS CLI) in a \*nix environment with `export RUST_SDK_GLUE_TEST_ROLE=$(aws iam get-role --role-name GlueRole --output text --query Role.Arn)`.

## Run the code

### Scenario

After loading the role ARN, use this command to run the entire scenario.
The `RUST_LOG` configuration will print every HTTP request made by the SDK while the scenario runs.
Change `aws_smithy_http_tower::dispatch` to `off` to disable this.
We recommend keeping `scenario` always on, and tuning `aws_glue_sdk` to your preference.

```
RUST_LOG=scenario,aws_glue_sdk=info,aws_smithy_http_tower::dispatch=trace \
  cargo run --bin scenario -- --iam-role=$RUST_SDK_GLUE_TEST_ROLE
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

All tests can be run with `cargo test --all-targets --all-features -- --ignored`.
Tests must have an appropriate AWS IAM Role ARN exported in `RUST_SDK_GLUE_TEST_ROLE`.

## Additional Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for AWS Glue](https://docs.rs/aws-sdk-glue)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html)
