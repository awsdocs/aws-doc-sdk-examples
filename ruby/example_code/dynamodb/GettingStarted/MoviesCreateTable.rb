# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to create a new table in
# Amazon DynamoDB. The table is named 'Movies' and contains
# a composite primary key (also known as a hash-range key)
# consisting of a 'year' hash/partition key attrribute and a
# 'title' range/sort key attribute. The table creation status
# is then returned, for example 'CREATING'.

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesCreateTable]
require 'aws-sdk-dynamodb'

def create_table(dynamodb_client, table_definition)
  response = dynamodb_client.create_table(table_definition)
  response.table_description.table_status
rescue StandardError => e
  puts "Error creating table: #{e.message}"
  'Error'
end

def run_me
# Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = 'us-west-2'

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: 'http://localhost:8000',
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new

  table_definition = {
    table_name: 'Movies',
    key_schema: [
      {
        attribute_name: 'year',
        key_type: 'HASH'  # Partition key.
      },
      {
        attribute_name: 'title',
        key_type: 'RANGE' # Sort key.
      }
    ],
    attribute_definitions: [
      {
        attribute_name: 'year',
        attribute_type: 'N'
      },
      {
        attribute_name: 'title',
        attribute_type: 'S'
      }
    ],
    provisioned_throughput: {
      read_capacity_units: 10,
      write_capacity_units: 10
    }
  }

  puts "Creating the table named 'Movies'..."
  create_table_result = create_table(dynamodb_client, table_definition)

  if create_table_result == 'Error'
    puts 'Table not created.'
  else
    puts "Table created with status '#{create_table_result}'."
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesCreateTable]
