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
# snippet-sourcedescription:[kda-python-datagenerator-stagger.py demonstrates how to generate sample data for the Stagger Window example.]
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
# snippet-start:[kinesisanalytics.python.datagenerator.stagger]

import json
import boto3
import random
import datetime
import time

kinesis = boto3.client('kinesis')
def getData():
    data = {}
    now = datetime.datetime.utcnow() - datetime.timedelta(seconds=10)
    str_now = now.isoformat()
    data['EVENT_TIME'] = str_now
    data['TICKER'] = random.choice(['AAPL', 'AMZN', 'MSFT', 'INTC', 'TBV'])
    return data

while True:
    data = json.dumps(getData())
    # Send six records, ten seconds apart, with the same event time and ticker
    for x in range(0, 6):
        print(data)
        kinesis.put_record(
                StreamName="ExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")
        time.sleep(10)
 
# snippet-end:[kinesisanalytics.python.datagenerator.stagger]


