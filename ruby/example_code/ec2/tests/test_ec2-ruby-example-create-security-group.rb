# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-create-security-group"

describe "#security_group_created_with_egress?" do
  let(:group_name) { "my-security-group" }
  let(:description) { "This is my security group." }
  let(:vpc_id) { "vpc-6713dfEX" }
  let(:ip_protocol) { "tcp" }
  let(:from_port) { "22" }
  let(:to_port) { "22" }
  let(:cidr_ip_range) { "0.0.0.0/0" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        create_security_group: {
          group_id: "sg-903004EX"
        },
        authorize_security_group_egress: {}
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "creates a security group and adds an egress rule" do
    expect(
      security_group_created_with_egress?(
        ec2_resource,
        group_name,
        description,
        vpc_id,
        ip_protocol,
        from_port,
        to_port,
        cidr_ip_range
      )
    ).to be(true)
  end
end
