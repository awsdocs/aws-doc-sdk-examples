# Amazon S3 examples using SDK for Rust

# Overview

These examples demonstrate how to perform several Amazon Simple Storage Service
(Amazon S3) operations using the developer preview version of the AWS SDK for Rust.

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any
amount of data at any time, from anywhere on the web.*

## ⚠ Important

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Code examples

### Single actions

- [Create basic client](src/bin/client.rs) (ListBuckets)
- [Copies an object from one bucket to another](src/bin/copy-object.rs) (CopyObject)
- [Create a bucket](src/bin/create-bucket.rs) (CreateBucket)
- [Delete an object from a bucket](src/bin/delete-object.rs) (DeleteObject)
- [Deletes one or more objects from a bucket](src/bin/delete-objects.rs) (DeleteObjects)
- [Delete an empty bucket](src/s3-service-lib.rs) (DeleteBucket)
- [Gets a presigned URI for an object](src/bin/get-object-presigned.rs) (GetObject)
- [Lists your buckets](src/bin/list-buckets.rs) (ListBuckets)
- [Lists the objects in a bucket](src/bin/list-objects.rs) (ListObjectsV2)
- [Lists the versions of the objects in a bucket](src/bin/list-object-versions.rs) (ListObjectVersions)
- [Adds an object to a bucket and returns a public URI to the object.](src/bin/put-object-presigned.rs) (PutObject)
- [Lists your buckets and uploads a file to a bucket](src/bin/s3-helloworld.rs) (ListBuckets, PutObject)
- [Lists your buckets at a specified endpoint](src/bin/s3-object-lambda.rs) (ListBuckets)
- [Uses an SQL expression to retrieve content from an object in a bucket](src/bin/select-object-content.rs) (SelectObjectContent)
- [Create multipart upload](src/bin/s3-multipart-upload.rs)(CreateMultipartUpload)
- [Upload part](src/bin/s3-multipart-upload.rs)(UploadPart)
- [Complete multipart upload](src/bin/s3-multipart-upload.rs)(CompleteMultipartUpload)

### Scenarios

* [Get started with buckets and objects](src/bin/s3-getting-started.rs)
* [Upload and download large files](src/bin/s3-multipart-upload.rs)

### Run the examples

To run these examples, use:
```
cargo run --bin name-of-binary
```
where `name-of-binary` is the name of the file you want to run (e.g. s3-getting-started).

### Prerequisites

- You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).
- Install Rust and Cargo as described [in the Rust documentation](https://doc.rust-lang.org/book/ch01-01-installation.html)

## Tests

⚠️ Running the tests might result in charges to your AWS account.

Run the tests with the following command:

```
cargo test -p s3_code_examples -- --include-ignored --nocapture
```

## Additional resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon S3](https://docs.rs/aws-sdk-s3)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg) 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0