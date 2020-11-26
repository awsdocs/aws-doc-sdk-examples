# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Kinesis API to
generate a data stream. This script generates data for the _Detecting Data Anomalies
and Getting an Explanation_ example in the Amazon Kinesis Data Analytics SQL
Developer Guide.
"""

# snippet-start:[kinesisanalytics.python.datagenerator.anomalyex]

from enum import Enum
import json
import random
import boto3

STREAM_NAME = "ExampleInputStream"


class PressureType(Enum):
    low = 'LOW'
    normal = 'NORMAL'
    high = 'HIGH'


def get_blood_pressure(pressure_type):
    pressure = {'BloodPressureLevel': pressure_type.value}
    if pressure_type == PressureType.low:
        pressure['Systolic'] = random.randint(50, 80)
        pressure['Diastolic'] = random.randint(30, 50)
    elif pressure_type == PressureType.normal:
        pressure['Systolic'] = random.randint(90, 120)
        pressure['Diastolic'] = random.randint(60, 80)
    elif pressure_type == PressureType.high:
        pressure['Systolic'] = random.randint(130, 200)
        pressure['Diastolic'] = random.randint(90, 150)
    else:
        raise TypeError
    return pressure


def generate(stream_name, kinesis_client):
    while True:
        rnd = random.random()
        pressure_type = (
            PressureType.low if rnd < 0.005
            else PressureType.high if rnd > 0.995
            else PressureType.normal)
        blood_pressure = get_blood_pressure(pressure_type)
        print(blood_pressure)
        kinesis_client.put_record(
            StreamName=stream_name,
            Data=json.dumps(blood_pressure),
            PartitionKey="partitionkey")


if __name__ == '__main__':
    generate(STREAM_NAME, boto3.client('kinesis'))
# snippet-end:[kinesisanalytics.python.datagenerator.anomalyex]
