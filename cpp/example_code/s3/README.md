# Amazon S3 C++ SDK code examples using S3Client

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Simple Storage Service 
(Amazon S3) using the AWS SDK for C++.

Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance. 

## Code examples
This is a workspace where you can find AWS SDK for C++ S3 examples utilizing the S3Client.

- [Copy an object from on Amazon S3 bucket to another](./copy_object.cpp) (CopyObject)
- [Create an Amazon S3](./create_bucket.cpp) (CreateBucket)
- [Delete an Amazon S3](./delete_bucket.cpp) (DeleteBucket)
- [Delete an Amazon S3 policy ](./delete_bucket_policy.cpp) (DeleteBucketPolicy)
- [Delete an object from an Amazon S3](./delete_object.cpp) (DeleteObject)
- [Delete the website configuration of an Amazon S3](./delete_website_config.cpp) (DeleteBucketWebsite)
- [Get the access control list (ACL) for an Amazon S3](./get_acl.cpp) (GetBucketAcl)
- [Get a bucket policy for an Amazon S3](./get_bucket_policy.cpp) (GetBucketPolicy)
- [Get an object out of an Amazon S3](./get_object.cpp) (GetObject)
- [Get and set the access control list (ACL) for an Amazon S3](./get_put_bucket_acl.cpp) (GetBucketAcl, PutBucketAcl)
- [Get and set the access control list (ACL) for an object in an Amazon S3](./get_put_object_acl.cpp) (GetObjectAcl, PutObjectAcl)
- [Get configuration of an Amazon S3 configured for static website hosting](./get_website_config.cpp) (GetBucketWebsite)
- [List all your Amazon S3 buckets](./list_buckets.cpp) (GetBuckets)
- [List all objects in an Amazon S3](./list_objects.cpp) (ListObjects)
- [Make requests to Amazon S3 across AWS Regions by specifying aws-global as the AWS Region](./list_objects_with_aws_global_region.cpp)
- [Add a bucket policy (permission to access resources) to a bucket](./put_bucket_policy.cpp) (PutBucketPolicy)
- [Upload an object to an Amazon S3 bucket](./put_object.cpp) (PutObject)
- [Upload an object to an Amazon S3 bucket (asynchronously)](./put_object_async.cpp) (PutObjectAsync)
- [Upload an object to an Amazon S3 bucket (using a memory buffer instead of local disk copy)](./put_object_buffer.cpp) (PutObject)
- [Configure an Amazon S3 bucket for static website hosting](./put_website_config.cpp) (PutBucketWebsite)
- [Find, create, and delete an Amazon S3 bucket in a sequence](./s3-demo.cpp)

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

To run these code examples, your AWS user must have permissions to perform these actions with Amazon S3.  
The AWS managed policy named "AmazonS3FullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [Amazon Simple Storage Service Documentation](https://docs.aws.amazon.com/s3/)
