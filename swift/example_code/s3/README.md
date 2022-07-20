# Amazon S3 code examples for the SDK for Swift
## Overview
This folder contains code examples demonstrating how to use the AWS SDK for
Swift to use the Amazon Simple Storage Service (S3). This README discusses how
to run these examples.

Amazon Simple Storage Service (Amazon S3) is storage for the internet. You can
use Amazon S3 to store and retrieve any amount of data at any time, from
anywhere on the web.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

<!-- ### Single actions
Code excerpts that show you how to call individual service functions.
* [*Abbreviated title of code example (to match SOS), using imperative form of verb*](*relative link to code example*)(*API command*) --->

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [Amazon S3 basics](./basics/)

<!-- ### Cross-service examples
Sample applications that work across multiple AWS services.
* [*Title of code example*](*relative link to code example*) --->

## Running the examples
To build any of these examples from a terminal window, navigate into its directory then use the command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `FindOrCreateIdentityPool` directory, to build that example), then
type `xed .` to open the example directory in Xcode. You can then use standard
Xcode build and run commands.

### Prerequisites
See the [Prequisites](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/swift#Prerequisites) section in the README for the AWS SDK for Swift examples repository.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

To run the tests for an example, use the command `swift test` in the example's directory.

## Additional resources
* [AWS SDK for Swift Developer Guide](https://docs.aws.amazon.com/sdk-for-swift/latest/developer-guide): The documentation for the AWS SDK for Swift
* [AWS SDK for Swift Developer
  Reference](https://awslabs.github.io/aws-sdk-swift/reference/0.x/): API reference for the AWS SDK for Swift
* [AWS SDK for Swift project on
  GitHub](https://github.com/awslabs/aws-sdk-swift): Contribute to the AWS SDK
  for Swift
* [The Swift Programming Language](https://docs.swift.org/swift-book): The
  definitive reference and guide for Swift programmers

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
