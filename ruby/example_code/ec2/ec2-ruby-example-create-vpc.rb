#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-service:[Amazon EC2]
#snippet-sourcetype:[<<snippet or full-example>>]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

vpc = ec2.create_vpc({ cidr_block: '10.200.0.0/16' })

# So we get a public DNS
vpc.modify_attribute({
  enable_dns_support: { value: true }
})

vpc.modify_attribute({
  enable_dns_hostnames: { value: true }
})

# Name our VPC
vpc.create_tags({ tags: [{ key: 'Name', value: 'MyGroovyVPC' }]})

puts vpc.vpc_id
