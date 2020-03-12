# AWS SDK for Go code examples for AWS CloudTrail

## Purpose

These examples demonstrate how to perform some AWS CloudTrail operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the *AWS SDK for Go Developer Guide*.

## Running the code examples

All of these code examples perform the operations in the default AWS Region
and use your default credentials.

### Operations

The **cloudtrailOps.go** file defines several AWS CloudTrail operations.

Use the following command to display the commands that invoke these operations.

`go run cloudtrailOps.go -h`

### Notes

- You should grant these code examples least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all regions.
  Some AWS services are available only in specific 
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the Unit Tests

Unit tests should delete any resources they create.
However, they might result in charges to your 
AWS account if a test fails.
If you run the tests with the option of seeing log messages and a test fails,
as described later in this document,
you'll see a log message that tells you which resources you must manually delete.

If you want to use the unit tests with an existing trail,
set the **TrailName** entry in *config.json*.

Otherwise, the unit tests create a trail with a random name that starts with **MyTrail-** to which the bucket events are sent

If you want to use the unit tests with an existing bucket,
set the **BucketName** entry in *config.json*.

Otherwise, the unit tests create a bucket with a random name that starts with **mybucket-**.

The unit test **cloudtrailOps_test.go**:

- Adds a couple of items to the bucket
- Displays a list of trails
- Lists any events in the trail from the current user
- If the unit test created a trail, it deletes the trail
- If the unit test created a bucket, it deletes the bucket

To run the unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 6.224s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

You can confirm it has deleted any trail it created by looking at the remaining trails
for any that start with **MyTrail-**:

`go run cloudtrailOps.go -l`