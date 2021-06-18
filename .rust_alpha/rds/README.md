# AWS SDK for Rust code examples for Amazon RDS

Amazon Relational Database Service (Amazon RDS) is a web service that makes it easier to set up, operate, and scale a relational database in the cloud.

## rds-helloworld

This code example displays information about your RDS instances.

### Usage

```cargo run --bin rds-helloworld [-d DEFAULT_REGION] [-v]```

where:

- _DEFAULT_REGION_ is the region in which the client is created.
  If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- __-v__ enables displaying additional information.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
