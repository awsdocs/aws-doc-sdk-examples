# AWS SDK for Go Code Examples for Amazon S3

## Purpose

These examples demonstrates how to perform several Amazon S3 operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CopyObject/CopyObject.go

This example copies an item from one Amazon S3 bucket to another.

`go run CopyObject.go -f FROM-BUCKET -t TO-BUCKET -i ITEM`

- *FROM-BUCKET* is the name of the bucket containing the item to copy.
- *TO-BUCKET* is the name of the bucket to which the item is copied.
- *ITEM* is the item to copy.

The unit test accepts similar values from *config.json*.

### CreateBucket/CreateBucket.go

This example creates an Amazon S3 bucket.

`go run CreateBucket.go -b BUCKET`

- *BUCKET* is the name of the bucket to create.

The unit test accepts a similar value from *config.json*.

### CreateBucketAndObject/CreateBucketAndObject.go

This example creates an Amazon S3 bucket and a dummy object in that bucket.

`go run CreateBucketAndObject.go -b BUCKET [-k KEY]`

- *BUCKET* is the name of the bucket to create.
- *KEY* is the optional name of the dummy object.
  The default value is **TestFile.txt**.

The unit test accepts similar values from *config.json*.

### CRUD/S3CrudOps.go

This example creates, reads, updates, and deletes an Amazon S3 bucket.

`go run S3CrudOps.go -b BUCKET`

- *BUCKET* is the name of the bucket to create, read, update, and delete.

The unit test accepts a similar value from *config.json*.

### CustomClient/CustomHTTPClient.go

This example either creates a custom HTTP client and uses it to get an Amazon S3 bucket object,
or gets the S3 bucket object using a custom timeout of 20 seconds.

`go run CustomHTTPClient.go -b BUCKET -o OBJECT [-s] [-t]`

- *BUCKET* is the name of the bucket.
- *OBJECT* is the name of the object (required).
- **-s** shows the object, as a string (optional).
- **-t** gets the object using a custom timout; otherwise, it uses a custom HTTP client.

The unit test accepts similar values from *config.json*.

### DeleteBucket/DeleteBucket.go

This example deletes an Amazon S3 bucket.

`go run DeleteBucket.go -b BUCKET`

- *BUCKET* is the name of the bucket to delete.

The unit test accepts a similar value from *config.json*.

### DeleteBucketPolicy/DeleteBucketPolicy.go

This example removes the policy for an Amazon S3 bucket.

`go run DeleteBucketPolicy.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### DeleteBucketWebsite/DeleteBucketWebsite.go

This example removes the website configuration for an Amazon S3 bucket.

`go run DeleteBucketWebsite.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### DeleteObject/DeleteObject.go

This example deletes an item from an Amazon S3 bucket.

`go run DeleteObject -b BUCKET -i ITEM`

- *BUCKET* is the name of the bucket.
- *ITEM* is the name of the item.

The unit test accepts similar values from *config.json*.

### DeleteObjects/DeleteObjects.go

This example deletes all of the objects in an Amazon S3 bucket.

`go run DeleteObjects.go -b BUCKET`

- *BUCKET* is the name of the bucket to purge of objects.

The unit test accepts a similar value from *config.json*.

### DownloadObject/DownloadObject.go

This example downloads a file from an Amazon S3 bucket.

`go run DownloadObject.go -b BUCKET -f FILENAME`

- *BUCKET* is the name of the bucket.
- *FILENAME* is the name of the bucket item to download to a file.

The unit test accepts similar values from *config.json*.

### EncryptOnServerWithKms/EncryptOnServerWithKms.go

This example adds an object to an Amazon S3 bucket with encryption based on an AWS Key Management Service (AWS KMS) key.

`go run EncryptOnServerWithKms.go -b BUCKET -o OBJECT -k KEY`

- *BUCKET* is the name of the bucket.
- *OBJECT* is the name of the file to upload.
- *KEY* is the AWS KMS key.

The unit test accepts similar values from *config.json*.

### EnforceMD5/EnforceMD5Content.go

This example enforces an MD5 checksum on the object uploaded to an Amazon S3 bucket.

`go run EnforceMD5Content.go -b BUCKET -k KEY`

- *BUCKET* is the name of the bucket.
- *KEY* is the object uploaded to the bucket.

The unit test accepts similar values from *config.json*.

### GeneratePresignedURL/GeneratePresignedURL.go

This example creates a presigned URL for an Amazon S3 bucket object.

`go run GeneratePresignedURL.go -b BUCKET -k KEY`

- *BUCKET* is the name of the bucket.
- *KEY* is the bucket object.

The unit test accepts similar values from *config.json*.

### GeneratePresignedURLSpecificPayload/GeneratePresignedURLSpecificPayload.go

This example creates a presigned URL for an Amazon S3 bucket object with specific content.

`go run GeneratePresignedURLSpecificPayload.go -b BUCKET -k KEY -c CONTENT`

- *BUCKET* is the name of the bucket.
- *KEY* is the bucket object.
- *CONTENT* is the contents of the object.

The unit test accepts similar values from *config.json*.

### GetBucketAcl/GetBucketAcl.go

This example gets the ACL for an Amazon S3 bucket.

`go run GetBucketAcl.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### GetBucketPolicy/GetBucketPolicy.go

This example retrieves the policy for an Amazon S3 bucket.

`go run GetBucketPolicy.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### GetBucketWebsite/GetBucketWebsite.go

This example retrieves the Amazon S3 bucket's website configuration.

