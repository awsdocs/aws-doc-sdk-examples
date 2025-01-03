# Lambda code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with AWS Lambda.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Lambda allows you to run code without provisioning or managing servers._

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

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](basics/increment/Package.swift)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](basics/lambda-basics/Sources/entry.swift#L177)
- [GetFunction](basics/lambda-basics/Sources/entry.swift#L142)
- [Invoke](basics/lambda-basics/Sources/entry.swift#L313)
- [ListFunctions](basics/lambda-basics/Sources/entry.swift#L283)
- [UpdateFunctionCode](basics/lambda-basics/Sources/entry.swift#L236)


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


#### Learn the basics

This example shows you how to do the following:

- Create an IAM role and Lambda function, then upload handler code.
- Invoke the function with a single parameter and get results.
- Update the function code and configure with an environment variable.
- Invoke the function with new parameters and get results. Display the returned execution log.
- List the functions for your account, then clean up resources.

<!--custom.basic_prereqs.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.basic_prereqs.lambda_Scenario_GettingStartedFunctions.end-->


<!--custom.basics.lambda_Scenario_GettingStartedFunctions.start-->
##### Build and deploy the example

This example consists of the main program and two AWS Lambda functions. To
build and deploy it:

1. Build the `increment` lambda function (`cd increment && swift build`).
2. Archive the `increment` lambda function for use as a Lambda function:
   `swift package archive --allow-network-connections docker`. When archiving
   is complete, the archive's path is displayed. Take note of it.
3. Build the `calculator` lambda function (`cd ../calculator && swift build`).
4. Archive it for use as a Lambda function: `swift package archive
   --allow-network-connections docker`. Take note of this created Zip file's
   path, too.
5. Build the main program in the `lambda-basics` directory.
6. Run the main program with the command `swift run lambda-basics --incpath <path-of-increment-archive> --calcpath <path-of-calculator-archive>`.
<!--custom.basics.lambda_Scenario_GettingStartedFunctions.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for Swift Lambda reference](https://sdk.amazonaws.com/swift/api/awslambda/latest/documentation/awslambda)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
