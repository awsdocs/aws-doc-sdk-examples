# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-elastic-ips"

describe "#instance_exists?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instances: {
          reservations: [
            instances: [
              {
                instance_id: instance_id
              }
            ]
          ]
        }
      }
    )
  end

  it "checks whether the instance exists" do
    expect(instance_exists?(ec2_client, instance_id)).to be(true)
  end
end

describe "#allocate_elastic_ip_address" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:allocation_id) { "eipalloc-0e7e1c46c5ee5f8EX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        allocate_address: {
          allocation_id: allocation_id
        }
      }
    )
  end

  it "checks for the Elastic IP address allocation ID" do
    expect(allocate_elastic_ip_address(ec2_client)).to eq(allocation_id)
  end
end

describe "#associate_elastic_ip_address_with_instance" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:allocation_id) { "eipalloc-0e7e1c46c5ee5f8EX" }
  let(:association_id) { "eipassoc-010e2d189043030EX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        associate_address: {
          association_id: association_id
        }
      }
    )
  end

  it "checks for the Elastic IP address allocation ID" do
    expect(
      associate_elastic_ip_address_with_instance(
        ec2_client,
        allocation_id,
        instance_id
      )
    ).to eq(association_id)
  end
end

describe "#describe_addresses_for_instance" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_addresses: {
          addresses: [
            {
              public_ip: "203.0.113.0",
              private_ip_address: "10.0.1.241"
            }
          ]
        }
      }
    )
  end

  it "lists information about the instance" do
    expect { describe_addresses_for_instance(ec2_client, instance_id) }.not_to raise_error
  end
end

describe "#elastic_ip_address_released?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:allocation_id) { "eipalloc-0e7e1c46c5ee5f8EX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        release_address: {}
      }
    )
  end

  it "releases the Elastic IP address" do
    expect(elastic_ip_address_released?(ec2_client, allocation_id)).to be(true)
  end
end
