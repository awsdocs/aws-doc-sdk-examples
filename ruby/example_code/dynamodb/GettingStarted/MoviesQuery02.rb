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
# must have a 'year' attribute value of 1992 and a 'title' attribute value
# beginning with the letters 'A' through 'L'.

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesQuery02]
require 'aws-sdk-dynamodb'

def query_for_items_from_table(dynamodb_client, query_condition)
  # To display the elapsed time for the query operation,
  # uncomment the following three comments.
  # start = Time.now
  result = dynamodb_client.query(query_condition)
  # finish = Time.now
  # puts "Search took #{finish - start} seconds."
  if result.items.count.zero?
    puts 'No matching movies found.'
  else
    puts "Found #{result.items.count} matching movies:"
    result.items.each do |movie|
      puts "#{movie['title']} (#{movie['year'].to_i}):"
      if movie['info'].key?('genres') && movie['info']['genres'].count.positive?
        puts '  Genres:'
        movie['info']['genres'].each do |genre|
          puts "    #{genre}"
        end
      end
      if movie['info'].key?('actors') && movie['info']['actors'].count.positive?
        puts '  Actors:'
        movie['info']['actors'].each do |actor|
          puts "    #{actor}"
        end
      end
    end
  end
rescue StandardError => e
  puts "Error querying for table items: #{e.message}"
end

def run_me
# Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = 'us-west-2'
  table_name = 'Movies'
  year = 1982
  letter1 = 'A'
  letter2 = 'L'

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: 'http://localhost:8000',
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new

  query_condition = {
    table_name: table_name,
    projection_expression: '#yr, title, info.genres, info.actors[0]',
    key_condition_expression: '#yr = :yyyy AND title BETWEEN :letter1 AND :letter2',
    expression_attribute_names: { '#yr' => 'year' },
    expression_attribute_values: {
      ':yyyy' => year,
      ':letter1' => letter1,
      ':letter2' => letter2
    }
  }

  puts "Searching for items in the '#{table_name}' table from '#{year}' and " \
    "titles starting with the letters '#{letter1}' through '#{letter2}'..."

  query_for_items_from_table(dynamodb_client, query_condition)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesQuery02]
