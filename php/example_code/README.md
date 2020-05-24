# AWS SDK for PHP examples

## Purpose

These code examples demonstrate how to use the AWS SDK for PHP to automate various AWS services. For more information, see the [AWS SDK for PHP Developer Documentation](https://docs.aws.amazon.com/sdk-for-php).

## Prerequisites

To run or test these code examples, you need the following:

- [PHP](https://www.php.net/)
- [PHPUnit](https://phpunit.de/), for unit testing
- The [AWS SDK for PHP](https://aws.amazon.com/sdk-for-php/)
- [AWS credentials](https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html)

For more information, see [Getting Started](https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/getting-started_index.html) in the *AWS SDK for PHP Developer Guide*.

## Running the code 

By default, these code examples run using the default AWS credential provider chain, which includes using an AWS shared credentials file and profiles. For more information, see [Using the AWS Credentials File and Credential Profiles](https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials_profiles.html) in the *AWS SDK for PHP Developer Guide*.

Running these code examples might result in charges to the AWS account that is associated with the AWS credentials being used.

## Running the tests

Some of these code examples have accompanying unit tests, which are designed to be run with PHPUnit. These unit test code files are in a subdirectory named `tests`, next to the code examples themselves. For more information, see the comments inside of the unit test code files.

## Additional information

- As an AWS best practice, grant these code examples least privilege, or only the permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see the 
  [AWS Regional Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/) on the AWS website.

