# Amazon DynamoDB examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to create Amazon DynamoDB 
tables and move data in and out of them.
 
* Create a table for storing movies.
* Load movies into the table from a JSON-formatted file.
* Update and query movies in the table.
* Get, write, and delete items in batches.
* Run PartiQL queries on a DynamoDB table. 
* Accelerate reads with DynamoDB Accelerator (DAX).

*Amazon DynamoDB is a fully managed NoSQL database service that provides fast and 
predictable performance with seamless scalability.*

## Code examples

**Scenarios**

* [Getting started with tables, items, and queries](GettingStarted/scenario_getting_started_movies.py)
* [Get, write, and delete batches of items](batching/dynamo_batching.py)
* [Query a table using PartiQL](partiql/scenario_partiql_single.py)
* [Query a table by using batches of PartiQL statements](partiql/scenario_partiql_batch.py)
* [Accelerate reads with DAX](TryDax)


**Actions**

* [Creating a table](GettingStarted/scenario_getting_started_movies.py)
(`create_table`)
* [Deleting a table](GettingStarted/scenario_getting_started_movies.py)
(`delete_table`)
* [Deleting an item from a table](GettingStarted/scenario_getting_started_movies.py)
(`delete_item`)
* [Deleting an item from a table if it meets a condition](GettingStarted/update_and_query.py)
(`delete_item`)
* [Getting an item from a table](GettingStarted/scenario_getting_started_movies.py)
(`get_item`)
* [Putting an item into a table](GettingStarted/scenario_getting_started_movies.py)
(`put_item`)
* [Putting items loaded from a JSON file into a table](GettingStarted/scenario_getting_started_movies.py)
(`put_item`)
* [Querying items and projecting a subset of data](GettingStarted/update_and_query.py)
(`query`)
* [Querying items by using a key condition expression](GettingStarted/scenario_getting_started_movies.py)
(`query`)
* [Running a PartiQL statement](partiql/scenario_partiql_single.py)
(`execute_statement`)
* [Running batches of PartiQL statements](partiql/scenario_partiql_batch.py)
(`batch_execute_statement`)
* [Scanning a table for items](GettingStarted/scenario_getting_started_movies.py)
(`scan`)
* [Updating an item by using a conditional expression](GettingStarted/update_and_query.py)
(`update_item`)
* [Updating an item by using an update expression](GettingStarted/update_and_query.py)
(`update_item`)
* [Updating an item in a table](GettingStarted/scenario_getting_started_movies.py)
(`update_item`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8.8 or later
- Boto 3 1.18.50 or later
- Requests 2.25.1 or later (to download the movie data JSON file)
- Amazon DynamoDB Accelerator (DAX) client 1.1.7 or later (to run the DAX example)
- pytest 5.3.5 or later (to run unit tests)

You can install these prerequisites by running the following command in a
virtual environment:

```
python -m pip install -r requirements.txt
``` 

### Getting started with tables, items, and queries

This scenario shows you how to create an Amazon DynamoDB table for storing movie data. 
The scenario loads movies into the table from a JSON-formatted file, walks you 
through an interactive demo to add, update, and delete movies one at a time, and 
shows you how to query for sets of movies with various parameters.

The demo uses the Requests package to download a `moviedata.json` file of movie data.
If you prefer, you can download and extract the file to the `GettingStarted` folder
from the 
[Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip). 

To start the scenario, navigate to the `GettingStarted` folder and run the following 
at a command prompt:

```
python scenario_getting_started_movies.py
```

### Get, write, and delete batches of items

This scenario shows you how to write and retrieve DynamoDB data using batch functions.

Boto3 features a 
[batch_writer](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/dynamodb.html#batch-writing) 
function that handles all of the necessary intricacies
of the DynamoDB batch API on your behalf. This includes buffering, removing
duplicates, and retrying unprocessed items.

The demo requires a `moviedata.json` file of movie data in the `batching` folder.
You can download and extract the file from the 
[Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip). 

To start the scenario, navigate to the `batching` folder and run the following at a 
command prompt:

```
python dynamo_batching.py
```  

### Query a table using PartiQL

These two scenarios show you how to run PartiQL statements to query a DynamoDB table of 
movie data. You can do this one at a time or in batches. Both scenarios add, get, 
update, and delete movies in the table.

To start a scenario, navigate to the `partiql` folder and run one of the 
following at a command prompt:

```
python scenario_partiql_single.py
python scenario_partiql_batch.py
```

### Accelerate reads with DAX

Shows you how to use the Amazon DAX Client for Python to read items from a DynamoDB 
table. Retrieval, query, and scan speeds are measured for both Boto3 and DAX clients 
to show some of the performance advantages of using DAX.

DAX is a DynamoDB-compatible caching service that provides fast in-memory performance 
and high availability for applications that demand microsecond latency. For more
information, see [In-Memory Acceleration with DynamoDB Accelerator (DAX)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DAX.html). 

#### Running on your local computer

When run on your local computer, only the Boto3 client works.

Each file can be run separately at a command prompt. For example, create the
table by running the following from a command prompt window.

```commandline
python 01-create-table.py
```  

#### Running on a DAX cluster

To run the scripts with the DAX client, you must run them on an Amazon Elastic Compute 
Cloud (Amazon EC2) instance within your virtual private cloud (VPC). This process is 
described in the Python sample application tutorial in the  
[Developing with the DAX Client](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DAX.client.html) 
section of the *Amazon DynamoDB Developer Guide*.

The tutorial shows you how to set up the following additional resources:

- A VPC from Amazon Virtual Private Cloud (Amazon VPC)
- A DAX cluster set up in your VPC 
- An EC2 instance running in your VPC with the following installed:
    - Python 3.7 or later
    - Boto3 1.11.10 or later
    - Amazon DAX Client for Python 1.1.7 or later

On an EC2 instance, run the get item, query, and scan test scripts with the DAX client
by specifying a DAX cluster endpoint as the first positional argument.
To run the query test script with the DAX client, run the following from a command 
prompt window.

```commandline
python 04-query-test.py YOUR-CLUSTER-NAME.111111.clustercfg.dax.usw2.cache.amazonaws.com:8111
```

## Running the tests

All tests use pytest, and you can find them in the `test` folder of each example.

### Unit tests

The unit tests in this module use the botocore Stubber. Stubber captures requests before 
they are sent to AWS and returns a mocked response so that no charges are incurred on 
your account.

Run unit tests at a command prompt from the example subfolder by including the 
`"not integ"` marker.

```
python -m pytest -m "not integ"
```

### Integration tests

**Note:** The integration tests in this module make actual requests to AWS, which means 
they can create and destroy resources in your account. These tests might also incur 
charges. Proceed with caution.

Run integration tests at a command prompt by including the `"integ"` marker.

```
python -m pytest -m "integ"
```

## Additional information

- [Boto3 Amazon DynamoDB service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/dynamodb.html)
- [Amazon DynamoDB documentation](https://docs.aws.amazon.com/dynamodb)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
