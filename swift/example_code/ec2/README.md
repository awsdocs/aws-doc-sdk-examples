# Amazon EC2 code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `swift` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon EC2](hello/Package.swift#L8) (`DescribeSecurityGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DescribeSecurityGroups](hello/Sources/entry.swift#L44)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

To build any of these examples from a terminal window, navigate into its
directory, then use the following command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `ListUsers` directory, to build that example). Then type `xed.`
to open the example directory in Xcode. You can then use standard Xcode build
and run commands.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for Swift Amazon EC2 reference](https://sdk.amazonaws.com/swift/api/awsec2/latest/documentation/awsec2)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
