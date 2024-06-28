# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.dynamodb.hello_dynamodb]

import boto3

# Create a DynamoDB client using the default credentials and region
dynamodb = boto3.client("dynamodb")

# List the tables in the current AWS account
print("Here are the DynamoDB tables in your account:")

# Use pagination to list all tables, limiting the number of results per page
table_names = []
last_table_name = None
while True:
    if last_table_name:
        response = dynamodb.list_tables(
            ExclusiveStartTableName=last_table_name, Limit=10
        )
    else:
        response = dynamodb.list_tables(Limit=10)

    for table_name in response.get("TableNames", []):
        print(f"- {table_name}")
        table_names.append(table_name)

    last_table_name = response.get("LastEvaluatedTableName")
    if not last_table_name:
        break

if not table_names:
    print("You don't have any DynamoDB tables in your account.")
else:
    print(f"\nFound {table_names.length} tables.")

# snippet-end:[python.dynamodb.hello_dynamodb]
