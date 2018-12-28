#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Creates a gateway, adds tags to it, and attaches it to a VPC.]
#snippet-keyword:[Amazon Elastic Compute Cloud]
#snippet-keyword:[attach_to_vpc method]
#snippet-keyword:[create_internet_gateway method]
#snippet-keyword:[create_tags method]
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

igw = ec2.create_internet_gateway

igw.create_tags({ tags: [{ key: 'Name', value: 'MyGroovyIGW' }]})
igw.attach_to_vpc(vpc_id: VPC_ID)

puts igw.id
