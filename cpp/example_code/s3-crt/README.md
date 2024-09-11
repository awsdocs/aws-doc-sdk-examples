# Amazon S3 C++ SDK code examples using S3CrtClient

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Simple Storage Service
(Amazon S3) using the AWS SDK for C++.

Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance.

This example uses the S3CrtClient for better upload/download performance over the S3Client.  For other classes provided by the AWS SDK for C++ that also interface to
Amazon S3, see example folder [s3](../s3) and example folder [transfer-manager](../transfer-manager).

## Code examples
This is a workspace where you can find AWS SDK for C++ S3 examples utilizing the S3CrtClient.

- [Multipart upload and download of data with Amazon S3](./s3-crt-demo.cpp) The example creates a bucket, uploads a large data object using multipart upload of parallel requests, downloads the object via multiple "ranged" GET requests, then deletes the object and bucket.

## ⚠ Important
- We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples
Before using the code examples, first complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application.

Next, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

If you want to demonstrate multipart upload, you will need a large enough file.  You can download census data, "ny.json", from
https://nara-1940-census.s3.us-east-2.amazonaws.com/metadata/json/ny.json.

See [Amazon S3 code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/examples-s3.html) in the AWS SDK for C++ Developer Guide for additional information on using the Amazon S3 service with the SDK.

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)

