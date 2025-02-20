# Amazon Redshift code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Redshift.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Redshift is a fast, fully managed, petabyte-scale data warehouse service that makes it simple and cost-effective to efficiently analyze all your data using your existing business intelligence tools._

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

- [Hello Amazon Redshift](hello.py#L4) (`DescribeClusters`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](redshift_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCluster](redshift.py#L39)
- [DeleteCluster](redshift.py#L81)
- [DescribeClusters](redshift.py#L125)
- [DescribeStatement](redshift_data.py#L94)
- [GetStatementResult](redshift_data.py#L115)
- [ModifyCluster](redshift.py#L102)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Redshift

This example shows you how to get started using Amazon Redshift.

```
python hello.py
```

#### Learn the basics

This example shows you how to do the following:

- Create a Redshift cluster.
- List databases in the cluster.
- Create a table named Movies.
- Populate the Movies table.
- Query the Movies table by year.
- Modify the Redshift cluster.
- Delete the Amazon Redshift cluster.

<!--custom.basic_prereqs.redshift_Scenario.start-->
<!--custom.basic_prereqs.redshift_Scenario.end-->

Start the example by running the following at a command prompt:

```
python redshift_scenario.py
```


<!--custom.basics.redshift_Scenario.start-->
<!--custom.basics.redshift_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Redshift Management Guide](https://docs.aws.amazon.com/redshift/latest/mgmt/welcome.html)
- [Amazon Redshift API Reference](https://docs.aws.amazon.com/redshift/latest/APIReference/Welcome.html)
- [SDK for Python Amazon Redshift reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/redshift.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
