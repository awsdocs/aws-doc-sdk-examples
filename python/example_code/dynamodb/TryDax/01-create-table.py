# snippet-sourcedescription:[ ]
# snippet-service:[dynamodb]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[Code Sample]
# snippet-keyword:[ ]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[ ]
# snippet-sourceauthor:[AWS]
# snippet-start:[dynamodb.Python.TryDax.01-create-table] 

#
#  Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
#  This file is licensed under the Apache License, Version 2.0 (the "License").
#  You may not use this file except in compliance with the License. A copy of
#  the License is located at
# 
#  http://aws.amazon.com/apache2.0/
# 
#  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
#  CONDITIONS OF ANY KIND, either express or implied. See the License for the
#  specific language governing permissions and limitations under the License.
#
#!/usr/bin/env python3
from __future__ import print_function

import os
import amazondax
import botocore.session

region = os.environ.get('AWS_DEFAULT_REGION', 'us-west-2')

session = botocore.session.get_session()
dynamodb = session.create_client('dynamodb', region_name=region) # low-level client

table_name = "TryDaxTable"

params = {
    'TableName' : table_name,
    'KeySchema': [       
        { 'AttributeName': "pk", 'KeyType': "HASH"},    # Partition key
        { 'AttributeName': "sk", 'KeyType': "RANGE" }   # Sort key
    ],
    'AttributeDefinitions': [       
        { 'AttributeName': "pk", 'AttributeType': "N" },
        { 'AttributeName': "sk", 'AttributeType': "N" }
    ],
    'ProvisionedThroughput': {       
        'ReadCapacityUnits': 10, 
        'WriteCapacityUnits': 10
    }
}

# Create the table
dynamodb.create_table(**params)

# Wait for the table to exist before exiting
print('Waiting for', table_name, '...')
waiter = dynamodb.get_waiter('table_exists')
waiter.wait(TableName=table_name)

# snippet-end:[dynamodb.Python.TryDax.01-create-table] 