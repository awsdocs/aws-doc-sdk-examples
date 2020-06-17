# AWS SDK for Go code examples for Amazon SES

## Purpose

These examples demonstrate how to perform several Amazon Simple Email Service (Amazon SES) operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the _AWS SDK for Go Developer Guide_.

## Running the code

### DeleteAddress/DeleteAddress.go

This example deletes an Amazon SES email address.

`go run DeleteAddress -a ADDRESS`

- _ADDRESS_ is the email address to delete.

The unit test mocks the service client and the `DeleteVerifiedEmailAddress` function.

### GetStatistics/GetStatistics.go

This example retrieves statistics about Amazon SES send operations.

`go run GetStatistics.go`

The unit test mocks the service client and the `GetSendStatistics` function.

### ListAddresses/ListAddresses.go

This example lists the verified SES email addresses.

`go run ListAddresses.go`

The unit test mocks the service client and the `ListIdentities` and `GetIdentityVerificationAttributes` functions.

### SendMessage/SendMessage.go

This example sends an email message to a recipient.

`go run SendMessage.go -f SENDER -t RECIPIENT [-s SUBJECT]`

- _SENDER_ is the email address for the From field.
- _RECIPIENT_ is the email address for the recipient.
- _SUBJECT_ is the text for the Subject field.
  If this value is not supplied, it defaults to **Amazon SES Test (AWS SDK for Go)**.

The unit test mocks the service client and the `SendEmail` function.

### VerifyAddress/VerifyAddress.go

This example verifies an email address.

`go run VerifyAddress -r RECIPIENT`

- _RECIPIENT_ is the email address to verify.

The unit test mocks the service client and the `VerifyEmailAddress` function.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the _AWS Identity and Access Management User Guide_.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the unit tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run a unit test, enter the following.

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files.

```sh
PASS
ok      PATH 6.593s
```

To see any log messages, enter the following.

`go test -test.v`

You should see additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
