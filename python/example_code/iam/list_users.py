# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# Import the AWS SDK for python
import boto3

# List users with the IAM service resource
resource = boto3.resource('iam')

for user in resource.users.all():
    print("User {} created on {}".format(
        user.user_name, 
        user.create_date)
    )


# List users with the IAM client
client = boto3.client('iam')

done = False
while(not done):
    for user in client.list_users()['Users']:
        print("User {} created on {}".format(
            user['UserName'], 
            user['CreateDate']
        )
    )
    if not client.list_users()['IsTruncated']:
        done = True
