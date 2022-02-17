# AWS SDK for Go Code Examples for AWS CloudFormation

## Purpose

This example demonstrates how to perform the following tasks in your default AWS Region
using your default credentials:

- Create a stack
- List your stacks
- Delete a stack

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the Code

### Syntax

`go run CfnCodeOps.go [-o all | create | list | delete] [-n ` *stack-name*`] [-t ` *template-name*`]`

- ```all``` is the default value.
- *stack-name* is the name of the stack to create or delete.
- *template-name* is the name of the (local) file containing an AWS CloudFormation template.
- If you supply ```-o create```, ```-o delete```, ```-o all```, or no ```o``` flag
  (which default to ```-o add```),
  ```-n ``` *stack-name* is required.
- If you supply ```-o create```, ```-o all```, or no ```-o``` flag
  (which default to ```-o add```),
  ```-t``` *template-name* is required.

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

## Running the Unit Tests

Unit tests should delete any resources they create.
However, they might result in charges to your 
AWS account.

The unit test gets the name of the template file from the **TemplateFile** entry in *config.json*.
By default this value is *template.json*,
which creates an Amazon S3 bucket
(the policy includes deleting the bucket when the stack is deleted).

The *config.json* file has one additional member, **Debug**.
If you set it to **true**, the unit test displays additional log information.

The unit test creates and deletes a stack with a random name.
The stack creates one resource, a private AWS S3 bucket in your default Region with a random name.

To run the unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 65.593s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

You can confirm it has deleted any resources it created by running:

```go run CfnCrudOps.go -o list | sed /DELETE_COMPLETE/d | grep stack-```
