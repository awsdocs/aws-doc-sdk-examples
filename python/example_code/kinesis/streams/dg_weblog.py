# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Kinesis API to
generate a data stream. This script generates data for the _Parsing Web Logs_
example in the Amazon Kinesis Data Analytics SQL Developer Guide.
"""

# snippet-start:[kinesisanalytics.python.datagenerator.weblog]

import json
import boto3

STREAM_NAME = "ExampleInputStream"


def get_data():
    return {'log': '192.168.254.30 - John [24/May/2004:22:01:02 -0700] '
                   '"GET /icons/apache_pb.gif HTTP/1.1" 304 0'}


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
# snippet-end:[kinesisanalytics.python.datagenerator.weblog]
