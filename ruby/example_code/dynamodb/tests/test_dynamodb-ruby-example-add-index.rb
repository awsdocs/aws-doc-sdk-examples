# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../dynamodb-ruby-example-add-index'

describe '#index_added?' do
  let(:index_definition) do
    {
      table_name: 'Users',
      attribute_definitions: [
        {
            attribute_name: 'AirMiles',
            attribute_type: 'N'
        }
      ],
      global_secondary_index_updates: [
        {
          create: {
            index_name: 'AirMileageIndex',
            key_schema: [
              {
                attribute_name: 'AirMiles',
                key_type: 'HASH'
              }
            ],
            projection: {
              projection_type: 'ALL'
            },
            provisioned_throughput: {
              read_capacity_units: 5,
              write_capacity_units: 10
            }
          }
        }
      ]
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        update_table: {
          table_description: {}
        }
      }
    )
  end

  it 'adds an index to a table' do
    expect(index_added?(dynamodb_client, index_definition)).to eq(true)
  end
end
