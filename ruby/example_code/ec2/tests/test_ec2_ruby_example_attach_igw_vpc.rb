# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-attach-igw-vpc"

describe "#internet_gateway_created_and_attached?" do
  let(:vpc_id) { "vpc-6713dfEX" }
  let(:tag_key) { "my-key" }
  let(:tag_value) { "my-value" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        create_internet_gateway: {
          internet_gateway: {
            internet_gateway_id: "igw-052bf3915781dd0EX"
          }
        },
        attach_internet_gateway: {},
        create_tags: {}
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "creates an internet gateway and attaches it to a VPC" do
    expect(
      internet_gateway_created_and_attached?(
        ec2_resource,
        vpc_id,
        tag_key,
        tag_value
      )
    ).to be(true)
  end
end
