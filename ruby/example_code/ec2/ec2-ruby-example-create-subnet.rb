# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'aws-sdk'

ec2 = Aws::EC2::Resource.new(region: 'us-west-2')

subnet = ec2.create_subnet({
  vpc_id: VPC_ID,
  cidr_block: '10.200.10.0/24',
  availability_zone: 'us-west-2a'
})

subnet.create_tags({ tags: [{ key: 'Name', value: 'MyGroovySubnet' }]})
puts subnet.id
