# IAM code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](src/main/kotlin/com/kotlin/iam/AttachRolePolicy.kt#L42)
- [CreateAccessKey](src/main/kotlin/com/kotlin/iam/CreateAccessKey.kt#L40)
- [CreateAccountAlias](src/main/kotlin/com/kotlin/iam/CreateAccountAlias.kt#L39)
- [CreatePolicy](src/main/kotlin/com/kotlin/iam/CreatePolicy.kt#L39)
- [CreateUser](src/main/kotlin/com/kotlin/iam/CreateUser.kt#L39)
- [DeleteAccessKey](src/main/kotlin/com/kotlin/iam/DeleteAccessKey.kt#L40)
- [DeleteAccountAlias](src/main/kotlin/com/kotlin/iam/DeleteAccountAlias.kt#L38)
- [DeletePolicy](src/main/kotlin/com/kotlin/iam/DeletePolicy.kt#L38)
- [DeleteUser](src/main/kotlin/com/kotlin/iam/DeleteUser.kt#L38)
- [DetachRolePolicy](src/main/kotlin/com/kotlin/iam/DetachRolePolicy.kt#L40)
- [GetPolicy](src/main/kotlin/com/kotlin/iam/GetPolicy.kt#L38)
- [ListAccessKeys](src/main/kotlin/com/kotlin/iam/ListAccessKeys.kt#L38)
- [ListAccountAliases](src/main/kotlin/com/kotlin/iam/ListAccountAliases.kt#L23)
- [ListUsers](src/main/kotlin/com/kotlin/iam/ListUsers.kt#L23)
- [UpdateUser](src/main/kotlin/com/kotlin/iam/UpdateUser.kt#L40)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a user and assume a role](src/main/kotlin/com/kotlin/iam/IAMScenario.kt)


<!--custom.examples.start-->

### Custom Examples

- **AccessKeyLastUsed** - Demonstrates how to display the time that an access key was last used.
- **IAMScenario** - Demonstrates how to perform various AWS IAM operations.
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

#### JSON File

To successfully run the **IAMScenario**, you need a JSON file that contains the information to create a role. Included in this file is the ARN of the IAM user for the trust relationship. The following JSON shows an example.

    {
     "Version": "2012-10-17",
      "Statement": [
       {
       "Effect": "Allow",
       "Principal": {
         "AWS": "<Enter the IAM User ARN value>"
       },
       "Action": "sts:AssumeRole",
       "Condition": {}
      }
     ]
    }

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information,
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html).

<!--custom.instructions.end-->



#### Create a user and assume a role

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.end-->


<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for Kotlin IAM reference](https://sdk.amazonaws.com/kotlin/api/latest/iam/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0