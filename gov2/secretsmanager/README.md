# AWS SDK for Go V2 code examples for AWS Secrets Manager 

## Purpose

These examples demonstrate how to perform several AWS Secrets Manager
operations using version 2 of the AWS SDK for Go.

AWS Secrets Manager helps you to securely encrypt, store, and retrieve credentials for your databases
and other services. Instead of hardcoding credentials in your apps, you can make calls to Secrets Manager
to retrieve your credentials whenever needed. Secrets Manager helps you protect access to your IT resources
and data by enabling you to rotate and manage access to your secrets. 

## Code Examples

The following samples are included:

* Create a secret
* Get the value of a secret secret
* Change the value of the secret
* List secrets in your account
* Delete a secret



## Running the code

To run the full example suite, enter:

```
go run main.go
```

This example will


### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

### Important!

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.


# License

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
