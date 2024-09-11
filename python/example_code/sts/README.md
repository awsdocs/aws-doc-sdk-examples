# AWS STS code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Security Token Service (AWS STS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS STS creates and provides trusted users with temporary security credentials that can control access to your AWS resources._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [AssumeRole](assume_role_mfa.py#L181)
- [GetSessionToken](session_token.py#L117)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Assume an IAM role that requires an MFA token](assume_role_mfa.py)
- [Construct a URL for federated users](federated_url.py)
- [Get a session token that requires an MFA token](session_token.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Assume an IAM role that requires an MFA token

This example shows you how to assume a role that requires an MFA token.

- Create an IAM role that grants permission to list Amazon S3 buckets.
- Create an IAM user that has permission to assume the role only when MFA credentials are provided.
- Register an MFA device for the user.
- Assume the role and use temporary credentials to list S3 buckets.

<!--custom.scenario_prereqs.sts_Scenario_AssumeRoleMfa.start-->
<!--custom.scenario_prereqs.sts_Scenario_AssumeRoleMfa.end-->

Start the example by running the following at a command prompt:

```
python assume_role_mfa.py
```


<!--custom.scenarios.sts_Scenario_AssumeRoleMfa.start-->
<!--custom.scenarios.sts_Scenario_AssumeRoleMfa.end-->

#### Construct a URL for federated users

This example shows you how to do the following:

- Create an IAM role that grants read-only access to the current account's Amazon S3 resources.
- Get a security token from the AWS federation endpoint.
- Construct a URL that can be used to access the console with federated credentials.

<!--custom.scenario_prereqs.sts_Scenario_ConstructFederatedUrl.start-->
<!--custom.scenario_prereqs.sts_Scenario_ConstructFederatedUrl.end-->

Start the example by running the following at a command prompt:

```
python federated_url.py
```


<!--custom.scenarios.sts_Scenario_ConstructFederatedUrl.start-->
<!--custom.scenarios.sts_Scenario_ConstructFederatedUrl.end-->

#### Get a session token that requires an MFA token

This example shows you how to get a session token that requires an MFA token.

- Create an IAM role that grants permission to list Amazon S3 buckets.
- Create an IAM user that has permission to assume the role only when MFA credentials are provided.
- Register an MFA device for the user.
- Provide MFA credentials to get a session token and use temporary credentials to list S3 buckets.

<!--custom.scenario_prereqs.sts_Scenario_SessionTokenMfa.start-->
<!--custom.scenario_prereqs.sts_Scenario_SessionTokenMfa.end-->

Start the example by running the following at a command prompt:

```
python session_token.py
```


<!--custom.scenarios.sts_Scenario_SessionTokenMfa.start-->
<!--custom.scenarios.sts_Scenario_SessionTokenMfa.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS STS User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp.html)
- [AWS STS API Reference](https://docs.aws.amazon.com/STS/latest/APIReference/welcome.html)
- [SDK for Python AWS STS reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sts.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0