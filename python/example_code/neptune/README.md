# Neptune code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Neptune.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Neptune is a serverless graph database designed for superior scalability and availability._

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

- [Hello Neptune](HelloNeptune.py#L4) (`DescribeDBClustersPaginator`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](NeptuneScenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBCluster](NeptuneScenario.py#L288)
- [CreateDBInstance](NeptuneScenario.py#L269)
- [CreateDBSubnetGroup](NeptuneScenario.py#L335)
- [CreateGraph](analytics/CreateNeptuneGraphExample.py#L7)
- [DeleteDBCluster](NeptuneScenario.py#L14)
- [DeleteDBInstance](NeptuneScenario.py#L77)
- [DeleteDBSubnetGroup](NeptuneScenario.py#L95)
- [DescribeDBClusters](NeptuneScenario.py#L203)
- [DescribeDBInstances](NeptuneScenario.py#L242)
- [ExecuteGremlinProfileQuery](database/NeptuneGremlinQueryExample.py#L22)
- [ExecuteGremlinQuery](database/NeptuneGremlinExplainAndProfileExample.py#L8)
- [ExecuteOpenCypherExplainQuery](database/OpenCypherExplainExample.py#L22)
- [ExecuteQuery](analytics/NeptuneAnalyticsQueryExample.py#L7)
- [StartDBCluster](NeptuneScenario.py#L161)
- [StopDBCluster](NeptuneScenario.py#L183)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Neptune

This example shows you how to get started using Neptune.

```
python HelloNeptune.py
```

#### Learn the basics

This example shows you how to do the following:

- Create an Amazon Neptune Subnet Group.
- Create an Neptune Cluster.
- Create an Neptune Instance.
- Check the status of the Neptune Instance.
- Show Neptune cluster details.
- Stop the Neptune cluster.
- Start the Neptune cluster.
- Delete the Neptune Assets.

<!--custom.basic_prereqs.neptune_Scenario.start-->
<!--custom.basic_prereqs.neptune_Scenario.end-->

Start the example by running the following at a command prompt:

```
python NeptuneScenario.py
```


<!--custom.basics.neptune_Scenario.start-->
<!--custom.basics.neptune_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Neptune User Guide](https://docs.aws.amazon.com/neptune/latest/userguide/intro.html)
- [Neptune API Reference](https://docs.aws.amazon.com/neptune/latest/apiref/Welcome.html)
- [SDK for Python Neptune reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iam.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
