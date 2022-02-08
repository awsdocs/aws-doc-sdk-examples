# AWS SDK for Go V2 code examples for Amazon S3

## Purpose

These examples demonstrates how to perform several Amazon Simple Storage Service 
(Amazon S3) operations using version 2 of the AWS SDK for Go.

Presented here are the following examples:

* [`common/`](common/) -- The most common S3 operations: Creating a bucket, managing objects within it, and deleting objects/buckets.
* [`GetBucketAcl`](GetBucketAcl/) and [`GetObjectAcl`](GetObjectAcl/) -- Two examples of working with ACLs on S3 objects.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

You must have at least Go 1.17 installed. 

## Running the code

From the directory you wish to run the sample of, do the following:

```
go mod tidy
go run .
```


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


Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
