# Amazon GuardDuty code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon GuardDuty.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon GuardDuty is a threat detection service that continuously monitors for malicious activity and unauthorized behavior to protect your AWS accounts and workloads._

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

### Get started

- [Hello GuardDuty](guardduty_hello.py#L15) (`ListDetectors`)

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDetector](guardduty_wrapper.py#L35)
- [CreateSampleFindings](guardduty_wrapper.py#L108)
- [DeleteDetector](guardduty_wrapper.py#L189)
- [GetDetector](guardduty_wrapper.py#L78)
- [GetFindings](guardduty_wrapper.py#L162)
- [ListDetectors](guardduty_wrapper.py#L56)
- [ListFindings](guardduty_wrapper.py#L135)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Learn the basics of GuardDuty](scenario_guardduty_basics.py)

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello GuardDuty

This example shows you how to get started using GuardDuty.

```
python guardduty_hello.py
```


#### Learn the basics of GuardDuty

This example shows you how to do the following:

* Create a GuardDuty detector to enable threat detection.
* Generate sample findings for demonstration purposes.
* List and examine findings by severity.
* Delete the detector to clean up resources.

```
python scenario_guardduty_basics.py
```

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon GuardDuty User Guide](https://docs.aws.amazon.com/guardduty/latest/ug/what-is-guardduty.html)
- [Amazon GuardDuty API Reference](https://docs.aws.amazon.com/guardduty/latest/APIReference/Welcome.html)
- [AWS SDK for Python (Boto3) GuardDuty reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/guardduty.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0