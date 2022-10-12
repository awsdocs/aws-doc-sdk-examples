# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-create-subnet"

describe "#subnet_created_and_tagged?" do
  let(:vpc_id) { "vpc-6713dfEX" }
  let(:cidr_block) { "10.0.0.0/24" }
  let(:availability_zone) { "us-west-2a" }
  let(:tag_key) { "my-key" }
  let(:tag_value) { "my-value" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        create_subnet: {
          subnet: {
            subnet_id: "subnet-03d9303b57c7187EX"
          }
        },
        create_tags: {}
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "creates and tags a subnet" do
    expect(
      subnet_created_and_tagged?(
        ec2_resource,
        vpc_id,
        cidr_block,
        availability_zone,
        tag_key,
        tag_value
      )
    ).to be(true)
  end
end
