# Organizations code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Organizations.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Organizations consolidates multiple AWS accounts into an organization that you create and centrally manage._

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

- [AttachPolicy](organizations_policies.py#L99)
- [CreatePolicy](organizations_policies.py#L21)
- [DeletePolicy](organizations_policies.py#L144)
- [DescribePolicy](organizations_policies.py#L76)
- [DetachPolicy](organizations_policies.py#L122)
- [ListPolicies](organizations_policies.py#L53)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
Run this example at a command prompt with the following command.

```
python organizations_policies.py [--target TARGET]
``` 

The example optionally attaches and detaches the demo policy to an AWS Organizations
resource, such as a root organization or account. If you want to include this in the
demo, replace `TARGET` in the command with the ID of the resource. 
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Organizations User Guide](https://docs.aws.amazon.com/organizations/latest/userguide/orgs_introduction.html)
- [Organizations API Reference](https://docs.aws.amazon.com/organizations/latest/userguide/orgs_introduction.html)
- [SDK for Python Organizations reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/organizations.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0