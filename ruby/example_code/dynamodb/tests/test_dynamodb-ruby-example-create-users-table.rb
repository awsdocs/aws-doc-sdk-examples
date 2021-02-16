# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../dynamodb-ruby-example-create-users-table'

describe '#table_created?' do
  let(:table_status) { 'ACTIVE' }  
  let(:table_name) { 'Users' }
  let(:attribute_definitions) do
    [
      {
        attribute_name: 'ID',
        attribute_type: 'N'
      },
      {
        attribute_name: 'FirstName',
        attribute_type: 'S'
      },
      {
        attribute_name: 'LastName',
        attribute_type: 'S'
      }
    ]
  end
  let(:key_schema) do
    [
      {
        attribute_name: 'ID',
        key_type: 'HASH'
      }
    ]
  end
  let(:global_secondary_indexes) do
    [
      {
        index_name: 'LastNameFirstNameIndex',
        key_schema: [
          {
            attribute_name: 'FirstName',
            key_type: 'HASH'
          },
          {
            attribute_name: 'LastName',
            key_type: 'RANGE'
          }
        ],
        projection: { projection_type: 'ALL' },
        provisioned_throughput: {
          read_capacity_units: 5,
          write_capacity_units: 10
        }
      }
    ]
  end
  let(:provisioned_throughput) do
    {
        read_capacity_units: 5,
        write_capacity_units: 10
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        create_table: {
          table_description: {
            table_status: table_status
          }
        },
        describe_table: {
          table: {
            table_status: table_status
          }
        }
      }
    )
  end

  it 'creates a table' do
    expect(table_created?(
      dynamodb_client,
      table_name,
      attribute_definitions,
      key_schema,
      global_secondary_indexes,
      provisioned_throughput
    )).to eq(true)
  end
end
