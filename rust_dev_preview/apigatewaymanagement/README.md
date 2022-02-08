# AWS SDK for Rust code examples for API Gateway Management API

## Purpose

These examples demonstrate how to perform several Amazon API Gateway Management API (API Gateway Management API) operations using the developer preview version of the AWS SDK for Rust.

The Amazon API Gateway Management API allows you to directly manage runtime aspects of your deployed APIs. To use it,
you must explicitly set the SDK's endpoint to point to the endpoint of your deployed API. The endpoint must be of the
form `https://[api-id].execute-api.[region].amazonaws.com/[stage]` where:
* `api-id` is the ID of your API.
* `region` is the Region of your API.
* `stage` is the deployment stage of your API,
  or the endpoint corresponding to your API's
  custom domain and base path.

## Code examples

- [Get log events](src/bin/get-log-events.rs) (GetLogEvents)

## ⚠ Important

- We recommend that you grant this code least privilege, 
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the code examples

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

### post_to_connection

This example sends data to a connection.

`cargo run --bin post_to_connection -- -a APP-ID -s STAGE -c CONNECTION-ID -d DATA [-r REGION] [-v]`

- _APP-ID_ is the API ID for your API.
- _STAGE_ is the deployment stage for your API.
- _CONNECTION-ID_ is the ID of the connection where the data is sent.
- _DATA_ is the data to send to the connection.
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for API Gateway Management API](https://docs.rs/aws-sdk-apigatewaymanagement)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
