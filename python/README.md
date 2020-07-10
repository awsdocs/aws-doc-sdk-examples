# AWS SDK for Python (Boto3) examples

Code examples that show how to use Boto3 to access Amazon Web Services (AWS).

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured as described in the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)

## Documentation

For Boto3 and AWS documentation, see the following:

- [AWS SDK for Python (Boto3) Documentation](https://boto3.amazonaws.com/v1/documentation/api/latest/index.html)
- [AWS Documentation](https://docs.aws.amazon.com/)

## Examples

### [Amazon DynamoDB getting started examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/dynamodb/GettingStarted/README.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) to create an Amazon DynamoDB 
table for storing movies, load movies into the table from a JSON-formatted file, 
and update and query movies in various ways.

### [Amazon DynamoDB Accelerator (DAX) examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/dynamodb/TryDax/README.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) and the Amazon DAX Client for  
Python to read items from an Amazon DynamoDB table. Retrieval, query, and scan speeds
are measured for both Boto3 and DAX clients to show some of the performance 
advantages of using DAX.

DAX is a DynamoDB-compatible caching service that provides fast in-memory performance 
and high availability for applications that demand microsecond latency. For more
information, see [In-Memory Acceleration with DynamoDB Accelerator (DAX)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DAX.html). 

### [Amazon EC2 getting started examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/ec2/ec2_basics/README.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage Amazon Compute Cloud 
(Amazon EC2) resources. Learn to accomplish the following tasks:

* Create security keys, groups, and instances.
* Start and stop instances, use Elastic IP addresses, and update security 
groups.
* Clean up security keys and security groups.
* Permanently terminate instances. 

### [AWS IAM getting started examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/iam/iam_basics/README.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage AWS Identity and Access 
Management (IAM) resources. Learn to accomplish the following tasks:

* Create and manage IAM user access keys.
* Manage the alias of an account.
* Acquire reports about account usage.
* Create and manage IAM policies, including versioned policies.
* Create and manage IAM roles, including how to attach and detach policies.
* Create and manage IAM users, including how to attach a policy to a user.

### [AWS Lambda examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/lambda/README.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) to create, deploy, and invoke 
AWS Lambda functions. Learn to accomplish the following tasks:

* Create and deploy AWS Lambda functions that can be invoked in different ways:
    * By an invoke call through Boto3
    * By Amazon API Gateway as the target of a REST request
    * By Amazon EventBridge on a schedule
* Create and deploy a REST API on Amazon API Gateway. The REST API targets an 
AWS Lambda function to handle REST requests.
* Create a schedule rule on Amazon EventBridge that targets an AWS Lambda function.

These examples show how to use the low-level Boto3 client APIs to accomplish tasks
like creating a REST API and setting an event schedule. You can also use
[AWS Chalice](https://github.com/aws/chalice)
to achieve similar results more easily and with additional features. 

### [Amazon S3 managed file transfer example](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/s3/file_transfer/README.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) transfer manager to manage multipart
uploads to and downloads from an Amazon Simple Storage Service (Amazon S3) bucket.

When the file to transfer is larger than the specified threshold, the transfer
manager automatically uses multipart uploads or downloads. This example
shows how to use several of the available transfer manager settings, and reports
thread usage and time to transfer.

### [Amazon S3 getting started examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/s3/s3_basics/ReadMe.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) to  get started using bucket and 
object operations in Amazon Simple Storage Service (Amazon S3). 
Learn to create, get, remove, and configure buckets and objects.

### [Amazon S3 batch and versioning examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/s3/s3_versioning/README.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) to set up an Amazon S3 bucket for 
versioning, and how to perform taks on a version-enabled bucket. Learn to 
accomplish the following tasks:

* Create a version-enabled bucket and apply revisions to its objects.
* Get a full series of object versions.
* Roll back to a previous version.
* Revive a deleted object.
* Permanently delete all versions of an object.

Shows how to manipulate Amazon S3 versioned objects in batches by creating jobs
that call AWS Lambda functions to perform processing. Learn to accomplish the
following tasks:

* Create Lambda functions that operate on versioned objects.
* Create a manifest of objects to update.
* Create batch jobs that invoke Lambda functions to update objects.
* Delete Lambda functions.
* Empty and delete a versioned bucket.

### [Amazon SQS getting started examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/sqs/ReadMe.md)

#### Purpose

Shows how to use the AWS SDK for Python (Boto3) to get started using queue and 
message operations in Amazon Simple Queue Service (Amazon SQS). Learn how to 
create, get, and remove standard, FIFO, and dead-letter queues. Learn how to 
send, receive, and delete messages from a queue.

### [AWS STS temporary credential examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/sts/sts_temporary_credentials/README.md)

#### Purpose

Shows how to use the AWS Python SDK (Boto3) to access the AWS Security Token 
Service (AWS STS) to acquire temporary credentials that grant specific permissions. Also
demonstrates how to set up and use a multi-factor authentication (MFA) device. Learn
to accomplish the following tasks:

* Assume a role that grants specific permissions, and use those credentials to 
perform permitted actions.
* Add a new MFA device to a user. 
* Assume a role that requires MFA to be present.
* Construct a URL that gives federated users direct access to an account through the
AWS Management Console.
* Get a session token that can be used to call an API function that requires MFA.

### [Test tools for Python code examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/test_tools/README.md)

#### Purpose

Centralizes PyTest fixtures and specialized stubbers based on the botocore Stubber 
that Python code examples can use for unit tests.

## Additional information

- As an AWS best practice, grant this code least privilege, or only the permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see the [AWS Regional Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
- Running this code might result in charges to your AWS account.

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
