# Secrets Manager code examples for the AWS SDK for Go (v2) 

## Overview

These examples demonstrate how to perform several AWS Secrets Manager
operations using version 2 of the AWS SDK for Go.

AWS Secrets Manager helps you to securely encrypt, store, and retrieve credentials for your databases and other services. 

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code Examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a secret](common/create.go)
- [Delete a secret](common/delete.go)
- [Get a secret value](common/get.go)
- [List secrets](common/list.go)
- [Update a secret](common/update.go)

## Running the code

### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

### Instructions

To run the full example suite, enter:

```
go run main.go
```

# Additional resources

* [AWS Secrets Manager Developer Guide](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html)
* [AWS Secrets Manager API documentation](https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html).
* [AWS SDK for Go (v2) Secrets Manager API Documentation](https://docs.aws.amazon.com/sdk-for-go/api/service/secretsmanager/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
