# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../movies_query02"

describe "#query_for_items_from_table" do
  let(:query_condition) do
    {
      table_name: "Movies",
      projection_expression: "#yr, title, info.genres, info.actors[0]",
      key_condition_expression: "#yr = :yyyy AND title BETWEEN :letter1 AND :letter2",
      expression_attribute_names: { "#yr" => "year" },
      expression_attribute_values: {
        ":yyyy" => 1982,
        ":letter1" => "A",
        ":letter2" => "L"
      }
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        query: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: "Movies"
          },
          count: 2,
          items: [
            {
              "title" => "A Big Movie",
              "year" => 1982,
              "info" => {
                "genres" => %w[
                  Drama
                  Action
                ],
                "actors" => %w[
                  Larry
                  Moe
                  Curly
                ]
              }
            },
            {
              "title" => "Le Grand Film",
              "year" => 1982,
              "info" => {
                "genres" => [
                  "Drama"
                ],
                "actors" => %w[
                  Laurel
                  Hardy
                ]
              }
            }
          ],
          scanned_count: 2
        }
      }
    )
  end

  it "searches for matching items in a table" do
    expect { query_for_items_from_table(dynamodb_client, query_condition) }.not_to raise_error
  end
end
