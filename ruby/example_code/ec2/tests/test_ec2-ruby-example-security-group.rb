# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-security-group"

describe "#create_security_group" do
  let(:group_name) { "my-security-group" }
  let(:description) { "This is my security group." }
  let(:vpc_id) { "vpc-6713dfEX" }
  let(:group_id) { "sg-0050f059851d102EX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        create_security_group: {
          group_id: group_id
        }
      }
    )
  end

  it "creates a security group" do
    expect(
      create_security_group(
        ec2_client,
        group_name,
        description,
        vpc_id
      )
    ).to eq(group_id)
  end
end

describe "#security_group_ingress_authorized?" do
  let(:security_group_id) { "sg-0050f059851d102EX" }
  let(:ip_protocol) { "tcp" }
  let(:from_port) { "22" }
  let(:to_port) { "22" }
  let(:cidr_ip_range) { "0.0.0.0/0" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        authorize_security_group_ingress: {}
      }
    )
  end

  it "adds an inbound rule to a security group" do
    expect(
      security_group_ingress_authorized?(
        ec2_client,
        security_group_id,
        ip_protocol,
        from_port,
        to_port,
        cidr_ip_range
      )
    ).to be(true)
  end
end

describe "#describe_security_groups" do
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_security_groups: {
          security_groups: [
            {
              group_name: "launch-wizard-1",
              description: "launch-wizard-1 created",
              group_id: "sg-03d327b2d28d827EX",
              owner_id: "111111111111",
              vpc_id: "vpc-6713dfEX",
              tags: [
                {
                  key: "my-key",
                  value: "my-value"
                }
              ],
              ip_permissions: [
                {
                  ip_protocol: "tcp",
                  from_port: 22,
                  to_port: 22,
                  ip_ranges: [
                    {
                      cidr_ip: "0.0.0.0/0"
                    }
                  ]
                }
              ],
              ip_permissions_egress: [
                {
                  ip_protocol: "-1",
                  from_port: -1,
                  to_port: -1,
                  ip_ranges: [
                    {
                      cidr_ip: "0.0.0.0/0"
                    }
                  ]
                }
              ]
            }
          ]
        }
      }
    )
  end

  it "displays information about available security groups" do
    expect { describe_security_groups(ec2_client) }.not_to raise_error
  end
end

describe "#security_group_deleted?" do
  let(:security_group_id) { "sg-0050f059851d102EX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        delete_security_group: {}
      }
    )
  end

  it "deletes a security group" do
    expect(security_group_deleted?(ec2_client, security_group_id)).to be(true)
  end
end
