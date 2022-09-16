# Amazon S3 code examples for the SDK for Rust

# Overview

These examples demonstrate how to perform several Amazon Simple Storage Service
(Amazon S3) operations using the developer preview version of the AWS SDK for Rust.

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any
amount of data at any time, from anywhere on the web.*

## ⚠ Important

- Running this code might result in charges to your AWS account. 
- Running the tests might result in charges to your AWS account. 
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see Grant least privilege. 
- This code is not tested in every AWS Region. For more information, see AWS Regional Services.

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Create basic client](src/bin/client.rs) (ListBuckets)
- [Copy an object from one bucket to another](src/bin/copy-object.rs) (CopyObject)
- [Create a bucket](src/bin/create-bucket.rs) (CreateBucket)
- [Delete an object from a bucket](src/bin/delete-object.rs) (DeleteObject)
- [Delete one or more objects from a bucket](src/bin/delete-objects.rs) (DeleteObjects)
- [Delete an empty bucket](src/s3-service-lib.rs) (DeleteBucket)
- [Get a presigned URI for an object](src/bin/get-object-presigned.rs) (GetObject)
- [List your buckets](src/bin/list-buckets.rs) (ListBuckets)
- [List the objects in a bucket](src/bin/list-objects.rs) (ListObjectsV2)
- [List the versions of the objects in a bucket](src/bin/list-object-versions.rs) (ListObjectVersions)
- [Add an object to a bucket and returns a public URI to the object.](src/bin/put-object-presigned.rs) (PutObject)
- [List your buckets and uploads a file to a bucket](src/bin/s3-helloworld.rs) (ListBuckets, PutObject)
- [List your buckets at a specified endpoint](src/bin/s3-object-lambda.rs) (ListBuckets)
- [Use an SQL expression to retrieve content from an object in a bucket](src/bin/select-object-content.rs) (SelectObjectContent)
- [Create multipart upload](src/bin/s3-multipart-upload.rs)(CreateMultipartUpload)
- [Upload part](src/bin/s3-multipart-upload.rs)(UploadPart)
- [Complete multipart upload](src/bin/s3-multipart-upload.rs)(CompleteMultipartUpload)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Get started with buckets and objects](src/bin/s3-getting-started.rs)
* [Upload and download large files](src/bin/s3-multipart-upload.rs)

### Run the examples

To run these examples, use the following command:
```
cargo run --bin name-of-binary
```
where `name-of-binary` is the name of the file you want to run (such as s3-getting-started).

### Prerequisites

- You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).
- Install Rust and Cargo as described [in the Rust documentation](https://doc.rust-lang.org/book/ch01-01-installation.html).

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