# Amazon S3 getting started examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to  get started using bucket and 
object operations in Amazon Simple Storage Service (Amazon S3). 
Learn to create, get, remove, and configure buckets and objects.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto 3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)

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

Both `bucket_wrapper.py` and `object_wrapper.py` contain `usage_demo` functions
that demonstrate ways to use the functions in their respective modules. 
For example, to see the bucket demonstration, run the module in a command window.

```
python -m bucket_wrapper
``` 

You can also run individual functions in the Python shell to make requests to your 
AWS account. For example, run the following commands to create a bucket, upload 
an object, get the object, empty the bucket, and delete the bucket.  

    > python
    >>> import time
    >>> bucket_name = f"bucket{time.time_ns()}"
    >>> import bucket_wrapper
    >>> bucket = bucket_wrapper.create_bucket(bucket_name, region='us-west-2')
    >>> import object_wrapper
    >>> object_wrapper.put_object(bucket, 'my-test-object', b'My test data')
    >>> obj = object_wrapper.get_object(bucket, 'my-test-object')
    >>> object_wrapper.empty_bucket(bucket)
    >>> bucket_wrapper.delete_bucket(bucket)

## Running the tests

The best way to learn how to use this service is to run the tests.
You can run tests in two modes. By default, tests use the botocore Stubber,
which captures requests before they are sent to AWS and returns a mocked response.
You can also run tests against your AWS account. In this case, they will create and 
manipulate AWS resources, which might incur charges on your account.

To run all of the S3 tests with the botocore Stubber, run the following in
your [GitHub root]/python/example_code/s3 folder.

    python -m pytest -o log_cli=1 --log-cli-level=INFO test

The '-o log_cli=1 --log-cli-level=INFO' flags configure pytest to output
logs to stdout during the test run. Without them, pytest captures logs and prints
them only when the test fails.

To run the tests using your AWS account and default shared credentials, include the
'--use-real-aws-may-incur-charges' flag.

    python -m pytest -o log_cli=1 --log-cli-level=INFO --use-real-aws-may-incur-charges test

When tests are run in this mode, a best effort is made to clean up any resources 
created during the test. But it's your responsibility to verify that all resources 
have actually been cleaned up.

## Additional information

- [Boto 3 Amazon S3 examples](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/s3-examples.html)
- [Boto 3 Amazon S3 service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
