# Amazon Cognito Identity Provider code examples for the SDK for .NET (v4)

## Overview

Shows how to use the AWS SDK for .NET (v4) to work with Amazon Cognito Identity Provider.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Cognito Identity Provider handles user authentication and authorization for your web and mobile apps._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
These examples also require the following resources:

* An existing Amazon Cognito user pool that is configured to allow self sign-up.
* A client ID to use for authenticating with Amazon Cognito.


To create these resources, run the AWS CloudFormation script in the
[resources/cdk/cognito_scenario_user_pool_with_mfa](../../resources/cdk/cognito_scenario_user_pool_with_mfa)
folder. This script outputs a user pool ID and a client ID that you can use to run
the scenario.
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [ListUserPools](Actions/CognitoWrapper.cs#L25)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Cognito Identity Provider Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
- [Amazon Cognito Identity Provider API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
- [SDK for .NET (v4) Amazon Cognito Identity Provider reference](https://docs.aws.amazon.com/sdkfornet/v4/apidocs/items/Cognito-identity-provider/NCognito-identity-provider.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
