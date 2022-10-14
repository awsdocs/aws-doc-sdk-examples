# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example demonstrates how to load information from a file into
# an existing table in Amazon DynamoDB named 'Movies'. The file must be named
# 'moviedata.json', must be in the same directory as this code example file,
# and must follow a specific format. For example, each table item for this
# particular table must be represented in the file with the following
# minimum information:
#
# {
#   "year": 2015,
#   "title": "The Big New Movie"
# }
#
# Table items can have additional information though, for example:
#
# [
#   {
#     "year": 2015,
#     "title": "The Big New Movie",
#     "info": {
#       "rating": 5.5,
#       "plot": "A big new movie launches.",
#       "genres": [
#         "Biography",
#         "Drama"
#       ],
#       "actors": [
#         "Larry",
#         "Moe",
#         "Curly"
#       ]
#     }
#   },
#   {
#     "year": 2017,
#     "title": "The Big New Movie 2",
#     "info": {
#       "rating": 3.0,
#       "plot": "Another big new movie launches.",
#       "running_time_secs": 7380
#       "actors": [
#         "Tina",
#         "Amy"
#       ]
#     }
#   }
# ]

# snippet-start:[dynamodb.Ruby.CodeExample.MoviesLoadData]
require "aws-sdk-dynamodb"
require "json"

$movie_counter = 0
$total_movies = 0

def add_item_to_table(dynamodb_client, table_item)
  dynamodb_client.put_item(table_item)
  $movie_counter += 1
  puts "Added movie #{$movie_counter}/#{$total_movies}: " \
    "'#{table_item[:item]['title']} " \
    "(#{table_item[:item]['year']})'."
rescue StandardError => e
  puts "Error adding movie '#{table_item[:item]['title']} " \
    "(#{table_item[:item]['year']})': #{e.message}"
  puts "Program stopped."
  exit 1
end

def run_me
# Replace us-west-2 with the AWS Region you're using for Amazon DynamoDB.
  region = "us-west-2"
  table_name = "Movies"
  data_file = "moviedata.json"

  # To use the downloadable version of Amazon DynamoDB,
  # uncomment the endpoint statement.
  Aws.config.update(
    # endpoint: 'http://localhost:8000',
    region: region
  )

  dynamodb_client = Aws::DynamoDB::Client.new
  file = File.read(data_file)
  movies = JSON.parse(file)
  $total_movies = movies.count

  puts "Adding #{$total_movies} movies from file '#{data_file}' " \
    "into table '#{table_name}'..."

  movies.each do |movie|
    table_item = {
      table_name: table_name,
      item: movie
    }
    add_item_to_table(dynamodb_client, table_item)
  end

  puts "Done."
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.CodeExample.MoviesLoadData]
