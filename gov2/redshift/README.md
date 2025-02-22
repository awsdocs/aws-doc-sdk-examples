# Amazon Redshift code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with Amazon Redshift.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Redshift](hello/hello.go#L4) (`DescribeClusters`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/redshift_basics.go)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCluster](actions/redshift_actions.go#L30)
- [DeleteCluster](actions/redshift_actions.go#L85)
- [DescribeClusters](actions/redshift_actions.go#L115)
- [ModifyCluster](actions/redshift_actions.go#L59)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Redshift

This example shows you how to get started using Amazon Redshift.

```
go run ./hello
```

#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
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


<!--custom.basics.redshift_Scenario.start-->
<!--custom.basics.redshift_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Redshift Management Guide](https://docs.aws.amazon.com/redshift/latest/mgmt/welcome.html)
- [Amazon Redshift API Reference](https://docs.aws.amazon.com/redshift/latest/APIReference/Welcome.html)
- [SDK for Go V2 Amazon Redshift reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/redshift)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
