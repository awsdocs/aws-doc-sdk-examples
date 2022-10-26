# AWS SDK for Go (v2) examples

## Overview

These examples show how to work with language-specific aspects of the
AWS SDK for Go (v2). 

The AWS SDK for Go provides easy-to-use APIs and utilities developers can 
use to quickly integrate Go applications with AWS services like Amazon S3 and Amazon EC2.

## AWS Usage Scenario Examples

### `config.go`: Loading configurations and using STS for roles. 

This example demonstrates

* Loading the default configuration
* Assuming a role using STS 

To run this example, run

```
go run config.go
```

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

### Important

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
