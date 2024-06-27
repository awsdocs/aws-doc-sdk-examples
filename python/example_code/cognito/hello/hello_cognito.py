# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.cognito-idp.HelloCognito]

import boto3

# Create a Cognito Identity Provider client
cognitoidp = boto3.client('cognito-idp')

# Initialize variables for pagination
user_pools = []
response = cognitoidp.list_user_pools(MaxResults=10)
user_pools.extend(response.get('UserPools', []))

# Handle pagination
while 'NextToken' in response:
    response = cognitoidp.list_user_pools(MaxResults=10, NextToken=response['NextToken'])
    user_pools.extend(response.get('UserPools', []))

# Print the list of user pools
print("User Pools for the account:")
if user_pools:
    for pool in user_pools:
        print(f"Name: {pool['Name']}, ID: {pool['Id']}")
else:
    print("No user pools found.")

# snippet-end:[python.example_code.cognito-idp.HelloCognito]
