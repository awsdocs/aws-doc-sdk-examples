# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../dynamodb-ruby-example-show-tables-names-and-item-count'

describe '#get_table_names' do
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        list_tables: {
          table_names: [
            'Users',
            'Movies'
          ]
        }
      }
    )
  end

  it 'gets a list of table names' do
    expect(get_table_names(dynamodb_client)).to eq(['Users', 'Movies'])
  end
end

describe '#get_count_of_table_items' do
  let(:table_name) { 'Users' }
  let(:scan_condition) do
    {
        table_name: table_name,
        select: 'ALL_ATTRIBUTES',
        limit: 50
      }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        scan: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: table_name
          },
          count: 2,
          items: [
            {
              'ID' => 123456,
              'FirstName' => 'John',
              'LastName' => 'Doe',
              'AirMiles' => 5000
            },
            {
              'ID' => 234567,
              'FirstName' => 'Jane',
              'LastName' => 'Doe',
              'AirMiles' => 10000
            }
          ],
          scanned_count: 2
        }
      }
    )
  end

  it 'gets the number of items in a table' do
    expect(get_count_of_table_items(dynamodb_client, table_name)).to eq(2)
  end
end
