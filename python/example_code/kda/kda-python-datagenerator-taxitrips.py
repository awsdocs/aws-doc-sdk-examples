# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[kda-python-datagenerator-taxitrips.py demonstrates how to generate sample data for Amazon Kinesis Data Analytics Java applications.]
# snippet-service:[kinesisanalytics]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Kinesis Data Analytics]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2020-01-13]
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
# snippet-start:[kinesisanalytics.python.datagenerator.taxitrips]


import json
import boto3
import random
import datetime


kinesis = boto3.client('kinesis')
tripId = 0

def getTrip():
    
    data = {}
    now = datetime.datetime.now()
    pickup = now - datetime.timedelta(minutes=random.random() * 120)
    s = "%Y-%m-%dT%H:%M:%SZ"
    str_now = now.strftime(s)
    str_pickup = pickup.strftime(s)

    data['vendor_id'] = int(random.random() * 10)
    data['pickup_datetime'] = str_pickup
    data['dropoff_datetime'] = str_now
    data['passenger_count'] = int(random.random() * 3)
    data['trip_distance'] = int(random.random() * 20)
    data['ratecode_id'] = int(random.random() * 3)
    data['store_and_fwd_flag'] = int(random.random() * 2)
    data['pickup_location_id'] = int(random.random() * 1000)
    data['dropoff_location_id'] = int(random.random() * 1000)
    data['payment_type'] = int(random.random() * 3)
    data['fare_amount'] = round(random.random() * 50, 2)
    data['extra'] = round(random.random() * 20, 2)
    data['mta_tax'] = round(random.random() * 10, 2)
    data['tip_amount'] = round(random.random() * 10, 2)
    data['tolls_amount'] = round(random.random() * 10, 2)
    data['improvement_surcharge'] = round(random.random() * 10, 2)
    data['total_amount'] = round(random.random() * 100, 2)
    data['trip_id'] = tripId
    data['type'] = "trip"

    return data

while True:
        data = json.dumps(getTrip())
        print(data)
        kinesis.put_record(
                StreamName="ExampleInputStream",
                Data=data,
                PartitionKey="partitionkey")

# snippet-end:[kinesisanalytics.python.datagenerator.taxitrips]
