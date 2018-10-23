#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Creates a 2048-bit RSA key pair.]
#snippet-keyword:[Amazon Elastic Compute Cloud]
#snippet-keyword:[create_key_pair method]
#snippet-keyword:[Ruby]
#snippet-service:[ec2]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
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

key_name = 'MyGroovyKeyPair'

client = Aws::EC2::Client.new(region: 'us-west-2')
key_pair = client.create_key_pair({key_name: key_name})

# Save it in user's home directory as MyGroovyKeyPair.pem
filename = File.join(Dir.home, key_name + '.pem')
File.open(filename, 'w') { |file| file.write(key_pair.key_material) }
