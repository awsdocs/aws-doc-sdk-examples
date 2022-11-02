# DynamoDB code examples for the SDK for Ruby
## Overview
These examples show how to create and manage Amazon DynamoDB database tables using the SDK for Ruby.

DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a table](scenario_getting_started_movies.rb) (`CreateTable`)

* [Delete a table](scenario_getting_started_movies.rb) (`DeleteTable`)

* [Delete an item from a table](scenario_getting_started_movies.rb) (`DeleteItem`)

* [Get an item from a table](scenario_getting_started_movies.rb) (`GetItem`)

* [Get information about a table](scenario_getting_started_movies.rb) (`DescribeTable`)

* [Put an item in a table](scenario_getting_started_movies.rb) (`PutItem`)

* [Query a table](scenario_getting_started_movies.rb) (`Query`)

* [Scan a table](scenario_getting_started_movies.rb) (`Scan`)

* [Update an item in a table](scenario_getting_started_movies.rb) (`UpdateItem`)

* [Write a batch of items](scenario_getting_started_movies.rb) (`BatchWriteItem`)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Get started using tables, items, and queries](scenario_getting_started_movies.rb)





## Run the examples

### Prerequisites

See the [Ruby README.md](../../../ruby/README.md) for prerequisites.

### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

## Contributing
Code examples thrive on community contribution!
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)

### Tests
⚠️ Running tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
