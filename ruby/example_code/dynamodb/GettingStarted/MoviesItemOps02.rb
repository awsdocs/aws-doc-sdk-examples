# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to get information about an
# existing item in an existing table in Amazon DynamoDB named 'Movies'.
# If an item with the specified attributes exists in the table,
# information about that item is displayed. In this example, the item
# must have a 'year' attribute value of 2015 and a 'title' attribute value
# of 'The Big New Movie'.

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesItemOps02]
require 'aws-sdk-dynamodb'

def get_item_from_table(dynamodb_client, table_item)
  result = dynamodb_client.get_item(table_item)
  puts "#{result.item['title']} (#{result.item['year'].to_i}):"
  puts "  Plot:   #{result.item['info']['plot']}"
  puts "  Rating: #{result.item['info']['rating'].to_i}"
rescue StandardError => e
  puts "Error getting movie '#{table_item[:key][:title]} " \
        "(#{table_item[:key][:year]})': #{e.message}"
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
    }
  }

  puts "Getting information about '#{title} (#{year})' " \
    "from table '#{table_name}'..."
  get_item_from_table(dynamodb_client, table_item)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesItemOps02]
