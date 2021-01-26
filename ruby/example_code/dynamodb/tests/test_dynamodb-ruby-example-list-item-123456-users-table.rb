# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../dynamodb-ruby-example-list-item-123456-users-table'

describe '#get_item_from_table' do
  let(:table_item) do
    {
      table_name: 'Users',
      key: { 'ID': 123456 }
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        get_item: {
          :item => {
            'ID' => 123456,
            'FirstName' => 'John',
            'LastName' => 'Doe',
            'AirMiles' => 5000
          }
        }
      }
    )
  end

  it 'gets an item from a table' do
    expect{ get_item_from_table(dynamodb_client, table_item) }.not_to raise_error
  end
end
