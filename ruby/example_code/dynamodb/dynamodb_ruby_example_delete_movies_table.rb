# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# dynamodb_ruby_example_delete_movies_table.rb demonstrates how to
# delete a table in Amazon DynamoDB using the AWS SDK for Ruby.

# Inputs:
# - REGION - The AWS Region.

# snippet-start:[dynamodb.Ruby.deleteMovieTable]

require 'aws-sdk-dynamodb'

# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_name [String] The name of the table to delete.
# @return [Boolean] true if the table was deleted; otherwise, false.
# @example
#   ext 1 unless table_deleted?(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     'Movies'
#   )
def table_deleted?(dynamodb_client, table_name)
  dynamodb_client.delete_table(table_name: table_name)
  true
rescue StandardError => e
  puts "Error deleting table: #{e.message}"
  false
end

# Full example call:
def run_me
# Replace us-west-2 with the AWS Region you're using for AWS DynamoDB.
  region = 'us-west-2'
  table_name = 'Movies'

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  puts "Deleting table '#{table_name}'..."

  if table_deleted?(dynamodb_client, table_name)
    puts 'Table deleted.'
  else
    puts 'Table not deleted.'
  end
end

run_me if $PROGRAM_NAME == __FILE__

# snippet-end:[dynamodb.Ruby.deleteMovieTable]
