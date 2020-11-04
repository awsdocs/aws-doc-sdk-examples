# AWS SDK for Go V2 code examples for Amazon S3

## Purpose

These examples demonstrates how to perform several Amazon Simple Storage Service 
(Amazon S3) operations using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CopyObject/CopyObject.go

This example copies an Amazon S3 object from one bucket to another.

`go run CopyObject.go -f SOURCE -t DESTINATION -i ITEM`

- _SOURCE_ is the name of the bucket containing the item to copy.
- _DESTINATION_ is the name of the bucket to which the item is copied.
- _ITEM_ is the name of the item to copy.

The unit test accepts similar values in _config.json_.

###  CreateBucket/CreateBucket.go

This example creates an Amazon S3 bucket.

`go run CreateBucket.go -b BUCKET`

- _BUCKET_ is the name of the bucket to create.

The unit test accepts a similar value in _config.json_.

### DeleteBucket/DeleteBucket.go

This example deletes an Amazon S3 bucket.

`go run DeleteBucket.go -b BUCKET`

- _BUCKET_ is the name of the bucket to delete.

The unit test accepts a similar value in _config.json_.

### DeleteObject/DeleteObject.go

This example deletes an Amazon S3 bucket item.

`go run DeleteObject.go -b BUCKET -i ITEM`

- _BUCKET_ is the name of the bucket containing the item to delete.
- _ITEM_ is the name of the item to delete.

The unit test accepts similar values in _config.json_.

### GetBucketAcl/GetBucketAcl.go

This example retrieves the access control list (ACL) for an Amazon S3 bucket.

`go run GetBucketAcl.go -b BUCKET`

- _BUCKET_ is the name of the bucket for which the ACL is retrieved.

The unit test accepts a similar value in _config.json_.

### GetObjectAcl/GetObjectAcl.go

This example retrieves the access control list (ACL) for an Amazon S3 bucket item.

`go run GetObjectAcl.go -b BUCKET -i ITEM`

- _BUCKET_ is the name of the bucket containing the item.
- _ITEM_ is the name of the item for which the ACL is retrieved.

The unit test accepts similar values in _config.json_.

### ListBuckets/ListBuckets.go

This example lists your Amazon S3 buckets.

`go run ListBuckets.go`

### ListObjects/ListObjects.go

This example lists the items in an Amazon S3 bucket.

`go run ListObjects.go -b BUCKET`

- _BUCKET_ is the name of the bucket for which the items are listed.

The unit test accepts a similar value in _config.json_.

### PutObject/PutObject.go

This example creates an Amazon S3 bucket item from a local file.

`go run PutObject.go -b BUCKET -f FILE`

- _BUCKET_ is the name of the bucket to which the file is uploaded.
- _FILE_ is the name of the local file to upload.

The unit test accepts similar values in _config.json_.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the unit tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run a unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
