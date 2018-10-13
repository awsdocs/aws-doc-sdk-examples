#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-service:[Amazon S3]
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

require 'aws-sdk-s3'  # v2: require 'aws-sdk'

args_list = {}
args_list[:bucket] = 'my-bucket'
args_list[:key]    = 'my-item'

# Where we are getting the source to copy from
args_list[:copy_source] = 'my-bucket/my-item'

# The acl can be any of:
# private, public-read, public-read-write, authenticated-read, aws-exec-read, bucket-owner-read, bucket-owner-full-control
args_list[:acl] = 'public-read'

# The encryption can be any of:
# AES256, aws:kms
args_list[:server_side_encryption] = 'AES256'

# The storage_class can be any of:
# STANDARD, REDUCED_REDUNDANCY, STANDARD_IA
args_list[:storage_class] = 'REDUCED_REDUNDANCY'

client = Aws::S3::Client.new(region: 'us-west-2')

client.copy_object(args_list)
