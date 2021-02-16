# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# Updates an item in a table in Amazon DynamoDB.
#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_item [Hash] The properties of the item, in the correct format.
# @return [Boolean] true if the item was updated; otherwise, false.
# @example
#   exit 1 unless table_item_updated?(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     {
#       table_name: 'Movies',
#       key: {
#         year: 2015,
#         title: 'The Big New Movie'
#       },
#       update_expression: 'SET info.rating = :r',
#       expression_attribute_values: { ':r': 0.1 },
#       return_values: 'UPDATED_NEW'
#     }
#   )
def table_item_updated?(dynamodb_client, table_item)
  response = dynamodb_client.update_item(table_item)
  puts "Table item updated with the following attributes for 'info':"
  response.attributes['info'].each do |key, value|
    if key == 'rating'
      puts "#{key}: #{value.to_f}"
    else
      puts "#{key}: #{value}"
    end
  end
  true
rescue StandardError => e
  puts "Error updating item: #{e.message}"
  false
end

# Full example call:
def run_me
  region = 'us-west-2'
  table_name = 'Movies'
  title = 'The Big New Movie'
  year = 2015

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  table_item = {
    table_name: table_name,
    key: {
      year: year,
      title: title
    },
    update_expression: 'SET info.rating = :r',
    expression_attribute_values: { ':r': 0.1 },
    return_values: 'UPDATED_NEW'
  }

  puts "Updating table '#{table_name}' with information about " \
    "'#{title} (#{year})'..."

  if table_item_updated?(dynamodb_client, table_item)
    puts 'Table updated.'
  else
    puts 'Table not updated.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
