# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# dynamodb_ruby_example_load_movies.rb demonstrates how to
# add an item to an Amazon DynamoDB table using the AWS SDK for Ruby.

# Inputs:
# - REGION - The AWS Region.

# snippet-start:[dynamodb.Ruby.loadMovies]

require 'aws-sdk-dynamodb'
require 'json'

# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_item [Hash] The properties of the item, in the correct format.
# @example
#   add_item_to_table(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     {
#       table_name: 'Movies',
#       item: {
#         "year": 1985,
#         "title": "The Big Movie",
#         "info": {
#           "directors": ["Mary"],
#           "release_date": "1985-12-25T00:00:00Z",
#           "rating": 5.5,
#           "genres": [
#             "Action",
#             "Drama"
#           ],
#           "image_url": "http://docs.aws.amazon.com/assets/images/aws_logo_dark.png",
#           "plot": "Nothing happens at all.",
#           "rank": 2,
#           "running_time_secs": 7380,
#           "actors": [
#             "Larry",
#             "Moe",
#             "Curly"
#           ]
#         }
#       }
#     }
#   )
def add_item_to_table(dynamodb_client, table_item)
  dynamodb_client.put_item(table_item)
  puts "Added movie: #{table_item[:item]['title']} " \
    "(#{table_item[:item]['year']})"
rescue StandardError => e
  puts 'Error adding movie ' \
    "#{table_item[:item]['title']} " \
    "(#{table_item[:item]['year']})': #{e.message}"
  puts 'Program stopped.'
  exit 1
end

# Full example call:
def run_me
# Replace us-west-2 with the AWS Region you're using for AWS DynamoDB.
  region = 'us-west-2'
  table_name = 'Movies'
  data_file = 'moviedata.json'

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)
  file = File.read(data_file)
  movies = JSON.parse(file)

  puts "Adding movies from file '#{data_file}' " \
    "into table '#{table_name}'..."

  movies.each do |movie|
    table_item = {
      table_name: table_name,
      item: movie
    }
    add_item_to_table(dynamodb_client, table_item)
  end

  puts 'Done.'
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.loadMovies]
