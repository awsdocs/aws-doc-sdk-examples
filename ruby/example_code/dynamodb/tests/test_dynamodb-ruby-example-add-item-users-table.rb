# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../dynamodb-ruby-example-add-item-users-table'

describe '#item_added_to_table?' do
  let(:table_item) do
    {
      table_name: 'Users',
      item: {
        'ID': 123456,
        'FirstName': 'John',
        'LastName': 'Doe',
        'AirMiles': 0
      }
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        put_item: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: 'Users'
          }
        }
      }
    )
  end

  it 'adds an item to a table' do
    expect(item_added_to_table?(dynamodb_client, table_item)).to eq(true)
  end
end
