# AWS SDK for Go (v2) code examples

## Overview

The code examples in this topic show you how to use the AWS SDK for Go (v2) with AWS. 

The AWS SDK for Go (v2) provides a Go API for AWS infrastructure services. Using the 
SDK, you can build applications on top of Amazon S3, Amazon EC2, Amazon DynamoDB, 
and more.

## Types of code examples

* **Single-service actions** - Code examples that show you how to call individual 
  service functions.

* **Single-service scenarios** - Code examples that show you how to accomplish a 
  specific task by calling multiple functions within the same service.

* **Cross-service examples** - Sample applications that work across multiple AWS 
  services.

### Finding code examples

Single-service actions and scenarios are organized by AWS service. 
A README in each folder lists and describes how to run the examples.

Cross-service examples are located in the [cross_service](cross_service) folder. 
A README in each folder describes how to run the example.

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the 
  minimum permissions required to perform the task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, 
  see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

* You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the
  [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
* Go 1.18 or later

## Tests

All tests use go test, and you can find them alongside the code in the folder for each 
example. When an example has additional requirements to run tests, you can find them 
in the README for that service or cross-service example.

### Unit tests

The unit tests in this module use stubbed responses from [AwsmStubber](testtools/awsm_stubber.go).
AwsmStubber is a tool that uses the AWS SDK for Go middleware to intercept calls to
AWS, verify inputs, and return a mocked response. This means that when the unit tests 
are run, requests are not sent to AWS and no charges are incurred on your account.

Run unit tests in the folder for each service or cross-service example at a command
prompt.

```
go test ./...
```

### Integration tests

⚠️ Running the integration tests might result in charges to your AWS account.

The integration tests in this module make actual requests to AWS. This means that when
the integration tests are run, they can create and destroy resources in your account.
These tests might also incur charges. Proceed with caution.

Run integration tests in the folder for each service or cross-service example at a
command prompt by including the `integration` tag.

```
go test -tags=integration ./...
```

## Additional resources

* [AWS SDK for Go (v2) Developer Guide](https://aws.github.io/aws-sdk-go-v2/docs/)
* [AWS SDK for Go (v2) package](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
