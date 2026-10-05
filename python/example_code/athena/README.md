# Athena code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Athena.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Athena _

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

- [Hello Athena](athena_hello.py#L21) (`ListWorkGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn Athena basics](scenarios/athena_basics_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateNamedQuery](athena_wrapper.py#L310)
- [CreateWorkGroup](athena_wrapper.py#L42)
- [DeleteNamedQuery](athena_wrapper.py#L417)
- [DeleteWorkGroup](athena_wrapper.py#L441)
- [GetNamedQuery](athena_wrapper.py#L355)
- [GetQueryExecution](athena_wrapper.py#L148)
- [GetQueryResults](athena_wrapper.py#L219)
- [GetWorkGroup](athena_wrapper.py#L83)
- [ListNamedQueries](athena_wrapper.py#L383)
- [ListQueryExecutions](athena_wrapper.py#L276)
- [StartQueryExecution](athena_wrapper.py#L110)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Athena

This example shows you how to get started using Athena.

```
python athena_hello.py
```

#### Learn Athena basics

This example shows you how to learn Athena basics.

- Create an Athena workgroup.
- Verify the workgroup configuration.
- Create a database and table using DDL queries.
- Run SELECT queries and retrieve results.
- Create, list, and delete named queries.
- List query executions.
- Clean up all resources.

<!--custom.basic_prereqs.athena_Scenario.start-->
<!--custom.basic_prereqs.athena_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/athena_basics_scenario.py
```


<!--custom.basics.athena_Scenario.start-->
<!--custom.basics.athena_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Athena User Guide](https://docs.aws.amazon.com/athena/latest/ug/what-is.html)
- [Athena API Reference](https://docs.aws.amazon.com/athena/latest/APIReference/Welcome.html)
- [SDK for Python (Boto3) Athena reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/athena.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
