# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# Adds an item to a table in Amazon DynamoDB.
#
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
#           "plot": "Nothing happens at all.",
#           "rating": 5.5
#         }
#       }
#     }
#   )
def add_item_to_table(dynamodb_client, table_item)
  dynamodb_client.put_item(table_item)
  puts "Added movie '#{table_item[:item][:title]} " \
    "(#{table_item[:item][:year]})'."
rescue StandardError => e
  puts "Error adding movie '#{table_item[:item][:title]} " \
    "(#{table_item[:item][:year]})': #{e.message}"
end

# Full example call:
def run_me
  region = 'us-west-2'
  table_name = 'Movies'
  year = 2015
  title = 'The Big New Movie'
  plot = 'Nothing happens at all.'
  rating = 5.5

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  table_item = {
    table_name: table_name,
    item: {
      year: year,
      title: title,
      info: {
        plot: plot,
        rating: rating
      }
    }
  }

  puts "Adding movie '#{table_item[:item][:title]} " \
    " (#{table_item[:item][:year]})' " \
    "to table '#{table_name}'..."
  add_item_to_table(dynamodb_client, table_item)
end

run_me if $PROGRAM_NAME == __FILE__
