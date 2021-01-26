# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

# Searches for items in a table in Amazon DynamoDB by using a scan
# operation, which reads every item in a table or a secondary index.
#
# @param dynamodb_client [Aws::DynamoDB::Client] An initialized
#   Amazon DynamoDB client.
# @param scan_condition [Hash] The properties of the scan operation,
#   in the correct format.
# @example
#   scan_for_items_from_table(
#     Aws::DynamoDB::Client.new(region, 'us-west-2'),
#     {
#       table_name: 'Users',
#       select: 'ALL_ATTRIBUTES',
#       limit: 50
#     }
#   )
def scan_for_items_from_table(dynamodb_client, scan_condition)
  result = dynamodb_client.scan(scan_condition)

  if result.items.count.zero?
    puts 'No matching items found.'
  else
    puts "Displaying #{result.items.count} matching items:"
    result.items.each do |item|
      puts '-' * 15
      keys = item.keys
      keys.each do |k|
        case k
        when 'AirMiles'
          puts "Air miles:  #{item[k].to_i}"
        when 'FirstName'
          puts "First name: #{item[k]}"
        when 'ID'
          puts "User ID:    #{item[k].to_i}"
        when 'LastName'
          puts "Last name:  #{item[k]}"
        else
          puts "#{k}: #{item[k]}"
        end
      end
    end
  end
rescue StandardError => e
  puts "Error getting items: #{e.message}"
end

# Full example call:
def run_me
  region = 'us-west-2'
  table_name = 'Users'
  selection_criteria = 'ALL_ATTRIBUTES'
  items_limit = 50

  dynamodb_client = Aws::DynamoDB::Client.new(region: region)

  scan_condition = {
    table_name: table_name,
    select: selection_criteria,
    limit: items_limit
  }

  puts "Getting up to the first #{items_limit} items from " \
    "the '#{table_name}' table..."
  scan_for_items_from_table(dynamodb_client, scan_condition)
end

run_me if $PROGRAM_NAME == __FILE__
