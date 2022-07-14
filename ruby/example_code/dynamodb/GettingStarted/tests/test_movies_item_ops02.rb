# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../movies_item_ops02"

describe "#get_item" do
  let(:table_item) do
    {
      table_name: "Movies",
      key: {
        year: 2015,
        title: "The Big New Movie"
      }
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        get_item: {
          item: {
            "title" => "The Big New Movie",
            "year" => 2015,
            "info" => {
              "plot" => "Nothing happens at all.",
              "rating" => 5.5
            }
          }
        }
      }
    )
  end

  it "gets an item from a table" do
    expect { get_item_from_table(dynamodb_client, table_item) }.not_to raise_error
  end
end
