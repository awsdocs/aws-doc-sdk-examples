# AWS SDK for Rust code examples for Amazon RDS Data Service

Amazon Relational Database Service Data Service (Data Service) provides a secure HTTP endpoint and integration with AWS SDKs so you can SQL statements without managing connections.

## rdsdata-helloworld

This code example sends a query to an Aurora serverless cluster.

### Usage

```cargo run --bin rdsdata-helloworld -- -q QUERY -r RESOURECE_ARN -s SECRET_ARN [-d DEFAULT_REGION] [-v]```

where:

- _QUERY_ is the query to send to the cluster.
- _RESOURCE_ARN_ is the ARN of the cluster.
- _SECRET_ARN_ is the ARN of the of the Secrets Manager secret.
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
