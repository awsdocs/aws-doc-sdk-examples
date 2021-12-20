# AWS SDK for Rust code examples for Amazon S3

## Purpose

These examples demonstrate how to perform several Amazon Simple Storage Service (Amazon S3) operations using the developer preview version of the AWS SDK for Rust.

Use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.

## Code examples


- [Create basic client](src/bin/client.rs) (ListBuckets)
- [Create a bucket](src/bin/create-bucket.rs) (CreateBucket)
- [Delete an object from a bucket](src/bin/delete-object.rs) (DeleteObject)
- [Deletes one or more objects from a bucket](src/bin/delete-objects.rs) (DeleteObjects)
- [Gets a presigned URI for an object](src/bin/get-object-presigned.rs) (GetObject)
- [Lists your buckets](src/bin/list-buckets.rs) (ListBuckets)
- [Lists the versions of the objects in a bucket](src/bin/list-object-versions.rs) (ListObjectVersions)
- [Lists the objects in a bucket](src/bin/list-objects.rs) (ListObjectsV2)
- [Lists the versions of the objects in a bucket](src/bin/list-object-versions.rs) (ListObjectVersions)
- [Adds an object to a bucket and returns a public URI to the object.](src/bin/put-object-presigned.rs) (PutObject)
- [Lists the objects in a bucket and uploads a file to the bucket](src/bin/s3_helloworld.rs) (ListObjects, PutObject)
- [Lists your buckets at a specified endpoint](src/bin/s3-object-lambda.rs) (ListBuckets)
- [Uses an SQL expression to retrieve content from an object in a bucket](src/bin/select-object-content.rs) (SelectObjectContent)


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

## Running the code examples

### Prerequisites

- We recommend that you grant this code least privilege, 
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the code examples

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).
=======
### client

This example creates a basic client and lists your Amazon S3 buckets.

`cargo run --bin client -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

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

### get-object-presigned

This example creates a public URI to an object in an Amazon S3 bucket.

`cargo run --bin get-object-presigned -- -b BUCKET -o OBJECT [-e EXPIRES-IN] [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _OBJECT_ is the name of the object.
- _EXPIRES-IN_ is the duration, in seconds, that the URI is valid. The default is 900 (15 minutes).
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

### put-object-presigned

This example uploads a file to an Amazon S3 bucket, creates a public URI to the object, and displays the URI.

`cargo run --bin put-object-presigned -- -b BUCKET -o OBJECT [-e EXPIRES-IN] [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _OBJECT_ is the name of the file to upload to the bucket.
- _EXPIRES-IN_ is the duration, in seconds, that the URI is valid. The default is 900 (15 minutes).
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### s3-helloworld

This example lists your buckets and uploads a file to a bucket.

`cargo run --bin s3-helloworld -- -b BUCKET -f FILENAME -k KEY [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _FILENAME_ is the name of the file to upload.
- _KEY_ is the name of the file to upload to the bucket.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.


### select-object-content.rs

This example uses an SQL query to retrive information from an object, in CSV format, in an Amazon S3 bucket.

`cargo run --bin select-object-content -- -b BUCKET -o OBJECT -n NAME [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _OBJECT_ is the name of the object to query.
- _NAME_ is the name of the person to retrieve infomation about.
=======
### s3-object-lambda

This example lists your buckets in a specified endpoint.

`cargo run --bin s3-object-lambda -- -a ACCOUNT -e ENDPOINT [-r REGION] [-v]`

- _ACCOUNT_ is the your account number.
- _ENDPOINT_ is the endpoint.
- _KEY_ is the name of the file to upload to the bucket.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

The object must have the following format.

```
Name,PhoneNumber,City,Occupation
Person1,(nnn) nnn-nnnn,City1,Occupation1
...
PersonN,(nnn) nnn-nnnn,CityN,OccupationN
```

=======
## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon S3](https://docs.rs/aws-sdk-s3)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg) 
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

=======
## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0