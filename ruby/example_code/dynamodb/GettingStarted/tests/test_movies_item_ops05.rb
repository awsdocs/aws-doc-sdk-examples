# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../movies_item_ops05"

describe "#table_item_updated?" do
  let(:table_item) do
    {
      table_name: "Movies",
      key: {
        year: 2015,
        title: "The Big New Movie"
      },
      update_expression: "REMOVE info.actors[0]",
      condition_expression: "size(info.actors) > :num",
      expression_attribute_values: {
        ':num': 3
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
              "actors" => %w[Moe Curly],
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
