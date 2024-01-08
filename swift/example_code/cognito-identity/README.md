# Amazon Cognito Identity code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with Amazon Cognito Identity.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Cognito Identity enables you to create unique identities for your users and federate them with identity providers._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create an identity pool](FindOrCreateIdentityPool/Sources/CognitoIdentityHandler/CognitoIdentityHandler.swift#L94) (`CreateIdentityPool`)
- [Delete an identity pool](FindOrCreateIdentityPool/Sources/CognitoIdentityHandler/CognitoIdentityHandler.swift#L116) (`DeleteIdentityPool`)
- [List identity pools](FindOrCreateIdentityPool/Sources/CognitoIdentityHandler/CognitoIdentityHandler.swift#L27) (`CreateIdentityPool`)


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



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Cognito Identity Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-identity.html)
- [Amazon Cognito Identity API Reference](https://docs.aws.amazon.com/cognitoidentity/latest/APIReference/Welcome.html)
- [SDK for Swift Amazon Cognito Identity reference](https://awslabs.github.io/aws-sdk-swift/reference/0.x/AWSCognito-identity/Home)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0