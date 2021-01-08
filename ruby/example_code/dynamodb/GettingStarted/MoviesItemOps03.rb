# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesItemOps03]
require 'aws-sdk-dynamodb'

def table_item_updated?(dynamodb_client, table_item)
  dynamodb_client.update_item(table_item)
  true
rescue StandardError => e
  puts "Error updating item: #{e.message}"
  false
end

def run_me
  region = 'us-west-2'
  table_name = 'Movies'
  title = 'The Big New Movie'
  year = 2015

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: "http://localhost:8000",
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new

  table_item = {
    table_name: table_name,
    key: {
      year: year,
      title: title
    },
    update_expression: 'SET info.rating = :r, info.plot = :p, info.actors = :a',
    expression_attribute_values: {
      ':r': 5.5,
      ':p': 'Everything happens all at once.',
      ':a': [ 'Larry', 'Moe', 'Curly' ]
    },
    return_values: 'UPDATED_NEW'
  }

  puts "Updating table '#{table_name}' with information about " \
    "'#{title} (#{year})'..."

  if table_item_updated?(dynamodb_client, table_item)
    puts 'Table updated.'
  else
    puts 'Table not updated.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesItemOps03]
