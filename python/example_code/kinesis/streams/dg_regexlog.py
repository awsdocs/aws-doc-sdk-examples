# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Kinesis API to
generate a data stream. This script generates data for the _Replacing a Substring
using Regex_ example in the Amazon Kinesis Data Analytics SQL Developer Guide.
"""
# snippet-start:[kinesisanalytics.python.datagenerator.regexlog]

import json
import boto3

STREAM_NAME = 'ExampleInputStream'


def get_data():
    return {
        'LOGENTRY': '203.0.113.24 - - [25/Mar/2018:15:25:37 -0700] '
                    '"GET /index.php HTTP/1.1" 200 125 "-" '
                    '"Mozilla/5.0 [en] Gecko/20100101 Firefox/52.0"'}


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
# snippet-end:[kinesisanalytics.python.datagenerator.regexlog]
