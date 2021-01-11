# Amazon DynamoDB batch examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to write and retrieve Amazon DynamoDB
data using batch functions.

Boto3 features a 
[batch_writer](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/dynamodb.html#batch-writing) 
function that handles all of the necessary intricacies
of the Amazon DynamoDB batch API on your behalf. This includes buffering, removing
duplicates, and retrying unprocessed items.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)
- Download and extract the 
  [example movie data JSON file](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip). 

## Cautions

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

See the usage demonstration by running the following at a command prompt.

```
python dynamo_batching.py
```  

### Example structure

**dynamo_batching.py**

Reads from a JSON file that contains movie data and uses the data to create and fill
two Amazon DynamoDB tables. One table holds movie data from the JSON file, and the 
other contains actor data compiled from the movie data.

Uses the Boto3 `batch_writer` function to write data in batches to the two tables, 
and uses the `batch_get_item` function to retrieve data in batches.

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/dynamodb/batching 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon DynamoDB examples](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/dynamodb.html)
- [Boto3 Amazon DynamoDB service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/dynamodb.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
