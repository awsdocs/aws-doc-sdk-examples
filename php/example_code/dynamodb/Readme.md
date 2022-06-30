# Amazon DynamoDB basics

## Purpose

Shows how to use the AWS SDK for PHP v3 to get started using operations in Amazon DynamoDB. Learn to create tables, add
items, update data, create custom queries, and delete tables and items.

*Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless
scalability.*

## Code examples

### Scenario examples

* [Getting started with DynamoDB](dynamodb_basics/GettingStartedWithDynamoDB.php)

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

**Getting started DynamoDB**

This scenario shows you how to create an Amazon DynamoDB table for storing movie data. The scenario loads movies into
the table from a JSON-formatted file, walks you through an interactive demo to add, update, and delete movies one at a
time, and shows you how to query for sets of movies.

```
composer install
```

Once your composer dependencies are successfully installed, you can run the interactive getting started file directly
with:

```
php Runner.php
```   

Or, to have the choices automatically selected, you can run it as part of a PHPUnit test with:

```
..\..\vendor\bin\phpunit DynamoDBBasicsTests.php
```

## Additional information

- [Amazon DynamoDB documentation](https://docs.aws.amazon.com/dynamodb)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
