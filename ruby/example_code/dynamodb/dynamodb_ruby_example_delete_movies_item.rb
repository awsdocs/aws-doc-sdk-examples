# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# dynamodb_ruby_example_delete_movies_item.rb demonstrates how to
# delete an item from a table in Amazon DynamoDB using the AWS SDK for Ruby.

# Inputs:
# - REGION - The AWS Region.

# snippet-start:[dynamodb.Ruby.deleteMovieItem]

require 'aws-sdk-dynamodb'

# Deletes an item from a table in Amazon DynamoDB.
#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_item [Hash] The properties of the item, in the correct format.
# @return [Boolean] true if the item was deleted; otherwise, false.
# @example
#   ext 1 unless item_deleted_from_table?(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     {
#       table_name: 'Movies',
#       key: {
#         year: 2015,
#         title: 'The Big New Movie'
#       }
#     }
#   )
def item_deleted_from_table?(dynamodb_client, table_item)
  dynamodb_client.delete_item(table_item)
  true
rescue StandardError => e
  puts "Error deleting item: #{e.message}"
  false
end

# Full example call:
def run_me
  region = 'REGION'
  table_name = 'Movies'
  year = 2015
  title = 'The Big New Movie'

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  table_item = {
    table_name: table_name,
    key: {
      year: year,
      title: title
    }
  }

  puts "Deleting movie '#{title} (#{year})' from the '#{table_name}' table..."

  if item_deleted_from_table?(dynamodb_client, table_item)
    puts 'Item deleted.'
  else
    puts 'Item not deleted.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[dynamodb.Ruby.deleteMovieItem]
