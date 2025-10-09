# Amazon Inspector code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Inspector.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Inspector is a vulnerability management service that continuously scans AWS workloads for software vulnerabilities and unintended network exposure._

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

- [Hello Amazon Inspector](inspector_hello.py#L15) (`BatchGetAccountStatus`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchGetAccountStatus](inspector_wrapper.py#L35)
- [BatchGetFindingDetails](inspector_wrapper.py#L125)
- [Disable](inspector_wrapper.py#L175)
- [Enable](inspector_wrapper.py#L25)
- [ListCoverage](inspector_wrapper.py#L145)
- [ListFindings](inspector_wrapper.py#L75)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Learn the basics of Amazon Inspector](scenario_inspector_basics.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Inspector

This example shows you how to get started using Amazon Inspector.

```
python inspector_hello.py
```


#### Learn the basics of Amazon Inspector

This example shows you how to learn the basics of Amazon Inspector.


<!--custom.scenario_prereqs.inspector_Scenario.start-->
<!--custom.scenario_prereqs.inspector_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenario_inspector_basics.py
```


<!--custom.scenarios.inspector_Scenario.start-->
<!--custom.scenarios.inspector_Scenario.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Inspector User Guide](https://docs.aws.amazon.com/inspector/latest/user/)
- [Amazon Inspector API Reference](https://docs.aws.amazon.com/inspector/v2/APIReference/)
- [SDK for Python Amazon Inspector reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/inspector2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0