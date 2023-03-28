# DynamoDB code examples for the SDK for Swift
## Overview
This folder contains code examples demonstrating how to use the AWS SDK for
Swift to use Amazon DynamoDB. This README discusses how to run these examples.

Amazon DynamoDB is a fully managed NoSQL database service that provides fast
and predictable performance with seamless scalability. DynamoDB lets you
offload the administrative burdens of operating and scaling a distributed
database so that you don't have to worry about hardware provisioning, setup
and configuration, replication, software patching, or cluster scaling.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/DynamoDB/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
* [Add an item to a table](./basics/MovieList/MovieTable.swift) (`PutItem`)
* [Add the contents of a JSON file to a table](./basics/MovieList/MovieTable.swift) (`BatchWriteItem`)
* [Create a table](./basics/MovieList/MovieTable.swift)
  (`CreateTable`)
* [Delete an item by key](./basics/MovieList/MovieTable.swift) (`DeleteItem`)
* [Delete a table](./basics/MovieList/MovieTable.swift) (`DeleteTable`)
* [Get an item by key](./basics/MovieList/MovieTable.swift) (`GetItem`)
* [Get multiple items by key](./BatchGetItem/Sources/MovieDatabase.swift)
  (`BatchGetItem`)
* [List all tables](./basics/ListTables/listtables.swift) (`ListTables`)
* [Query a table](./basics/MovieList/MovieTable.swift) (`Query`)
* [Scan a table with pagination](./basics/MovieList/MovieTable.swift) (`Scan`)
* [Update an item](./basics/MovieList/MovieTable.swift) (`UpdateItem`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [DynamoDB Basics](./basics/Sources/basics.swift). Demonstrates a common
  series of actions to create and perform common actions on a movie list
  database. (`Basics`)

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
(such as the `basics` directory, to build that example). Then type `xed.`
to open the example directory in Xcode. You can then use standard Xcode build
and run commands.

### Prerequisites
See the [Prerequisites](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/swift#Prerequisites) section in the README for the AWS SDK for Swift examples repository.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

To run the tests for an example, use the command `swift test` in the example's directory.

## Additional resources
* [DynamoDB Developer Guide](https://docs.aws.amazon.com/dynamodb/index.html)
* [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/)
* [DynamoDB Developer Guide for Swift](https://docs.aws.amazon.com/sdk-for-swift/latest/developer-guide/examples-ddb.html)
* [DynamoDB API Reference for Swift](https://awslabs.github.io/aws-sdk-swift/reference/0.x/AWSDynamoDB/Home)
* [Security best practices for DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/best-practices-security.html)

_Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0_