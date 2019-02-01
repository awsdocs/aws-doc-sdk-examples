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
# snippet-sourcedescription:[kda-python-datagenerator-anomaly.py demonstrates how to generate sample data for the RANDOM_CUT_FOREEST SQL function.]
# snippet-service:[kinesisanalytics]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon Kinesis Data Analytics]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[kinesis.put_record]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-29]
# snippet-sourceauthor:[fletpatr (AWS)]
# snippet-start:[kinesisanalytics.python.datagenerator.anomaly]

import json
import boto3
import random

kinesis = boto3.client('kinesis')

# generate normal heart rate with probability .99
def getNormalHeartRate():
    data = {}
    data['heartRate'] = random.randint(60, 100)
    data['rateType'] = "NORMAL"
    return data
# generate high heart rate with probability .01 (very few)
def getHighHeartRate():
    data = {}
    data['heartRate'] = random.randint(150, 200)
    data['rateType'] = "HIGH"
    return data

while True:
    rnd = random.random()
    if (rnd < 0.01):
        data = json.dumps(getHighHeartRate())
        print(data)
        kinesis.put_record(
                StreamName="ExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")
    else:
        data = json.dumps(getNormalHeartRate())
        print(data)
        kinesis.put_record(
                StreamName="ExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")
 
# snippet-end:[kinesisanalytics.python.datagenerator.anomaly]


