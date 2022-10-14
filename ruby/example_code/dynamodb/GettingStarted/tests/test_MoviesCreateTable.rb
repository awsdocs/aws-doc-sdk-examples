# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../MoviesCreateTable"

describe "#create_table" do
  let(:table_status) { "ACTIVE" }
  let(:table_definition) do
    {
      table_name: "Movies",
      key_schema: [
        {
          attribute_name: "year",
          key_type: "HASH"  # Partition key.
        },
        {
          attribute_name: "title",
          key_type: "RANGE" # Sort key.
        }
      ],
      attribute_definitions: [
        {
          attribute_name: "year",
          attribute_type: "N"
        },
        {
          attribute_name: "title",
          attribute_type: "S"
        }
      ],
      provisioned_throughput: {
        read_capacity_units: 10,
        write_capacity_units: 10
      }
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        create_table: {
          table_description: {
            table_status: table_status
          }
        }
      }
    )
  end

  it "creates a table" do
    expect(create_table(dynamodb_client, table_definition)).to eq(table_status)
  end
end
