#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Creates a route table for a VPC, with a route and tags, and associates it with a subnet.]
#snippet-keyword:[Amazon Elastic Compute Cloud]
#snippet-keyword:[create_route_table method]
#snippet-keyword:[Table.associate_with_subnet method]
#snippet-keyword:[Table.create_route method]
#snippet-keyword:[Table.create_tags method]
#snippet-keyword:[Ruby]
#snippet-service:[ec2]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
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

require 'aws-sdk-ec2'  # v2: require 'aws-sdk'

ec2 = Aws::EC2::Resource.new(region: 'us-west-2')

table = ec2.create_route_table({
    vpc_id: VPC_ID
  })

table.create_tags({ tags: [{ key: 'Name', value: 'MyGroovyRouteTable' }]})

table.create_route({
  destination_cidr_block: '0.0.0.0/0',
  gateway_id: IGW_ID
})

table.associate_with_subnet({
  subnet_id: SUBNET_ID
})

puts table.id
