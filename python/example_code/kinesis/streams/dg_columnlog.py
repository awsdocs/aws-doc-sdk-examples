# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Kinesis API to
generate a data stream. This script generates data for the _Split Strings into
Multiple Fields_ example in the Amazon Kinesis Data Analytics SQL Developer Guide.
"""

# snippet-start:[kinesisanalytics.python.datagenerator.columnlog]

import json
import boto3

STREAM_NAME = "ExampleInputStream"


def get_data():
    return {
        'Col_A': 'a',
        'Col_B': 'b',
        'Col_C': 'c',
        'Col_E_Unstructured': 'x,y,z'}


def generate(stream_name, kinesis_client):
    while True:
        data = get_data()
        print(data)
        kinesis_client.put_record(
            StreamName=stream_name,
            Data=json.dumps(data),
            PartitionKey="partitionkey")


if __name__ == '__main__':
    generate(STREAM_NAME, boto3.client('kinesis'))
# snippet-end:[kinesisanalytics.python.datagenerator.columnlog]
