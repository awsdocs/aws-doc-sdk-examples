# HealthLake code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS HealthLake.

<!--custom.overview.start-->
<!--custom.overview.end-->

_HealthLake _

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

- [CreateFHIRDatastore](health_lake_wrapper.py#L42)
- [DeleteFHIRDatastore](health_lake_wrapper.py#L136)
- [DescribeFHIRDatastore](health_lake_wrapper.py#L84)
- [DescribeFHIRExportJob](health_lake_wrapper.py#L310)
- [DescribeFHIRImportJob](health_lake_wrapper.py#L197)
- [ListFHIRDatastores](health_lake_wrapper.py#L106)
- [ListFHIRExportJobs](health_lake_wrapper.py#L335)
- [ListFHIRImportJobs](health_lake_wrapper.py#L222)
- [ListTagsForResource](health_lake_wrapper.py#L404)
- [StartFHIRExportJob](health_lake_wrapper.py#L272)
- [StartFHIRImportJob](health_lake_wrapper.py#L154)
- [TagResource](health_lake_wrapper.py#L385)
- [UntagResource](health_lake_wrapper.py#L426)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [HealthLake Developer Guide](https://docs.aws.amazon.com/healthlake/latest/devguide/what-is-amazon-health-lake.html)
- [HealthLake API Reference](https://docs.aws.amazon.com/healthlake/latest/APIReference/Welcome.html)
- [SDK for Python HealthLake reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/medical-imaging.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
