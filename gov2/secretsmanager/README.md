# AWS SDK for Go V2 code examples for AWS Secrets Manager 

## Purpose

These examples demonstrate how to perform several AWS Secrets Manager
operations using version 2 of the AWS SDK for Go.

AWS Secrets Manager helps you to securely encrypt, store, and retrieve credentials for your databases and other services. 


## Code Examples

The following examples are included:

* Create a secret
* Get the value of a secret
* Change the value of the secret
* List secrets in your account
* Delete a secret


## Running the code

To run the full example suite, enter:

```
go run main.go
```

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

# Resources

For more information, see

* The [AWS SDK for Go V2 documentation](https://docs.aws.amazon.com/sdk-for-go/).
* The [AWS Secrets Manager documentation](https://docs.aws.amazon.com/secretsmanager/?id=docs_gateway).


# Contributing

To propose a new code example to the AWS documentation team, see the [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md) file. The team prefers to create code examples that show broad scenarios rather than individual API calls.

# License

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
