# Amazon S3 bucket and object examples

## Purpose

Shows how to use the AWS SDK for PHP v3 to get started using bucket and object operations in Amazon Simple Storage
Service (Amazon S3). Learn to create, get, remove, and configure buckets and objects.

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from
anywhere on the web.*

## Code examples

### Scenario examples

* [Getting started with buckets and objects](GettingStartedWithS3.php)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the permissions required to perform a task. For more
  information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more
  information, see the
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured as described in
  the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html)
  .
- PHP 7.1 or later
- Composer installed

### Command

**Getting started with buckets and objects**

Interactively shows how to create a bucket and upload and download objects. To start, ensure your composer dependencies
are installed with the following command at a command prompt in the s3_basics directory:

```
composer install
```

Once your composer depencies are successfully installed, you can run the getting started file directly with:

```
php GettingStartedWithS3.php
```   

Or you can run it as part of a PHPUnit test with:

```
vendor/bin/phpunit S3BasicsTests.php
```

## Additional information

- [Amazon S3 documentation](https://docs.aws.amazon.com/s3)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
