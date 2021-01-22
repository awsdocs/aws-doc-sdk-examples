# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

def table_updated?(dynamodb_client,
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

def run_me
  region = 'us-west-2'
  table_name = 'Users'
  table_key_attribute = 'ID'
  attribute_to_update = 'AirMiles'
  new_attribute_value = 10000

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  update_expression = "SET #{attribute_to_update}=:pVal"
  expression_attribute_values = { ':pVal' => new_attribute_value }

  puts "Updating table '#{table_name}'..."

  if table_updated?(dynamodb_client,
    table_name,
    table_key_attribute,
    update_expression,
    expression_attribute_values
  )
    puts 'Table updated.'
  else
    puts 'Table not updated.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
