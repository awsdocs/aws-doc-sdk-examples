# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../MoviesDeleteTable"

describe "#table_deleted?" do
  let(:table_name) { "Movies" }
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        delete_table: {
          table_description: {
            item_count: 0,
            provisioned_throughput: {
              number_of_decreases_today: 1,
              read_capacity_units: 5,
              write_capacity_units: 5
            },
            table_name: "Movies",
            table_size_bytes: 0,
            table_status: "DELETING"
          }
        }
      }
    )
  end

  it "deletes a table" do
    expect(table_deleted?(dynamodb_client, table_name)).to eq(true)
  end
end
