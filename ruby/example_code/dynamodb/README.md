# AWS SDK for Ruby code examples for Amazon DynamoDB

## Purpose
This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate Amazon DynamoDB.

Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable 
performance with seamless scalability.

## Code examples 

### API examples
- [Add an index](./dynamodb-ruby-example-add-index.rb)
- [Add item to a table](./dynamodb-ruby-example-add-item-users-table.rb)
- [Create a table](./dynamodb-ruby-example-create-users-table.rb)
- [List items in a table (scan)](./dynamodb-ruby-example-list-50-users-table-items.rb)
- [Get item from a table](./dynamodb-ruby-example-list-item-123456-users-table.rb)
- [Gets list of available table names](./dynamodb-ruby-example-show-tables-names-and-item-count.rb)
- [Update a table](./dynamodb-ruby-example-update-users-table.rb)
- [Create an item (movie example)](./dynamodb_ruby_example_create_movies_item.rb)
- [Create a table (movie example)](./dynamodb_ruby_example_create_movies_table.rb)
- [Delete an item from a table (movie example)](./dynamodb_ruby_example_delete_movies_item.rb)
- [Delete a table (movie example)](./dynamodb_ruby_example_delete_movies_table.rb)
- [Add an item to a table (movie example)](./dynamodb_ruby_example_load_movies.rb)
- [Get an item from a table (movie example)](./dynamodb_ruby_example_read_movies_item.rb)
- [Update an item in a table (movie example)](./dynamodb_ruby_example_update_movies_item.rb)

## Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific AWS Regions. For more information, see the 
  [AWS Regional Services List](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- AWS credentials or an AWS Security Token Service (AWS STS) access token. For details, see 
  [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html) in the 
  *AWS SDK for Ruby Developer Guide*.
- To run the code examples, Ruby version 1.9 or later. For Ruby download and installation instructions, see 
  [Download Ruby](https://www.ruby-lang.org/en/downloads/) on the Ruby Progamming Language website.
- To test the code examples, RSpec 3.9 or later. For RSpec download and installation instructions, see the [rspec/rspec](https://github.com/rspec/rspec) repository in GitHub.
- The AWS SDK for Ruby. For AWS SDK for Ruby download and installation instructions, see 
  [Install the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html) in the 
  *AWS SDK for Ruby Developer Guide*.
  
Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `dynamodb-ruby-example-add-index.rb` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
ruby dynamodb-ruby-example-add-index.rb
```

Most of these files have been refactored into reusable functions that can be copied into your own code. You can then call those functions directly from your own code without modifying the copied function code itself. For example, you could copy the `create_table` function code from the `dynamodb_ruby_example_create_movies_table.rb` file into your own code. You could then adapt the code in the `run_me` function in that same file as a basis to write your own code to call the copied `create_table` function.

## Running the tests

Most of these code example files have accompanying tests that are written to work with RSpec. These tests are in the `tests` folder and contain the same file name as the corresponding code example file, for example `tests/test_dynamodb_ruby_example_create_movies_table.rb` contains tests for `dynamodb_ruby_example_create_movies_table.rb`.

To use RSpec to run all tests within a file, specify the path to that file, for example:

```
rspec tests/test_dynamodb_ruby_example_create_movies_table.rb
```

To explore additional options for using RSpec to run tests, run the `rspec --help` command. 

Most of these tests are designed to use stubs, to avoid generating unnecessary costs in an AWS account. For more information, see [Stubbing Client Responses and Errors](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/stubbing.html) in the *AWS SDK for Ruby Developer Guide*.


## Additional information
- [Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide)
- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs)
- [RSpec Documentation](https://rspec.info/documentation)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
