# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to delete an existing item in
# an existing table in Amazon DynamoDB named 'Movies'.
# If an item with the specified attributes exists in the table,
# then that item is deleted. In this example, the item
# must have a 'year' attribute value of 2015 and a 'title' attribute value
# of 'The Big New Movie'. If so, and if the 'rating' value of the item's
# 'info' attribute is less than or equal to 5, then that item is deleted.

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesItemOps06]
require 'aws-sdk-dynamodb'

def table_item_deleted?(dynamodb_client, table_item)
  dynamodb_client.delete_item(table_item)
  true
rescue StandardError => e
  puts "Error deleting item: #{e.message}"
  false
end

def run_me
# Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = 'us-west-2'
  table_name = 'Movies'
  title = 'The Big New Movie'
  year = 2015

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: 'http://localhost:8000',
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new

  table_item = {
    table_name: table_name,
    key: {
      year: year,
      title: title
    },
    condition_expression: 'info.rating <= :val',
    expression_attribute_values: {
      ':val' => 5
    }
  }

  puts "Deleting item from table '#{table_name}' matching " \
    "'#{title} (#{year})' if specified criteria are met..."

  if table_item_deleted?(dynamodb_client, table_item)
    puts 'Item deleted.'
  else
    puts 'Item not deleted.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesItemOps06]
