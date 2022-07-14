# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../movies_scan"

describe "#scan_for_items_from_table" do
  let(:scan_condition) do
    {
      table_name: "Movies",
      projection_expression: "#yr, title, info.rating",
      filter_expression: "#yr between :start_yr and :end_yr",
      expression_attribute_names: { "#yr" => "year" },
      expression_attribute_values: {
        ":start_yr" => 1950,
        ":end_yr" => 1959
      }
    }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        scan: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: "Movies"
          },
          count: 2,
          items: [
            {
              "title" => "The Big Movie",
              "year" => 1950,
              "info" => {
                "rating" => 5.0
              }
            },
            {
              "title" => "The Big Movie 2",
              "year" => 1959,
              "info" => {
                "rating" => 3.5
              }
            }
          ],
          scanned_count: 2
        }
      }
    )
  end

  it "searches for matching items in a table" do
    expect { scan_for_items_from_table(dynamodb_client, scan_condition) }.not_to raise_error
  end
end
