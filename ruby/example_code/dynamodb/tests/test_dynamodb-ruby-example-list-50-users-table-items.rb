# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../dynamodb-ruby-example-list-50-users-table-items'

describe '#scan_for_items_from_table' do
  let(:scan_condition) do
    {
        table_name: 'Users',
        select: 'ALL_ATTRIBUTES',
        limit: 50
      }
  end
  let(:dynamodb_client) do
    Aws::DynamoDB::Client.new(
      stub_responses: {
        scan: {
          consumed_capacity: {
            capacity_units: 1.0,
            table_name: 'Users'
          },
          count: 2,
          items: [
            {
              'ID' => 123456,
              'FirstName' => 'John',
              'LastName' => 'Doe',
              'AirMiles' => 5000
            },
            {
              'ID' => 234567,
              'FirstName' => 'Jane',
              'LastName' => 'Doe',
              'AirMiles' => 10000
            }
          ],
          scanned_count: 2
        }
      }
    )
  end

  it 'searches for matching items in a table' do
    expect { scan_for_items_from_table(dynamodb_client, scan_condition) }.not_to raise_error
  end
end
