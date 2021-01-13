# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesItemOps01]
require 'aws-sdk-dynamodb'

def add_item_to_table(dynamodb_client, table_item)
  dynamodb_client.put_item(table_item)
  puts "Added movie '#{table_item[:item][:title]} " \
    "(#{table_item[:item][:year]})'."
rescue StandardError => e
  puts "Error adding movie '#{table_item[:item][:title]} " \
    "(#{table_item[:item][:year]})': #{e.message}"
end

def run_me
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

  item = {
    year: year,
    title: title,
    info: {
      plot: 'Nothing happens at all.',
      rating: 0
    }
  }

  table_item = {
    table_name: table_name,
    item: item
  }

  puts "Adding movie '#{item[:title]} (#{item[:year]})' " \
    "to table '#{table_name}'..."
  add_item_to_table(dynamodb_client, table_item)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesItemOps01]
