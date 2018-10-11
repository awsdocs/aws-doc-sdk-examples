#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-service:[AWS Identity and Access Management (IAM)]
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

require 'aws-sdk-iam'  # v2: require 'aws-sdk'

client = Aws::IAM::Client.new(region: 'us-west-2')
iam = Aws::IAM::Resource.new(client: client)

# Let EC2 assume a role
policy_doc = {
  Version:"2012-10-17",
  Statement:[
    {
      Effect:"Allow",
      Principal:{
        Service:"ec2.amazonaws.com"
      },
      Action:"sts:AssumeRole"
  }]
}

role = iam.create_role({
  role_name: 'my_groovy_role',
  assume_role_policy_document: policy_doc.to_json
})

# Give the role full access to S3
role.attach_policy({
  policy_arn: 'arn:aws:iam::aws:policy/AmazonS3FullAccess'
})

# Give the role full access to DynamoDB
role.attach_policy({
  policy_arn: 'arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess'
})
