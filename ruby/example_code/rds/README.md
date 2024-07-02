# Amazon RDS code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with Amazon Relational Database Service (Amazon RDS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon RDS is a web service that makes it easier to set up, operate, and scale a relational database in the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `ruby` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon RDS](hello/hello_rds.rb#L4) (`DescribeDBInstances`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBSnapshot](create_snapshot.rb#L4)
- [DescribeDBInstances](list_instances.rb#L4)
- [DescribeDBParameterGroups](list_parameter_groups.rb#L4)
- [DescribeDBParameters](list_parameter_groups.rb#L4)
- [DescribeDBSnapshots](list_instance_snapshots.rb#L6)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Single Action](#single-actions) from your command line. For example, `ruby some_example.rb` will invoke `some_example.rb`.
<!--custom.instructions.end-->

#### Hello Amazon RDS

This example shows you how to get started using Amazon RDS.

```
ruby hello/hello_rds.rb
```


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `ruby` folder.



<!--custom.tests.start-->

## Contribute
Code examples thrive on community contribution.

To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md).
<!--custom.tests.end-->

## Additional resources

- [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
- [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for Ruby Amazon RDS reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Rds.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0