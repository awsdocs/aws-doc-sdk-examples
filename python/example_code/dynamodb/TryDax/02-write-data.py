# snippet-sourcedescription:[ ]
# snippet-service:[dynamodb]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[Code Sample]
# snippet-keyword:[ ]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[ ]
# snippet-sourceauthor:[AWS]
# snippet-start:[dynamodb.Python.TryDax.02-write-data] 

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

some_data = 'X' * 1000
pk_max = 10
sk_max = 10

for ipk in range(1, pk_max+1):
    for isk in range(1, sk_max+1):
        params = {
            'TableName': table_name,
            'Item': {
                "pk": {'N': str(ipk)},
                "sk": {'N': str(isk)},
                "someData": {'S': some_data}
            }
        }

        dynamodb.put_item(**params)
        print("PutItem ({}, {}) suceeded".format(ipk, isk))

# snippet-end:[dynamodb.Python.TryDax.02-write-data] 