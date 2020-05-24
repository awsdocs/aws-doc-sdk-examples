# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[kda-python-datagenerator-weblog.py demonstrates how to generate sample data for the W3C_LOG_PARSE SQL function.]
# snippet-service:[kinesisanalytics]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Kinesis Data Analytics]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[kinesis.put_record]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-29]
# snippet-sourceauthor:[fletpatr (AWS)]
# snippet-start:[kinesisanalytics.python.datagenerator.weblog]

import json
import boto3
import random

kinesis = boto3.client('kinesis')
def getLog():
    data = {}
    data['log'] = '192.168.254.30 - John [24/May/2004:22:01:02 -0700] "GET /icons/apache_pb.gif HTTP/1.1" 304 0'
    return data

while True:
        data = json.dumps(getLog())
        print(data)
        kinesis.put_record(
                StreamName="ExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")
 
# snippet-end:[kinesisanalytics.python.datagenerator.weblog]


