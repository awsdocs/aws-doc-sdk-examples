# IAM code examples for the SDK for Swift
## Overview
This folder contains code examples demonstrating how to use the AWS SDK for
Swift to use the Amazon Identity and Access Management (IAM) service. This
README discusses how to run these examples.

Amazon IAM is a web service for securely controlling access to AWS services.
With IAM, you can centrally manage users, security credentials such as access
keys, and permissions that control which AWS resources users and applications
can access. 

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
* [Attach an IAM policy to a
  role](./AttachRolePolicy/Sources/ServiceHandler/ServiceHandler.swift)
  (`AttachRolePolicy`)
* [Create a new IAM user](./CreateUser/Sources/ServiceHandler/ServiceHandler.swift) (`CreateUser`)
* [Create an IAM role](./CreateRole/Sources/ServiceHandler/ServiceHandler.swift) (`CreateRole`)
* [Create an IAM role linked to a specific service](./CreateServiceLinkedRole/Sources/ServiceHandler/ServiceHandler.swift) (`CreateServiceLinkedRole`)
* [Get information about an IAM policy](./GetPolicy/Sources/ServiceHandler/ServiceHandler.swift) (`GetPolicy`)
* [Get information about an IAM role](./GetRole/Sources/ServiceHandler/ServiceHandler.swift) (`GetRole`)
* [List all AWS policies](./ListPolicies/Sources/ServiceHandler/ServiceHandler.swift) (`ListPolicies`)
* [List all groups on an AWS account](./ListGroups/Sources/ServiceHandler/ServiceHandler.swift) (`ListGroups`)
* [List all users on an AWS account](./ListUsers/Sources/ServiceHandler/ServiceHandler.swift) (`ListUsers`)
* [List the managed policies attached to a role](./ListAttachedRolePolicies/Sources/ServiceHandler/ServiceHandler.swift). (`ListAttachedRolePolicies`)
* [List the policies embedded in a role](./ListRolePolicies/Sources/ServiceHandler/ServiceHandler.swift). This does _not_ include managed policies attached to the role. (`ListRolePolicies`)


<!-- ### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
 -->

<!-- ### Cross-service examples
Sample applications that work across multiple AWS services.
* [*Title of code example*](*relative link to code example*) --->

## Run the examples
To build any of these examples from a terminal window, navigate into its
directory, then use the following command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `ListUsers` directory, to build that example). Then type `xed.`
to open the example directory in Xcode. You can then use standard Xcode build
and run commands.

### Prerequisites
See the [Prerequisites](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/swift#Prerequisites) section in the README for the AWS SDK for Swift examples repository.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

To run the tests for an example, use the command `swift test` in the example's directory.

## Additional resources
* [IAM user guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/)
* [IAM API reference](https://docs.aws.amazon.com/IAM/latest/APIReference/)
* [IAM Developer Guide for Swift](https://docs.aws.amazon.com/sdk-for-swift/latest/developer-guide/examples-iam.html)
* [IAM API reference for Swift](https://awslabs.github.io/aws-sdk-swift/reference/0.x/AWSIAM/Home)
* [Security best practices in IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)

_Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0_