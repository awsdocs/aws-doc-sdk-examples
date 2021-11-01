# AWS SDK for Rust code examples for Amazon S3

## Purpose

These examples demonstrate how to perform several Amazon Simple Storage Service (S3) operations using the alpha version of the AWS SDK for Rust.

Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.

## Code examples

- [Create a bucket](src/bin/create-bucket.rs) (CreateBucket)
- [Delete an object from a bucket](src/bin/delete-object.rs) (DeleteObject)
- [Deletes one or more objects from a bucket](src/bin/delete-objects.rs) (DeleteObjects)
- [Lists your buckets](src/bin/list-buckets.rs) (ListBuckets)
- [Lists the objects in a bucket](src/bin/list-objects.rs) (ListObjectsV2)
- [Lists the versions of the objects in a bucket](src/bin/list-object-versions.rs) (ListObjectVersions)
- [](src/bin/s3-helloworld.rs) ()

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).



### create-bucket

This example creates an Amazon S3 bucket.

`cargo run --bin create-bucket -- -b BUCKET [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket to create.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### delete-object

This example deletes an object from an Amazon S3 bucket.

`cargo run --bin delete-object -- -b BUCKET -k KEY [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _KEY_ is the name of the object.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### delete-objects

This example deletes one or more objects from an Amazon S3 bucket.

`cargo run --bin delete-objects -- -b BUCKET -o OBJECTS [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _OBJECTS_ are the names of the objects to delete, separated by spaces.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-buckets

This example lists your Amazon S3 buckets.

`cargo run --bin list-buckets -- [-s] [-r REGION] [-v]`

- __-s__ display only buckets in the Region.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-objects

This example lists the objects in an Amazon S3 bucket.

`cargo run --bin list-objects -- -b BUCKET [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-object-versions

This example lists the versions of the objects in an Amazon S3 bucket.

`cargo run --bin list-objects -- -b BUCKET [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### s3-helloworld

This example uploads a file to a bucket.

`cargo run --bin hello-world -- -b BUCKET -f FILENAME -k KEY [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _FILENAME_ is the name of the file to upload.
- _KEY_ is the name of the file to upload to the bucket.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
