# Amazon Redshift code examples for the SDK for .NET (v4)

## Overview

Shows how to use the AWS SDK for .NET (v4) to work with Amazon Redshift.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Redshift](Actions/HelloRedshift.cs#L20) (`DescribeClusters`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Actions/RedshiftWrapper.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCluster](Actions/RedshiftWrapper.cs#L34)
- [DeleteCluster](Actions/RedshiftWrapper.cs#L156)
- [DescribeClusters](Actions/RedshiftWrapper.cs#L77)
- [DescribeStatement](Actions/RedshiftWrapper.cs#L388)
- [GetStatementResult](Actions/RedshiftWrapper.cs#L419)
- [ListDatabases](Actions/RedshiftWrapper.cs#L189)
- [ModifyCluster](Actions/RedshiftWrapper.cs#L122)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Redshift

This example shows you how to get started using Amazon Redshift.


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


<!--custom.basics.redshift_Scenario.start-->
<!--custom.basics.redshift_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Redshift Management Guide](https://docs.aws.amazon.com/redshift/latest/mgmt/welcome.html)
- [Amazon Redshift API Reference](https://docs.aws.amazon.com/redshift/latest/APIReference/Welcome.html)
- [SDK for .NET (v4) Amazon Redshift reference](https://docs.aws.amazon.com/sdkfornet/v4/apidocs/items/Redshift/NRedshift.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
