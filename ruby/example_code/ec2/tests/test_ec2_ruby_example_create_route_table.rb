# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-create-route-table"

describe "#route_table_created_and_associated?" do
  let(:vpc_id) { "vpc-0b6f769731EXAMPLE" }
  let(:subnet_id) { "subnet-03d9303b57EXAMPLE" }
  let(:gateway_id) { "igw-06ca90c011EXAMPLE" }
  let(:destination_cidr_block) { "0.0.0.0/0" }
  let(:tag_key) { "my-key" }
  let(:tag_value) { "my-value" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        create_route_table: {
          route_table: {
            route_table_id: "rtb-225746EX"
          }
        },
        create_tags: {},
        create_route: {
          return: true
        },
        associate_route_table: {
          association_id: "rtbassoc-781d0dEX"
        }
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "creates and associates a route table" do
    expect(
      route_table_created_and_associated?(
        ec2_resource,
        vpc_id,
        subnet_id,
        gateway_id,
        destination_cidr_block,
        tag_key,
        tag_value
      )
    ).to be(true)
  end
end
