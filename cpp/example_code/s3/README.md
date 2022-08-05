#  Amazon S3 code examples for the SDK for C++ using S3Client

## Overview
The code examples in this directory demonstrate how to work with the Amazon Simple Storage Service (Amazon S3) using the AWS SDK for C++.

Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance. 

This example uses the S3Client, which is a fully-featured Amazon S3 interface and is ideally suited for smaller files.  For other classes provided by the AWS SDK for C++ that also interface to Amazon S3, see example folder [s3-crt](../s3-crt) and example folder [transfer-manager](../transfer-manager).

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).


## Code examples

### Single actions
- [Copying an object](./copy_object.cpp) (CopyObject)
- [Creating an S3 bucket](./create_bucket.cpp) (CreateBucket)
- [Deleting an S3 bucket](./delete_bucket.cpp) (DeleteBucket)
- [Deleting a bucket policy (permission to access resources) of an S3 bucket](./delete_bucket_policy.cpp) (DeleteBucketPolicy)
- [Deleting an object from an S3 bucket](./delete_object.cpp) (DeleteObject)
- [Deleting the website configuration of an S3 bucket](./delete_website_config.cpp) (DeleteBucketWebsite)
- [Getting the access control list (ACL) for an S3 bucket](./get_acl.cpp) (GetBucketAcl)
- [Getting a bucket policy (permission to access resources) for an S3 bucket](./get_bucket_policy.cpp) (GetBucketPolicy)
- [Getting an object out of an S3 bucket](./get_object.cpp) (GetObject)
- [Getting and setting the access control list (ACL) for an S3 bucket](./get_put_bucket_acl.cpp) (GetBucketAcl, PutBucketAcl)
- [Getting and setting the access control list (ACL) for an object in an S3 bucket](./get_put_object_acl.cpp) (GetObjectAcl, PutObjectAcl)
- [Getting configuration of an S3 bucket configured for static website hosting](./get_website_config.cpp) (GetBucketWebsite)
- [Listing all S3 buckets](./list_buckets.cpp) (GetBuckets)
- [Listing all objects in an S3 bucket](./list_objects.cpp) (ListObjects)
- [Making requests to S3 across AWS Regions by specifying aws-global as the AWS Region](./list_objects_with_aws_global_region.cpp)
- [Adding a bucket policy (permission to access resources) to an S3 bucket](./put_bucket_policy.cpp) (PutBucketPolicy)
- [Uploading an object to an S3 bucket](./put_object.cpp) (PutObject)
- [Uploading an object to an S3 bucket (asynchronously)](./put_object_async.cpp) (PutObjectAsync)
- [Uploading an object to an S3 bucket (using a memory buffer instead of local disk copy)](./put_object_buffer.cpp) (PutObject)
- [Configuring an S3 bucket for static website hosting](./put_website_config.cpp) (PutBucketWebsite)

### Scenarios
- [Creating, listing, and deleting S3 buckets](./s3_getting_started_scenario.cpp)
- [Finding, creating, and deleting an S3 bucket in a sequence](./s3-demo.cpp)

## Running the examples
Before using the code examples, first complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to get and build the SDK, and how to build your own code by using the SDK with a sample “Hello World”-style application. 

Next, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
   ./gtests/s3_gtest 
```   

## Additional resources
- [Amazon Simple Storage Service Documentation](https://docs.aws.amazon.com/s3/index.html)
- [Amazon S3 code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/examples-s3.html)
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
