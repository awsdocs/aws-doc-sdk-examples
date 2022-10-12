# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../MoviesLoadData"

describe "#add_item_to_table" do
  let(:item) do
    JSON.parse("{\"year\":2015,\"title\":\"The Big New Movie\",\"info\":{\"rating\":5.5,\"plot\":\"Nothing happens at all.\",\"actors\":[\"Larry\",\"Moe\",\"Curly\"]}}")
  end
  let(:table_item) do
    {
      table_name: "Movies",
      item: item
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        put_item: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: "Movies"
          }
        }
      }
    )
  end

  it "adds an item to a table" do
    expect { add_item_to_table(dynamodb_client, table_item) }.not_to raise_error
  end
end
