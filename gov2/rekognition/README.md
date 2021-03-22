# AWS SDK for Go V2 code examples for Amazon Rekognition

## Purpose

These examples demonstrates how to perform several Amazon Rekognition
operations using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go V2](https://aws.github.io/aws-sdk-go-v2/docs/configuring-sdk/)
in the AWS SDK for Go V2 Developer Guide.

## Running the code

### DetectLabels/DetectLabels.go

This example performs three tasks:

1. Saves the image in an Amazon Simple Storage Service (Amazon S3) bucket with an "uploads/" prefix.
1. Gets any ELIF information from the image and saves in the Amazon DynamoDB (DynamoDB) table.
1. Detects instances of real-world entities,
   such as flowers, weddings, and nature, within a JPEG or PNG image,
   and saves those instances as name/confidence pairs in the DynamoDB table.
1. Creates a thumbnail version of the image, no larger than 80 pixels by 80 pixels,
   and saves it in the same bucket with a "thumbs/" prefix and "thumb" suffix.

`go run DetectLabels.go -b BUCKET -t TABLE -f IMAGE`

- _BUCKET_ is the name of the bucket where the images are saved.
- _TABLE_ is the name of the bucket to which the item is copied.
- _IMAGE_ is the name of the JPG or PNG table.

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
