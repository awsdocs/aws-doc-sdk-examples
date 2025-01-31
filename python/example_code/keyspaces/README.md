# Amazon Keyspaces code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Keyspaces (for Apache Cassandra).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Keyspaces is a scalable, highly available, and managed Apache Cassandra-compatible database service._

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

- [Hello Amazon Keyspaces](hello.py#L4) (`ListKeyspaces`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_get_started_keyspaces.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateKeyspace](keyspace.py#L32)
- [CreateTable](keyspace.py#L109)
- [DeleteKeyspace](keyspace.py#L277)
- [DeleteTable](keyspace.py#L256)
- [GetKeyspace](keyspace.py#L57)
- [GetTable](keyspace.py#L147)
- [ListKeyspaces](keyspace.py#L86)
- [ListTables](keyspace.py#L177)
- [RestoreTable](keyspace.py#L224)
- [UpdateTable](keyspace.py#L199)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Keyspaces

This example shows you how to get started using Amazon Keyspaces.

```
python hello.py
```

#### Learn the basics

This example shows you how to do the following:

- Create a keyspace and table. The table schema holds movie data and has point-in-time recovery enabled.
- Connect to the keyspace using a secure TLS connection with SigV4 authentication.
- Query the table. Add, retrieve, and update movie data.
- Update the table. Add a column to track watched movies.
- Restore the table to its previous state and clean up resources.

<!--custom.basic_prereqs.keyspaces_Scenario_GetStartedKeyspaces.start-->
<!--custom.basic_prereqs.keyspaces_Scenario_GetStartedKeyspaces.end-->

Start the example by running the following at a command prompt:

```
python scenario_get_started_keyspaces.py
```


<!--custom.basics.keyspaces_Scenario_GetStartedKeyspaces.start-->
<!--custom.basics.keyspaces_Scenario_GetStartedKeyspaces.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Keyspaces Developer Guide](https://docs.aws.amazon.com/keyspaces/latest/devguide/what-is-keyspaces.html)
- [Amazon Keyspaces API Reference](https://docs.aws.amazon.com/keyspaces/latest/APIReference/Welcome.html)
- [SDK for Python Amazon Keyspaces reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/keyspaces.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0