# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[kda-python-datagenerator-anomalyex.py demonstrates how to generate sample data for the RANDOM_CUT_FOREST_WITH_EXPLANATION SQL function.]
# snippet-service:[kinesisanalytics]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon Kinesis Data Analytics]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[kinesis.put_record]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-29]
# snippet-sourceauthor:[fletpatr (AWS)]

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

# snippet-start:[kinesisanalytics.python.datagenerator.anomalyex]

import json
import boto3
import random

kinesis = boto3.client('kinesis')

# Generate normal blood pressure with a 0.995 probability
def getNormalBloodPressure():
    data = {}
    data['Systolic'] = random.randint(90, 120)
    data['Diastolic'] = random.randint(60, 80)
    data['BloodPressureLevel'] = 'NORMAL'
    return data
    
# Generate high blood pressure with probability 0.005
def getHighBloodPressure():
    data = {}
    data['Systolic'] = random.randint(130, 200)
    data['Diastolic'] = random.randint(90, 150)
    data['BloodPressureLevel'] = 'HIGH'
    return data
    
# Generate low blood pressure with probability 0.005
def getLowBloodPressure():
    data = {}
    data['Systolic'] = random.randint(50, 80)
    data['Diastolic'] = random.randint(30, 50)
    data['BloodPressureLevel'] = 'LOW'
    return data

while True:
    rnd = random.random()
    if (rnd < 0.005):
        data = json.dumps(getLowBloodPressure())
        print(data)
        kinesis.put_record(
                StreamName="BloodPressureExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")
    elif (rnd > 0.995):
        data = json.dumps(getHighBloodPressure())
        print(data)
        kinesis.put_record(
                StreamName="BloodPressureExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")
    else:
        data = json.dumps(getNormalBloodPressure())
        print(data)
        kinesis.put_record(
                StreamName="BloodPressureExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")
 
# snippet-end:[kinesisanalytics.python.datagenerator.anomalyex]
