# AWS SDK for PHP v3 code examples
## Overview
The code examples in this topic show you how to use the AWS SDK for PHP v3 with AWS.

The AWS SDK for PHP v3 provides a PHP API for AWS infrastructure services. Using the SDK, you can build applications on top of Amazon S3, Amazon EC2, Amazon DynamoDB, and more.

## Types of code examples
* **Single-service actions** - Code examples that show you how to call individual service functions.

* **Single-service scenarios** - Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* **Cross-service examples** - Sample applications that work across multiple AWS services.

### Find code examples
Single-service actions and scenarios are organized by AWS service in the [*example_code folder*](example_code). A README in each folder lists and describes how to run the examples.

Cross-service examples are located in the [*cross-services folder*](cross_service). A README in each folder describes how to run the example.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).


### Prerequisites
To run or test these code examples, you need the following:

- [PHP](https://www.php.net/) version 8.1 or higher
- [Composer](https://getcomposer.org), for dependency management
- [PHPUnit](https://phpunit.de/), for unit testing
- The [AWS SDK for PHP](https://aws.amazon.com/sdk-for-php/)
- [AWS credentials](https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html) set up

For more information, see [Getting Started](https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/getting-started_index.html) in the *AWS SDK for PHP Developer Guide*.

## Run the code

By default, these code examples run using the default AWS credential provider chain, which includes using an AWS shared credentials file and profiles.
For more information, see [Using the AWS Credentials File and Credential Profiles](https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials_profiles.html) in the *AWS SDK for PHP Developer Guide*.

Running these code examples might result in charges to the AWS account that is associated with the AWS credentials being used.

To install dependencies, run `composer install` (https://getcomposer.org) before attempting to run example code.

Many examples come with a `Runner.php` file to abstract the logic of the example from running the code.
From any example directory with a `Runner.php` file, run the example with `php Runner.php`.

## Tests
**Note**: Running the tests might result in charges to your AWS account.

Some of these code examples have accompanying unit and integration tests designed to run with PHPUnit.
These test files end with `Test.php` and are usually in a folder named `tests` next to the code examples themselves.
They don't run on their own, but must be run by using the `phpunit` command or your IDE.

To install dependencies, run `composer install` (https://getcomposer.org) before attempting to run test code.

To run the entire set of tests, run `phpunit` from the `php/example_code` directory.
To filter by a specific service, run `phpunit --testsuite <testsuite-name>` where `<testsuite-name>` is one of the options given with `phpunit --list-suites`.
To filter by either integration tests or unit tests, add `--group integ` or `--group unit` to the command.
The suite and group filters can be used together or separately.

For specific instructions on how to run individual tests, see the README files for each example.

## Docker image (Beta)
This example code will soon be available in a container image
hosted on [Amazon Elastic Container Registry (Amazon ECR)](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html).
This image will be preloaded with all PHP examples with dependencies pre-resolved. This way, you can explore
these examples in an isolated environment.

The [SDK for PHP image](https://gallery.ecr.aws/b4v4v1s0/php) is available on ECR Public.

## Additional resources
* [AWS SDK for PHP API Reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/index.html)
* [AWS SDK for PHP Developer Documentation](https://docs.aws.amazon.com/sdk-for-php)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
