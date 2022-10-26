# Amazon Elastic Compute Cloud (Amazon EC2) examples for  Go (v2).

## Overview

These examples demonstrates how to perform several Amazon EC2 operations
using version 2 of the AWS SDK for Go.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create an image](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\CreateImagev2.go)
- [Create an instance](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\CreateInstancev2.go)
- [Describe an instances](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\DescribeInstancesv2.go)
- [Describe a VPC endpoint](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\DescribeVPCEndpointsv2.go)
- [Monitor instances](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\MonitorInstancesv2.go)
- [Reboot an instance](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\RebootInstancesv2.go)
- [Start an instance](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\StartInstancesv2.go)
- [Stop an instance](C:\Users\igsmith\source\repos\aws-doc-sdk-examples\gov2\ec2\common\StopInstancesv2.go)

## Run the examples

### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

### Instructions

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

To run a unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

## Additional resources

- [Amazon EC2 Developer Guide for Linux Instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 Developer Guide for Windows Instances](https://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [AWS SDK for Go Amazon EC2 Client](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/ec2)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
