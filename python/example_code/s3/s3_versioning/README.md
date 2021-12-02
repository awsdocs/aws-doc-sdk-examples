# Amazon S3 batch and versioning examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to set up an Amazon S3 bucket for 
versioning, how to perform tasks on a version-enabled bucket, and how to act on
versioned objects in batches by creating jobs that call AWS Lambda functions. 

* Create a version-enabled bucket and apply revisions to its objects.
* Get a full series of object versions.
* Roll back to a previous version.
* Revive a deleted object.
* Permanently delete all versions of an object.
* Create batch jobs that invoke Lambda functions to update objects.

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any 
amount of data at any time, from anywhere on the web.*

## Code examples

### Scenario examples

* [Manage versioned objects in batches with a &LAM; function](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/s3/s3_versioning/batch_versioning.py)
* [Permanently delete a versioned object](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/s3/s3_versioning/versioning.py)
* [Remove delete markers from versioned objects](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/s3/s3_versioning/remove_delete_marker.py)
* [Revive a deleted object](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/s3/s3_versioning/versioning.py)
* [Roll back an object to a specific version](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/s3/s3_versioning/versioning.py)
* [Work with versioned objects](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/s3/s3_versioning/versioning.py)

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
- Python 3.8.0 or later
- Boto3 1.13.2 or later
- PyTest 5.3.5 or later (to run unit tests)

### Single-object demo

The `usage_demo_single_object` function in `versioning.py` demonstrates basic requests 
to manipulate a versioned object. The script performs the following actions.

1. Creates a version-enabled bucket and uploads a stanza from the poem *You Are Old,
Father William* by Lewis Carroll.
1. Applies a series of revisions to the stanza object.
1. Gets and prints the latest version of the stanza.
1. Gets the full series of versions of the stanza.
1. Rolls back two revisions and prints the result.
1. Deletes the stanza.
1. Revives the stanza.
1. Permanently deletes all versions of the stanza.
1. Deletes the bucket.

Run the following command to see the single-object demo.

```
python -m versioning
``` 

### Batch operation demo

The `batch_versioning.py` file contains setup, usage, and teardown functions that 
demonstrate how to use Lambda functions to perform batch operations on versioned
objects. The scripts perform the following actions.

`setup_demo`
1. Creates an AWS Access and Identity Management (IAM) role and attached policy that 
has the permissions needed by the Lambda functions used in this demo.
1. Creates Lambda functions that perform revisions on objects and remove delete markers
from versioned objects.
1. Creates a version-enabled bucket and uploads the stanzas from the poem *You Are Old,
Father William* by Lewis Carroll.

`usage_demo_batch_operations`
1. Creates a batch job that performs a series of random revisions on each stanza 
object, using the `revise_stanza` Lambda function.
1. Creates a batch job that revives any stanzas that were deleted during the revision
step, using the `remove_delete_marker` Lambda function.
1. Creates many delete markers and other revisions in the bucket.
1. Creates a batch job that removes all delete markers from the bucket, using the
`remove_delete_marker` Lambda function.

`teardown_demo`
1. Deletes the IAM role and policies created during setup.
1. Deletes Lambda functions created during setup.
1. Empties and deletes the bucket created during setup.

`revise_stanza.py`

This file contains code that is uploaded as a Lambda handler to perform revisions on
objects during the demonstration.

`remove_delete_marker.py`

This file contains code that is uploaded as a Lambda handler to remove delete markers
from versioned objects during the demonstration. 

Run the following command to see the batch operation demo.

```
python -m batch_versioning
``` 

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/s3/s3_versioning 
folder.

```
python -m pytest
```

## Additional information

- [Boto3 Amazon S3 service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)
- [Amazon S3 documentation](https://docs.aws.amazon.com/s3)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
