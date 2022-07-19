#  DynamoDB code examples for the AWS SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to manage Amazon DynamoDB resources.

*Amazon DynamoDB is a fully managed NoSQL database service that provides fast and
predictable performance with seamless scalability.*

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

* [Create a table](scenario_getting_started_movies.rb)
* [Delete a table](scenario_getting_started_movies.rb)
* [Delete an item from a table](scenario_getting_started_movies.rb)
* [Get an item from a table](scenario_getting_started_movies.rb)
* [Get information about a table](scenario_getting_started_movies.rb)
* [Put an item in a table](scenario_getting_started_movies.rb)
* [Query a table](scenario_getting_started_movies.rb)
* [Scan a table](scenario_getting_started_movies.rb)
* [Update an item in a table](scenario_getting_started_movies.rb)
* [Write a batch of items](scenario_getting_started_movies.rb)

### Scenario

* [Get started using tables, items, and queries](scenario_getting_started_movies.rb)

## Running the examples

Each scenario and usage demo can be run from the command prompt. Some scenarios run 
through a script without requiring input. Others interactively ask for more 
information as they run.

To start a scenario, run it at a command prompt.

```
ruby scenario_getting_started_movies.rb
```

### Prerequisites

Prerequisites for running the examples for this service can be found in the 
[README](../../README.md#Prerequisites) in the Ruby folder.

#### Additional required gems

* RubyZip 2.3.2 or later

## Tests

Instructions for running the tests for this service can be found in the 
[README](../../README.md#Tests) in the Ruby folder.

## Additional resources

* [Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide)
* [Amazon DynamoDB API Reference Guide](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference)
* [AWS SDK for Ruby Aws::DynamoDB Module](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/DynamoDB.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
