# snippet-sourcedescription:[ ]
# snippet-service:[dynamodb]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[ ]
# snippet-sourceauthor:[AWS]

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

# snippet-start:[dynamodb.Python.TryDax.02-write-data]

import os
import boto3

table_name = 'TryDaxTable'
some_data = 'X' * 1000
pk_max = 10
sk_max = 10

region = os.environ.get('AWS_DEFAULT_REGION', 'us-west-2')
dynamodb = boto3.client('dynamodb', region_name=region)
for ipk in range(1, pk_max+1):
    for isk in range(1, sk_max+1):
        params = {
            'TableName': table_name,
            'Item': {
                'pk': {'N': str(ipk)},
                'sk': {'N': str(isk)},
                'someData': {'S': some_data}
            }
        }

        dynamodb.put_item(**params)
        print(f'PutItem ({ipk}, {isk}) succeeded')

# snippet-end:[dynamodb.Python.TryDax.02-write-data]
