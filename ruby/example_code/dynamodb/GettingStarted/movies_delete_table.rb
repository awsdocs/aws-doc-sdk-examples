# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to delete an existing table in
# Amazon DynamoDB named 'Movies'.

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesDeleteTable]
require "aws-sdk-dynamodb"

def table_deleted?(dynamodb_client, table_name)
  dynamodb_client.delete_table(table_name: table_name)
  true
rescue StandardError => e
  puts "Error deleting table: #{e.message}"
  false
end

def run_me
  # Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = "us-west-2"
  table_name = "Movies"

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: 'http://localhost:8000',
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new

  puts "Deleting table '#{table_name}'..."

  if table_deleted?(dynamodb_client, table_name)
    puts "Table deleted."
  else
    puts "Table not deleted."
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesDeleteTable]
