# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../MoviesItemOps06"

describe "#table_item_deleted?" do
  let(:table_item) do
    {
      table_name: "Movies",
      key: {
        year: 2015,
        title: "The Big New Movie"
      },
      condition_expression: "info.rating <= :val",
      expression_attribute_values: {
        ":val" => 5
      }
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        delete_item: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: "Movies"
          }
        }
      }
    )
  end

  it "deletes an item from a table" do
    expect(table_item_deleted?(dynamodb_client, table_item)).to eq(true)
  end
end
