# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Gets the EC2 regions and availabity zones.]
# snippet-keyword:[Amazon Elastic Compute Cloud]
# snippet-keyword:[describe_availability_zones method]
# snippet-keyword:[describe_regions method]
# snippet-keyword:[Ruby]
# snippet-service:[ec2]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# Demonstrates how to get information about available Amazon EC2 regions and 
# availability zones for your current region.

require 'aws-sdk-ec2'  # v2: require 'aws-sdk'

ec2 = Aws::EC2::Client.new(region: 'us-east-1')

puts "Amazon EC2 region(s) (and their endpoint(s)) that are currently available to you:\n\n"
describe_regions_result = ec2.describe_regions()

describe_regions_result.regions.each do |region|
  puts "#{region.region_name} (#{region.endpoint})"  
end

puts "\nAmazon EC2 availability zone(s) that are available to you for your current region:\n\n"
describe_availability_zones_result = ec2.describe_availability_zones()

describe_availability_zones_result.availability_zones.each do |zone|
  puts "#{zone.zone_name} is #{zone.state}"
  if zone.messages.count > 0
    zone.messages.each do |message|
      puts "  #{message.message}"
    end
  end
end
  


