# Amazon S3 C++ SDK code examples using S3CrtClient

## Purpose
The code examples in this directory demonstrate how to use the Amazon Simple Storage Service 
(Amazon S3) S3CrtClient using the AWS SDK for C++.

The S3CrtClient class is available in version 1.9 of the AWS SDK for C++ and, compared to the S3Client, improves the throughput of uploading and downloading large data files to and from Amazon S3.

Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance. 

For other classes provided by the AWS SDK for C++ that also interface to
Amazon S3, see example folder [s3](../s3) and example folder [transfer-manager](../transfer-manager).

## Code examples

### Usage Scenarios
- [Manage your Amazon S3 operations using the S3CrtClient](./s3-crt-demo.cpp) The example creates a bucket, uploads a large data object using multipart upload of parallel requests, downloads the object via multiple "ranged" GET requests, then deletes the object and bucket.

## ⚠ Important
- We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- Complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application. 
- See [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

If you want to demonstrate multipart upload, you will need a large enough file.  You can download census data, "ny.json", from
https://nara-1940-census.s3.us-east-2.amazonaws.com/metadata/json/ny.json.

To run these code examples, your AWS user must have permissions to perform these actions with Amazon S3.  
The AWS managed policy named "AmazonS3FullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [Using S3CrtClient for Amazon S3 operations](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/examples-s3-crt.html)
- [Amazon Simple Storage Service Documentation](https://docs.aws.amazon.com/s3/)

