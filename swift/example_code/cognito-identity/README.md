# Amazon Cognito Swift code examples

This README discusses how to run the Swift code examples for Amazon Cognito Identity. All examples require Swift 5.5 or later.

## Running the Amazon Cognito Swift examples

**IMPORTANT**

The Swift code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a user pool. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To build any of these examples from a terminal window, navigate into its directory then use the command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `FindOrCreateIdentityPool` directory, to build that example), then
type `xed .` to open the example directory in Xcode. You can then use standard
Xcode build and run commands.

You will find these examples: 

- **FindOrCreateIdentityPool** - Demonstrates how to search for a Cognito identity pool by name, creating it if the named pool doesn't exist yet. This example is explained in detail in the [getting started](https://docs.aws.amazon.com/sdk-for-swift/latest/developer-guide/getting-started.html) page of the AWS SDK for Swift Developer Guide.


To run these examples, you can setup your development environment to use the [Swift Package Manager](https://www.swift.org/package-manager/) to configure and build AWS SDK for Swift projects. For more information, 
see [Getting started with the AWS SDK for Swift](https://docs.aws.amazon.com/sdk-for-swift/latest/developer-guide/getting-started.html). 

## For more information...

* [AWS SDK for Swift Developer Guide](https://docs.aws.amazon.com/sdk-for-swift/latest/developer-guide)
* [AWS SDK for Swift Reference](https://awslabs.github.io/aws-sdk-swift/reference/0.x/)
* [AWS SDK for Swift on GitHub](https://github.com/awslabs/aws-sdk-swift)
