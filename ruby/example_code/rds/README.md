# Amazon RDS code examples for the AWS SDK for Ruby
## Overview
These examples show how to create and manage Amazon Relational Database Service (Amazon RDS) instances and clusters using the AWS SDK for Ruby.

Amazon Relational Database Service (Amazon RDS) is a web service that makes it easier to set up, operate, and scale a relational database in the cloud.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a cluster snapshot](./create_cluster_snapshot.rb) (`CreateDbClusterSnapshot`)

* [Create a snapshot](./create_snapshot.rb) (`CreateDbSnapshot`)

* [List all instances](./list_instances.rb) (`DescribeDBInstances`)

* [List a cluster's snapshots](./list_cluster_snapshots.rb) (`DescribeDbClusterSnapshots`)

* [List instance snapshots](./list_instance_snapshots.rb) (`DescribeDbSnapshots`)

* [List parameter groups](./list_parameter_groups.rb) (`DescribeDbParameterGroups`)

* [List subnet groups](./list_security_groups.rb) (`GetDbClusterParameters`)

* [List security groups](./list_subnet_groups.rb) (`GetDbClusterParameters`)






## Run the examples

### Prerequisites

See the [Ruby README.md(https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md) for pre-requisites.

### Instructions
The easiest way to interact with this example code is by invoking individual [Actions](#Actions) from your Command Line Interface (CLI). This may require some modification to override hard-coded values, and some actions also expect runtime parameters. For example, `ruby some_action.rb ARG1 ARG2` will invoke `some_action.rb` with two arguments.

## Contributing
Code examples thrive on community contribution!

* To propose a new example, submit an [Enhancement Request](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fenhancement&template=enhancement.yaml&title=%5BEnhancement%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~2 min).
* To fix a bug, submit a [Bug Report](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fbug&template=bug.yaml&title=%5BBug%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~5 min).
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)
### Testing
⚠️ Running tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
