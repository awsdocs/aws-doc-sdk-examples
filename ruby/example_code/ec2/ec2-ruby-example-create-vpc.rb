# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# ec2-ruby-example-create-vpc.rb demonstrates how to create a virtual private cloud
# (VPC) in Amazon Virtual Private Cloud (Amazon VPC) and then tags the VPC.

# snippet-start:[ec2.Ruby.createVpc]

require "aws-sdk-ec2"

# Creates and tags an Amazon Virtual Private Cloud (Amazon VPC)
#
# @param ec2_resource [Aws::EC2::Resource] An initialized
#   Amazon Elastic Compute Cloud (Amazon EC2) resource object.
# @return vpc_id [String] The Id associated with the VPC;
#   otherwise, false.
def create_vpc(resource)
  vpc = resource.create_vpc(cidr_block: "10.0.0.1/24", tag_specifications: [
    {
      resource_type: "vpc",
      tags: [
        {
          key: "foo",
          value: "bar",
        }
      ]
    }
  ])
  if vpc.data.to_s
    puts "\nSuccessfully created VPC (#{vpc.data.vpc_id})! State: #{vpc.data.state}"
    return vpc.data.vpc_id
  else
    puts "There was an error creating VPC."
    return false
  end
end

def tag_vpc(resource, vpc_id)
  tags = resource.create_tags(resources: [vpc_id], tags: [
    {
      key: "baz",
      value: "biz"
    }
  ])
  if tags.batches.count > 0
    puts "\nVPC successfully tagged! - #{tags}"
  else
    puts "There was an error tagging VPC."
  end
end

# Full example call:
def run_me
  ec2_resource = Aws::EC2::Resource.new(region: "us-east-1")
  vpc_id = create_vpc(ec2_resource)
  tag_vpc(ec2_resource, vpc_id)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[ec2.Ruby.createVpc]
