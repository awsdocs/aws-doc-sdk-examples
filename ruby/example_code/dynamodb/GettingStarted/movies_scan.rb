# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to search for items in
# an existing table in Amazon DynamoDB named 'Movies'.
# If an item matches the specified search condition, then informaton about
# that item is returned. In this example, a scan operation is performed. The
# scan operation searches through the entire table, which can contain
# thousands of items. In this example, matching items
# must have a 'year' attribute value of 1950 through 1959; however,
# this filter is applied only after the entire table has been
# searched (making it slower than a query operation).

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesScan]
require "aws-sdk-dynamodb"

def scan_for_items_from_table(dynamodb_client, scan_condition)
  # To display the elapsed time for the query operation,
  # uncomment the following three comments.
  # start = Time.now
  loop do
    result = dynamodb_client.scan(scan_condition)

    if result.items.count.zero?
      puts "No matching movies found (yet)..."
    else
      puts "Found #{result.items.count} matching movies (so far):"
      result.items.each do |movie|
        puts "#{movie['title']} (#{movie['year'].to_i}), " \
          "Rating: #{movie['info']['rating'].to_f}"
      end

      break if result.last_evaluated_key.nil?

      puts "Searching for more movies..."
      scan_condition[:exclusive_start_key] = result.last_evaluated_key
    end
  end
  puts "Finished searching."
  # finish = Time.now
  # puts "Search took #{finish - start} seconds."
rescue StandardError => e
  puts "Error scanning for table items: #{e.message}"
end

def run_me
  # Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = "us-west-2"
  table_name = "Movies"
  start_year = 1950
  end_year = 1959

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: 'http://localhost:8000',
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new

  scan_condition = {
    table_name: table_name,
    projection_expression: "#yr, title, info.rating",
    filter_expression: "#yr between :start_yr and :end_yr",
    expression_attribute_names: { "#yr" => "year" },
    expression_attribute_values: {
      ":start_yr" => start_year,
      ":end_yr" => end_year
    }
  }

  puts "Searching for items in the '#{table_name}' table from #{start_year} " \
    "through #{end_year}..."

  scan_for_items_from_table(dynamodb_client, scan_condition)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesScan]
