# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Kinesis API to
generate a data stream. This script generates data for the _Transforming Multiple
Data Types_ example in the Amazon Kinesis Data Analytics SQL Developer Guide.
"""
# snippet-start:[kinesisanalytics.python.datagenerator.tworecordtypes]

import json
import random
import boto3

STREAM_NAME = "OrdersAndTradesStream"
PARTITION_KEY = "partition_key"


def get_order(order_id, ticker):
    return {
        'RecordType': 'Order',
        'Oid': order_id,
        'Oticker': ticker,
        'Oprice': random.randint(500, 10000),
        'Otype': 'Sell'}


def get_trade(order_id, trade_id, ticker):
    return {
        'RecordType': "Trade",
        'Tid': trade_id,
        'Toid': order_id,
        'Tticker': ticker,
        'Tprice': random.randint(0, 3000)}


def generate(stream_name, kinesis_client):
    order_id = 1
    while True:
        ticker = random.choice(['AAAA', 'BBBB', 'CCCC'])
        order = get_order(order_id, ticker)
        print(order)
        kinesis_client.put_record(
            StreamName=stream_name, Data=json.dumps(order),
            PartitionKey=PARTITION_KEY)
        for trade_id in range(1, random.randint(0, 6)):
            trade = get_trade(order_id, trade_id, ticker)
            print(trade)
            kinesis_client.put_record(
                StreamName=stream_name, Data=json.dumps(trade),
                PartitionKey=PARTITION_KEY)
        order_id += 1


if __name__ == '__main__':
    generate(STREAM_NAME, boto3.client('kinesis'))
# snippet-end:[kinesisanalytics.python.datagenerator.tworecordtypes]
