# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-dynamodb'

def get_item_from_table(dynamodb_client, table_item)
  result = dynamodb_client.get_item(table_item)
  puts "#{result.item['title']} (#{result.item['year'].to_i}):"
  puts "  Plot:   #{result.item['info']['plot']}"
  puts "  Rating: #{result.item['info']['rating'].to_i}"
rescue StandardError => e
  puts "Error getting movie '#{table_item[:key][:title]} " \
        "(#{table_item[:key][:year]})': #{e.message}"
end

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
    }
  }

  puts "Getting information about '#{title} (#{year})' " \
    "from table '#{table_name}'..."
  get_item_from_table(dynamodb_client, table_item)
end

run_me if $PROGRAM_NAME == __FILE__
