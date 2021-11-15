# Amazon S3 C++ SDK code examples using S3Client

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Simple Storage Service 
(Amazon S3) using the AWS SDK for C++.

Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance. 

This example uses the S3Client, which is a fully-featured S3 interface and is ideally suited for smaller files.  For other classes provided by the AWS SDK for C++ that also interface to
Amazon S3, see example folder [s3-crt](../s3-crt) and example folder [transfer-manager](../transfer-manager).

## Code examples
This is a workspace where you can find AWS SDK for C++ S3 examples utilizing the S3Client.

- [Copying an object](./copy_object.cpp) (CopyObject)
- [Creating an Amazon S3 bucket](./create_bucket.cpp) (CreateBucket)
- [Deleting an Amazon S3 bucket](./delete_bucket.cpp) (DeleteBucket)
- [Deleting a bucket policy (permission to access resources) of an Amazon S3 bucket](./delete_bucket_policy.cpp) (DeleteBucketPolicy)
- [Deleting an object from an Amazon S3 bucket](./delete_object.cpp) (DeleteObject)
- [Deleting the website configuration of an Amazon S3 bucket](./delete_website_config.cpp) (DeleteBucketWebsite)
- [Getting the access control list (ACL) for an Amazon S3 bucket](./get_acl.cpp) (GetBucketAcl)
- [Getting a bucket policy (permission to access resources) for an Amazon S3 bucket](./get_bucket_policy.cpp) (GetBucketPolicy)
- [Getting an object out of an Amazon S3 bucket](./get_object.cpp) (GetObject)
- [Getting and setting the access control list (ACL) for an Amazon S3 bucket](./get_put_bucket_acl.cpp) (GetBucketAcl, PutBucketAcl)
- [Getting and setting the access control list (ACL) for an object in an Amazon S3 bucket](./get_put_object_acl.cpp) (GetObjectAcl, PutObjectAcl)
- [Getting configuration of an Amazon S3 bucket configured for static website hosting](./get_website_config.cpp) (GetBucketWebsite)
- [Listing all Amazon S3 buckets](./list_buckets.cpp) (GetBuckets)
- [Listing all objects in an Amazon S3 bucket](./list_objects.cpp) (ListObjects)
- [Making requests to  Amazon S3 across AWS Regions by specifying aws-global as the AWS Region](./list_objects_with_aws_global_region.cpp)
- [Adding a bucket policy (permission to access resources) to an Amazon S3 bucket](./put_bucket_policy.cpp) (PutBucketPolicy)
- [Uploading an object to an Amazon S3 bucket](./put_object.cpp) (PutObject)
- [Uploading an object to an Amazon S3 bucket (asynchronously)](./put_object_async.cpp) (PutObjectAsync)
- [Uploading an object to an Amazon S3 bucket (using a memory buffer instead of local disk copy)](./put_object_buffer.cpp) (PutObject)
- [Configuring an Amazon S3 bucket for static website hosting](./put_website_config.cpp) (PutBucketWebsite)
- [Finding, creating, and deleting an Amazon S3 bucket in a sequence](./s3-demo.cpp)

## ⚠ Important
- We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples
Before using the code examples, first complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample “Hello World”-style application. 

Next, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

## Additional Information
See [Amazon S3 code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/examples-s3.html) in the AWS SDK for C++ Developer Guide for additional information on using the Amazon S3 service with the SDK.

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 

