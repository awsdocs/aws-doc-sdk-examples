# Amazon Keyspaces code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Keyspaces 
(for Apache Cassandra).

*Amazon Keyspaces is a scalable, highly available, and managed Apache Cassandra–compatible 
database service.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Hello

* [Hello Amazon Keyspaces](hello.py)
(`ListKeyspaces`)

### Single actions

Code excerpts that show you how to call individual service functions.

* [Create a keyspace](keyspace.py)
(`CreateKeyspace`)
* [Create a table](keyspace.py)
(`CreateTable`)
* [Delete a keyspace](keyspace.py)
(`DeleteKeyspace`)
* [Delete a table](keyspace.py)
(`DeleteTable`)
* [Get data about a table](keyspace.py)
(`GetTable`)
* [Get keyspace data](keyspace.py)
(`GetKeyspace`)
* [List keyspaces](keyspace.py)
(`ListKeyspaces`)
* [List tables in a keyspace](keyspace.py)
(`ListTables`)
* [Restore a table to a point in time](keyspace.py)
(`RestoreTable`)
* [Update a table](keyspace.py)
(`UpdateTable`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with keyspaces and tables](scenario_get_started_keyspaces.py)

## Run the examples

### Prerequisites

To find prerequisites for running these examples, see the
[README](../../README.md#Prerequisites) in the Python folder.

In addition to the standard prerequisites, the get started scenario requires these
additional packages:

* cassandra-driver 3.25.0 or later
* cassandra-sigv4 4.0.2 or later
* requests 2.25.1 or later

You can install these prerequisites by running the following command in a
virtual environment:

```
python -m pip install -r requirements.txt
``` 

### Instructions

#### Get started with keyspaces and tables

Run an interactive example that shows you how to:

* Create and manage a keyspace and table.
* Query the table over a secure TLS connection that's authenticated by Signature V4 (SigV4).
* Update and restore the table.

Start the example by running the following at a command prompt:

```
python scenario_get_started_keyspaces.py
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the Python folder.

## Additional resources
* [Amazon Keyspaces Developer Guide](https://docs.aws.amazon.com/keyspaces/latest/devguide/what-is-keyspaces.html)
* [Amazon Keyspaces API Reference](https://docs.aws.amazon.com/keyspaces/latest/APIReference/Welcome.html)
* [SDK for Python Amazon Keyspaces client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/keyspaces.html) 

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
