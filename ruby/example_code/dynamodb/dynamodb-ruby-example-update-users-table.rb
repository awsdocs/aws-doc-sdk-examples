# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# Updates items in a table in Amazon DynamoDB.
#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param table_name [String] The name of the table.
# @param table_key_attribute [String] The primary key of the items to update.
# @param update_expression [String] An expression that defines one or more
#   attributes for the items to be updated, the action to be performed on them,
#   and the new values for them.
# @param expression_attribute_values [Hash] The values to be substituted in
#   the update expression.
# @return [Boolean] true if the items were updated; otherwise, false.
# @example
#   exit 1 unless table_items_updated?(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     'Users',
#     'ID',
#     'SET AirMiles=:pVal',
#     { ':pVal' => 10000 }
#   )
def table_items_updated?(dynamodb_client,
  table_name,
  table_key_attribute,
  update_expression,
  expression_attribute_values
)
  result = dynamodb_client.scan(
    table_name: table_name,
    select: 'ALL_ATTRIBUTES')

  if result.items.count.zero?
    puts 'No items found to update.'
  else
    result.items.each do |item|
      dynamodb_client.update_item(
        table_name: table_name,
        key: { "#{table_key_attribute}": item["#{table_key_attribute}"] },
        update_expression: update_expression,
        expression_attribute_values: expression_attribute_values
      )
    end
  end
  true
rescue StandardError => e
  puts "Error updating table #{e.message}"
  false
end

# Full example call:
def run_me
  region = 'us-west-2'
  table_name = 'Users'
  table_key_attribute = 'ID'
  attribute_to_update = 'AirMiles'
  new_attribute_value = 10000

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  update_expression = "SET #{attribute_to_update}=:pVal"
  expression_attribute_values = { ':pVal' => new_attribute_value }

  puts "Updating items in table '#{table_name}'..."

  if table_items_updated?(dynamodb_client,
    table_name,
    table_key_attribute,
    update_expression,
    expression_attribute_values
  )
    puts 'Table items updated.'
  else
    puts 'Table items not updated.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
