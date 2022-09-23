# Amazon S3 code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to get started with bucket and 
object operations in Amazon Simple Storage Service (Amazon S3). 
Learn to create, get, remove, and configure buckets and objects.

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any 
amount of data at any time, from anywhere on the web.*

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello Amazon S3](hello.py)

### Single actions

Code excerpts that show you how to call individual service functions.

* [Add CORS rules to a bucket](bucket_wrapper.py)
(`PutBucketCors`)
* [Add a lifecycle configuration to a bucket](bucket_wrapper.py)
(`PutBucketLifecycleConfiguration`)
* [Add a policy to a bucket](bucket_wrapper.py)
(`PutBucketPolicy`)
* [Copy an object from one bucket to another](object_wrapper.py)
(`CopyObject`)
* [Create a bucket](bucket_wrapper.py)
(`CreateBucket`)
* [Delete CORS rules from a bucket](bucket_wrapper.py)
(`DeleteBucketCors`)
* [Delete a policy from a bucket](bucket_wrapper.py)
(`DeleteBucketPolicy`)
* [Delete an empty bucket](bucket_wrapper.py)
(`DeleteBucket`)
* [Delete an object](object_wrapper.py)
(`DeleteObject`)
* [Delete multiple objects](object_wrapper.py)
(`DeleteObjects`)
* [Delete the lifecycle configuration of a bucket](bucket_wrapper.py)
(`DeleteBucketLifecycle`)
* [Determine the existence of a bucket](bucket_wrapper.py)
(`HeadBucket`)
* [Get CORS rules for a bucket](bucket_wrapper.py)
(`GetBucketCors`)
* [Get the ACL of a bucket](bucket_wrapper.py)
(`GetBucketAcl`)
* [Get the ACL of an object](object_wrapper.py)
(`GetObjectAcl`)
* [Get the lifecycle configuration of a bucket](bucket_wrapper.py)
(`GetBucketLifecycleConfiguration`)
* [Get the policy for a bucket](bucket_wrapper.py)
(`GetBucketPolicy`)
* [List buckets](bucket_wrapper.py)
(`ListBuckets`)
* [List objects in a bucket](object_wrapper.py)
(`ListObjects`)
* [Read data from a bucket](object_wrapper.py)
(`GetObject`)
* [Set a new ACL for a bucket](bucket_wrapper.py)
(`PutBucketAcl`)
* [Set the ACL of an object](object_wrapper.py)
(`PutObjectAcl`)
* [Upload an object to a bucket](object_wrapper.py)
(`PutObject`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Get started with buckets and objects](scenario_getting_started.py)
* [Create a presigned URL](presigned_url.py)

## Run the examples

### Prerequisites

To find prerequisites for running these examples, see the 
[README](../../../README.md#Prerequisites) in the Python folder.

#### Get started with buckets and objects

Interactively shows how to create a bucket and upload and download objects. To start, 
run the following at a command prompt.

```
python scenario_getting_started.py
```   

**Create a presigned URL**

This example requires the following additional package:

- Requests 2.24.0 or later

Generates a presigned URL and uses the Requests package to get or 
put a file into an S3 bucket. For example, run the following command to get
a file from Amazon S3 at a command prompt.

```
python presigned_url.py your-bucket-name your-object-key get
``` 

Run the script with the `-h` flag to get more help.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../../README.md#Tests) 
in the Python folder.

## Additional resources

* [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
* [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
* [Boto3 Amazon S3 service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
