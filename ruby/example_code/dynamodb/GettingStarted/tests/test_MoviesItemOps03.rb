# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../MoviesItemOps03"

describe "#table_item_updated?" do
  let(:table_item) do
    {
      table_name: "Movies",
      key: {
        year: 2015,
        title: "The Big New Movie"
      },
      update_expression: "SET info.rating = :r, info.plot = :p, info.actors = :a",
      expression_attribute_values: {
        ':r': 5.5,
        ':p': "Everything happens all at once.",
        ':a': ["Larry", "Moe", "Curly"]
      },
      return_values: "UPDATED_NEW"
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        update_item: {
          attributes: {
            "year" => 2015,
            "title" => "The Big New Movie",
            "info" => {
              "actors" => ["Larry", "Moe", "Curly"],
              "plot" => "Everything happens all at once.",
              "rating" => 5.5
            }
          }
        }
      }
    )
  end

  it "updates an item in a table" do
    expect(table_item_updated?(dynamodb_client, table_item)).to eq(true)
  end
end
