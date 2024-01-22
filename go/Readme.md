# SDK for Go V1 code examples

## Overview

The code examples in this topic show you how to use the AWS SDK for Go V1 with AWS.
If you're looking for examples of how to use the SDK for Go V2, see the 
[README for SDK for Go V2 examples](../gov2/README.md) in this repo. 

⚠️ AWS SDK for Go V1 will enter maintenance mode on July 31, 2024 and reach end-of-support 
on July 31, 2025. For more information, see 
[this announcement](https://aws.amazon.com/blogs/developer/announcing-end-of-support-for-aws-sdk-for-go-v1-on-july-31-2025/).

The SDK for Go V1 provides a Go API for AWS infrastructure services. Using the 
SDK, you can build applications on top of Amazon S3, Amazon EC2, Amazon DynamoDB, 
and more.

## Types of code examples

* **Single-service actions** - Code examples that show you how to call individual 
  service functions.

### Finding code examples

Single-service actions are organized by AWS service. 
A README in each folder lists and describes how to run the examples.

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the 
  minimum permissions required to perform the task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, 
  see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Run the examples

### Prerequisites

* You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the
  [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
* [Go 1.14 or later](https://go.dev/doc/install)

### Run the code

Each example can be run separately at a command prompt. The README in each service
folder has instructions for how to run the examples. 

### Run the tests

All tests use go test, and you can find them alongside the code in the folder for each 
example. The README in each service folder has instructions for how to run the examples.  

## Additional resources

* [AWS SDK for Go V1 Developer Guide](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/welcome.html)
* [AWS SDK for Go V1 API Reference](https://docs.aws.amazon.com/sdk-for-go/api/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
