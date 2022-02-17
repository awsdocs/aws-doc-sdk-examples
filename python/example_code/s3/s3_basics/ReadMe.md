# Amazon S3 bucket and object examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to get started using bucket and 
object operations in Amazon Simple Storage Service (Amazon S3). 
Learn to create, get, remove, and configure buckets and objects.

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any 
amount of data at any time, from anywhere on the web.*

## Code examples

### Scenario examples

* [Getting started with buckets and objects](scenario_getting_started.py)
* [Create a presigned URL](presigned_url.py)

### API examples

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
- Python 3.7 or later
- Boto3 1.11.10 or later
- Requests 2.24.0 or later
- PyTest 5.3.5 or later (to run unit tests)

### Command

**Getting started with buckets and objects**

Interactively shows how to create a bucket and upload and download objects. To start, 
run the following at a command prompt.

```
python scenario_getting_started.py
```   

**Create a presigned URL**

Generates a presigned URL and uses the Requests package to get or 
put a file in an Amazon S3 bucket. For example, run the following command to get
a file from Amazon S3 at a command prompt.

```
python presigned_url.py your-bucket-name your-object-key get
``` 

Run the script with the `-h` flag to get more help.

**bucket_wrapper.py** and **object_wrapper.py**

These scripts contain `usage_demo` functions that demonstrate ways to use the 
functions in their respective modules. For example, to see the bucket demonstration, 
run the module in a command window.

```
python bucket_wrapper.py
``` 

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/s3/s3_basics 
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