`go run GetBucketWebsite.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### GetObjectAcl/GetObjectAcl.go

This example gets the ACL for an Amazon S3 bucket object.

`go run GetObjectAcl.go -b BUCKET -k KEY`

- *BUCKET* is the name of the bucket.
- *KEY* is the bucket object to get the ACL from.

The unit test accepts similar values from *config.json*.

### ListBuckets/ListBuckets.go`

This example lists all Amazon S3 buckets.

`go run ListBuckets.go`

### ListObjects/ListObjects.go

This example lists all of the objects in an Amazon S3 bucket.

`go run ListObjects.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### MakeBucketPublic/MakeBucketPublic.go

This example gives everyone access to an Amazon S3 bucket.

`go run MakeBucketPublic.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts similar values from *config.json*.

### PutBucketAcl/PutBucketAcl.go

This example gives a user access to an Amazon S3 bucket.

`go run PutBucketAcl.go -b BUCKET -e ADDRESS -p PERMISSION

- *BUCKET* is the name of the bucket.
- *ADDRESS* is the email address of the user.
- *PERMISSION* is one of: **FULL_CONTROL**, **WRITE**, **WRITE_ACP**, **READ**, or **READ_ACP**.

The unit test accepts similar values from *config.json*.

### PutObjectAcl/PutObjectAcl.go

This example gives a person read access to an object in an Amazon S3 bucket.

`go run PutObjectAcl.go -b BUCKET -k KEY -a ADDRESS

- *BUCKET* is the name of the bucket.
- *KEY* is the name of the object.
- *ADDRESS* is the email address of the user.

The unit test accepts similar values from *config.json*.

### PutObjectWithSetters/PutObjectWithSetters.go

This example uploads a file to an Amazon S3 bucket using setters.

`go run PutObjectWithSetters.go -b BUCKET -k KEY`

- *BUCKET* is the name of the bucket.
- *KEY* is the name of the file to upload.

The unit test accepts similar values from *config.json*.

### RequireServerEncryption/RequireServerEncryption.go

This example adds a policy to enable AWS Key Management Service (AWS KMS) encryption by default on an Amazon S3 bucket.

`go run RequireServerEncryption.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### RestoreObject/RestoreObject.go

This example restores an item to an Amazon S3 bucket for a number of days.

`go run RestoreObject.go -b BUCKET -i ITEM [-d DAYS]`

- *BUCKET* is the name of the bucket.
- *ITEM* is the name of the item to restore.
- *DAYS* is the optional number of days to restore it from Amazon S3 Glacier"

The unit test accepts similar values from *config.json*.

### SetBucketPolicy/SetBucketPolicy.go

This example applies a policy to an Amazon S3 bucket.

`go run SetBucketPolicy.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### SetBucketWebsite/SetBucketWebsite.go

This example sets up an Amazon S3 bucket as a static website.

`go run SetBucketWebsite.go -b BUCKET -i INDEX [-e ERROR]`

- *BUCKET* is the name of the bucket.
- *INDEX* is the name of the index page.
- *ERROR* is the optional name of the error page.

The unit test accepts similar values from *config.json*.

### SetCors/SetCors.go

This example configures CORS rules for an Amazon S3 bucket by setting the allowed HTTP methods.

`go run SetCors.go -b BUCKET`

- *BUCKET* is the name of the bucket.

The unit test accepts a similar value from *config.json*.

### SetDefaultEncryption/SetDefaultEncryption.go

This example enforces encryption using an AWS Key Management Service (AWS KMS) key on an Amazon S3 bucket.

`go run SetDefaultEncryption.go -b BUCKET -k KMS-KEY-ID`

- *BUCKET* is the name of the bucket.
- *KMS-KEY-ID* is the ID of an AWS KMS key.

The unit test accepts similar values from *config.json*.

### TLS/s3SetTls12.go

This example creates a custom HTTP client using TLS version 1.2
and uses it to attempt to access an item in an Amazon S3 bucket.

`go run s3SetTls12 -b BUCKET -o OBJECT [-r REGION] [-v]`

- *BUCKET* name of the bucket (required)
- *OBJECT* is the name of the object (required)
- If *REGION* is not specified, defaults to **us-west-2**
- If -v (version) is not specified, configures the session for Go version 1.13

The unit test accepts similar values from *config.json*.

### UploadDirectory/UploadDirectory.go

This example uploads the files in a directory to an Amazon S3 bucket.

`go run UploadDirectory.go -b BUCKET -d DIRECTORY`

- *BUCKET* is the name of the bucket.
- *DIRECTORY* is the directory to upload.

The unit test accepts similar values from *config.json*.

### UploadObject/UploadObject.go

This example uploads a file to an Amazon S3 bucket.

`go run UploadObject.go -b BUCKET -f FILENAME`

- *BUCKET* is the name of the bucket.
- *FILENAME* is the file to upload.

The unit test accepts similar values from *config.json*.

### UploadStream/UploadStream.go

This example uploads a stream for a file to an Amazon S3 bucket.

`go run UploadStream.go -b BUCKET -f FILENAME -k KEY`

- *BUCKET* is the name of the bucket.
- *FILENAME* is the file to upload.
- *KEY* the name of the resulting object in the bucket.

The unit test accepts similar values from *config.json*.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum  permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all Regions.
  Some AWS services are available only in specific 
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running a unit test

Unit tests should delete any resources they create.
However, they might result in charges to your 
AWS account.

To run a unit test, enter the following:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter the following.

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
