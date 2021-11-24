# Amazon S3 Kotlin code examples

This README discusses how to run the Kotlin code examples for the Amazon Simple Storage Service (Amazon S3).

## Running the Amazon S3 Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon S3 bucket. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CopyObject** - Demonstrates how to copy an object from one Amazon S3 bucket to another.
- **CreateBucket** - Demonstrates how to create an Amazon S3 bucket.
- **DeleteBucket** - Demonstrates how to delete an Amazon S3 bucket.
- **DeleteBucketPolicy** - Demonstrates how to delete a policy from an Amazon S3 bucket.
- **DeleteObjects** - Demonstrates how to delete an object from an Amazon S3 bucket.
- **GetAcl** - Demonstrates how to get the access control list (ACL) of an object located in an Amazon S3 bucket.
- **SetBucketPolicy** - Demonstrates how to add a bucket policy to an existing Amazon S3 bucket.
- **GetObjectData** - Demonstrates how to read data from an Amazon S3 object.
- **ListObjects** - Demonstrates how to list objects located in a given Amazon S3 bucket.
- **PutObject** - Demonstrates how to upload an object to an Amazon S3 bucket.
- **SetAcl** - Demonstrates how to set a new ACL for an Amazon S3 bucket.
- **SetBucketPolicy** - Demonstrates how to add a bucket policy to an existing Amazon S3 bucket.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
