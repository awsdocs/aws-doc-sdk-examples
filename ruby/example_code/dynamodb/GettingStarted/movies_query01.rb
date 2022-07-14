# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to search for items in
# an existing table in Amazon DynamoDB named 'Movies'.
# If an item matches the specified search condition, then informaton about
# that item is returned. In this example, a query operation is performed. The
# query condition must reference a hash/partition key. (Referencing a range/
# sort key, if available, is optional and can speed up query operations. Also,
# query operations are faster than scan operations, as scan operations must
# search through every item in a table.) In this example, matching items
# must have a 'year' attribute value of 1985.

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesQuery01]
require "aws-sdk-dynamodb"

def query_for_items_from_table(dynamodb_client, query_condition)
  # To display the elapsed time for the query operation,
  # uncomment the following three comments.
  # start = Time.now
  result = dynamodb_client.query(query_condition)
  # finish = Time.now
  # puts "Search took #{finish - start} seconds."
  if result.items.count.zero?
    puts "No matching movies found."
  else
    puts "Found #{result.items.count} matching movies:"
    result.items.each do |movie|
      puts "#{movie['title']} (#{movie['year'].to_i})"
    end
  end
rescue StandardError => e
  puts "Error querying for table items: #{e.message}"
end

def run_me
  # Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = "us-west-2"
  table_name = "Movies"
  year = 1985

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: 'http://localhost:8000',
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new

  # To query on the 'title' range/sort key in addition to the 'year'
  # hash/partition key, uncomment the following three 'title' comments.
  query_condition = {
    table_name: table_name,
    key_condition_expression: "#yr = :yyyy", # '#yr = :yyyy AND #t = :title'
    expression_attribute_names: {
      # '#t' => 'title',
      "#yr" => "year"
    },
    expression_attribute_values: {
      # ':title' => 'After Hours',
      ":yyyy" => year
    }
  }

  puts "Searching for items in the '#{table_name}' table from '#{year}'..."

  query_for_items_from_table(dynamodb_client, query_condition)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesQuery01]
