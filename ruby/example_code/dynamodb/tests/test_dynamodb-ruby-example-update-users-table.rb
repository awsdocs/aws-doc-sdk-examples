# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../dynamodb-ruby-example-update-users-table'

describe '#table_items_updated?' do
  let(:table_name) { 'Users' }
  let(:table_key_attribute) { 'ID' }
  let(:update_expression) { 'SET AirMiles=:pVal' }
  let(:expression_attribute_values) { { ':pVal' => 10000 } }  
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        scan: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: 'Users'
          },
          count: 2,
          items: [
            {
              'ID' => 123456,
              'FirstName' => 'John',
              'LastName' => 'Doe',
              'AirMiles' => 2500
            },
            {
              'ID' => 234567,
              'FirstName' => 'Jane',
              'LastName' => 'Doe',
              'AirMiles' => 5000
            }
          ],
          scanned_count: 2
        },
        update_item: [
          {
            attributes: {
              'ID' => 123456,
              'FirstName' => 'John',
              'LastName' => 'Doe',
              'AirMiles' => 10000
            }
          },
          {
            attributes: {
              'ID' => 234567,
              'FirstName' => 'Jane',
              'LastName' => 'Doe',
              'AirMiles' => 10000
            }
          }
        ]
      }
    )
  end

  it 'updates items in a table' do
    expect(table_items_updated?(
      dynamodb_client,
      table_name,
      table_key_attribute,
      update_expression,
      expression_attribute_values
    )).to be(true)
  end
end
