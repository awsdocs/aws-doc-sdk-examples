# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to update information about an
# existing item in an existing table in Amazon DynamoDB named 'Movies'.
# If an item with the specified attributes exists in the table,
# information about that item is updated. In this example, the item
# must have a 'year' attribute value of 2015 and a 'title' attribute value
# of 'The Big New Movie'. If so, the value of the item's 'info' attribute is
# changed to 5.5 for 'rating', to 'Everything happens all at once.' for
# 'plot', and to [ 'Larry', 'Moe', 'Curly' ] for 'actors'. Only information
# about the updated attributes are returned, as specified by 'UPDATED_NEW'.

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesItemOps03]
require "aws-sdk-dynamodb"

def table_item_updated?(dynamodb_client, table_item)
  response = dynamodb_client.update_item(table_item)
  puts "Table item updated with the following attributes for 'info':"
  response.attributes["info"].each do |key, value|
    if key == "rating"
      puts "#{key}: #{value.to_f}"
    else
      puts "#{key}: #{value}"
    end
  end
  true
rescue StandardError => e
  puts "Error updating item: #{e.message}"
  false
end

def run_me
  # Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = "us-west-2"
  table_name = "Movies"
  title = "The Big New Movie"
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
    update_expression: "SET info.rating = :r, info.plot = :p, info.actors = :a",
    expression_attribute_values: {
      ':r': 5.5,
      ':p': "Everything happens all at once.",
      ':a': %w[Larry Moe Curly]
    },
    return_values: "UPDATED_NEW"
  }

  puts "Updating table '#{table_name}' with information about " \
    "'#{title} (#{year})'..."

  if table_item_updated?(dynamodb_client, table_item)
    puts "Table updated."
  else
    puts "Table not updated."
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesItemOps03]
