# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-create-vpc"

describe "#vpc_created_and_tagged?" do
  let(:cidr_block) { "10.0.0.0/24" }
  let(:tag_key) { "my-key" }
  let(:tag_value) { "my-value" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        create_vpc: {
          vpc: {
            vpc_id: "vpc-6713dfEX"
          }
        },
        modify_vpc_attribute: {},
        create_tags: {}
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "creates and tags a VPC" do
    expect(
      vpc_created_and_tagged?(
        ec2_resource,
        cidr_block,
        tag_key,
        tag_value
      )
    ).to be(true)
  end
end
