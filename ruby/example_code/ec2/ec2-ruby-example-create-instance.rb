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
require 'base64'

# User code that's executed when the instance starts
script = ''

encoded_script = Base64.encode64(script)

ec2 = Aws::EC2::Resource.new(region: 'us-west-2')

instance = ec2.create_instances({
  image_id: 'IMAGE_ID',
  min_count: 1,
  max_count: 1,
  key_name: 'MyGroovyKeyPair',
  security_group_ids: ['SECURITY_GROUP_ID'],
  user_data: encoded_script,
  instance_type: 't2.micro',
  placement: {
    availability_zone: 'us-west-2a'
  },
  subnet_id: 'SUBNET_ID',
  iam_instance_profile: {
    arn: 'arn:aws:iam::' + 'ACCOUNT_ID' + ':instance-profile/aws-opsworks-ec2-role'
  }
})

# Wait for the instance to be created, running, and passed status checks
ec2.client.wait_until(:instance_status_ok, {instance_ids: [instance[0].id]})

# Name the instance 'MyGroovyInstance' and give it the Group tag 'MyGroovyGroup'
instance.create_tags({ tags: [{ key: 'Name', value: 'MyGroovyInstance' }, { key: 'Group', value: 'MyGroovyGroup' }]})

puts instance.id
puts instance.public_ip_address
