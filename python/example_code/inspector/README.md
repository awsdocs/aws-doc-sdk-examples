# Amazon Inspector code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Inspector.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Inspector _

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

- [Hello Amazon Inspector](inspector2_hello.py#L12) (`ListMembers`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_inspector_basics.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchGetAccountStatus](inspector_wrapper.py#L48)
- [BatchGetFindingDetails](inspector_wrapper.py#L220)
- [CreateFilter](inspector_wrapper.py#L253)
- [DeleteFilter](inspector_wrapper.py#L348)
- [Disable](inspector_wrapper.py#L376)
- [Enable](inspector_wrapper.py#L77)
- [ListCoverage](inspector_wrapper.py#L109)
- [ListCoverageStatistics](inspector_wrapper.py#L141)
- [ListFilters](inspector_wrapper.py#L292)
- [ListFindings](inspector_wrapper.py#L183)
- [ListUsageTotals](inspector_wrapper.py#L316)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Inspector

This example shows you how to get started using Amazon Inspector.

```
python inspector2_hello.py
```

#### Learn the basics

This example shows you how to do the following:

- Check Inspector account status.
- Ensure Inspector is enabled.
- Analyze security findings.
- Check scan coverage.
- Create a findings filter.
- List existing filters.
- Check usage and costs.
- Get coverage statistics.
- Delete a filter.

<!--custom.basic_prereqs.inspector_Scenario.start-->
<!--custom.basic_prereqs.inspector_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenario_inspector_basics.py
```


<!--custom.basics.inspector_Scenario.start-->
<!--custom.basics.inspector_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Inspector User Guide](https://docs.aws.amazon.com/inspector/latest/user/what-is-inspector.html)
- [Amazon Inspector API Reference](https://docs.aws.amazon.com/inspector/latest/APIReference/Welcome.html)
- [SDK for Python (Boto3) Amazon Inspector reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/inspector.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
