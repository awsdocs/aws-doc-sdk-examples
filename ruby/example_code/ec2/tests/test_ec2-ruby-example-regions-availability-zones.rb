# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-regions-availability-zones"

describe "#list_regions_endpoints" do
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_regions: {
          regions: [
            {
              endpoint: "ec2.ap-south-1.amazonaws.com",
              region_name: "ap-south-1"
            },
            {
              endpoint: "ec2.eu-west-1.amazonaws.com",
              region_name: "eu-west-1"
            },
            {
              endpoint: "ec2.ap-southeast-1.amazonaws.com",
              region_name: "ap-southeast-1"
            },
            {
              endpoint: "ec2.ap-southeast-2.amazonaws.com",
              region_name: "ap-southeast-2"
            },
            {
              endpoint: "ec2.eu-central-1.amazonaws.com",
              region_name: "eu-central-1"
            },
            {
              endpoint: "ec2.ap-northeast-2.amazonaws.com",
              region_name: "ap-northeast-2"
            },
            {
              endpoint: "ec2.ap-northeast-1.amazonaws.com",
              region_name: "ap-northeast-1"
            },
            {
              endpoint: "ec2.us-west-2.amazonaws.com",
              region_name: "us-west-2"
            },
            {
              endpoint: "ec2.sa-east-1.amazonaws.com",
              region_name: "sa-east-1"
            },
            {
              endpoint: "ec2.us-west-1.amazonaws.com",
              region_name: "us-west-1"
            },
            {
              endpoint: "ec2.us-west-2.amazonaws.com",
              region_name: "us-west-2"
            }
          ]
        }
      }
    )
  end

  it "displays information about regions and endpoints" do
    expect { list_regions_endpoints(ec2_client) }.not_to raise_error
  end
end

describe "#list_availability_zones" do
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_availability_zones: {
          availability_zones: [
            {
              messages: [],
              region_name: "us-west-2",
              state: "available",
              zone_name: "us-west-2a"
            },
            {
              messages: [],
              region_name: "us-west-2",
              state: "available",
              zone_name: "us-west-2b"
            },
            {
              messages: [],
              region_name: "us-west-2",
              state: "available",
              zone_name: "us-west-2c"
            },
            {
              messages: [],
              region_name: "us-west-2",
              state: "available",
              zone_name: "us-west-2d",
            },
            {
              messages: [],
              region_name: "us-west-2",
              state: "available",
              zone_name: "us-west-2e"
            }
          ]
        }
      }
    )
  end

  it "displays information about availability zones" do
    expect { list_availability_zones(ec2_client) }.not_to raise_error
  end
end
