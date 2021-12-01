# AWS SDK for Rust code examples for API Gateway

The Amazon API Gateway Management API allows you to directly manage runtime aspects of your deployed APIs. To use it,
you must explicitly set the SDK's endpoint to point to the endpoint of your deployed API. The endpoint will be of the
form `https://[api-id].execute-api.[region].amazonaws.com/[stage]` where:
* `api-id` is the ID of your API (eg. `xy4n5r0m12`)
* `region` is the region of your API (eg. `us-east-1`)
* `stage` is the deployment stage of your API (eg. `test`, `prod`, `beta`)
, or will be the endpoint corresponding to your API's
custom domain and base path, if applicable.

### Notes

- We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the
  task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
