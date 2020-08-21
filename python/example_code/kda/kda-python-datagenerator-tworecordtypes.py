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
# snippet-sourcedescription:[kda-python-datagenerator-tworecordtypes.py demonstrates how to generate sample data for the Transforming Multiple Data Types example.]
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
# snippet-start:[kinesisanalytics.python.datagenerator.tworecordtypes]


import json
import boto3
import random

kinesis = boto3.client('kinesis')

def getOrderData(orderId, ticker):
    data = {}
    data['RecordType'] = "Order"
    data['Oid'] = orderId
    data['Oticker'] = ticker
    data['Oprice'] = random.randint(500, 10000)
    data['Otype'] = "Sell"
    return data

def getTradeData(orderId, tradeId, ticker, tradePrice):
    data = {}
    data['RecordType'] = "Trade"
    data['Tid'] = tradeId
    data['Toid'] = orderId
    data['Tticker'] = ticker
    data['Tprice'] = tradePrice
    return data

x = 1
while True:
    #rnd = random.random()
    rnd = random.randint(1,3)
    if rnd == 1:
        ticker = "AAAA"
    elif rnd == 2:
        ticker = "BBBB"
    else:
        ticker = "CCCC"
    data = json.dumps(getOrderData(x, ticker))
    kinesis.put_record(StreamName="OrdersAndTradesStream", Data=data, PartitionKey="partitionkey")
    print(data)
    tId = 1
    for y in range (0, random.randint(0,6)):
        tradeId = tId
        tradePrice = random.randint(0, 3000)
        data2 = json.dumps(getTradeData(x, tradeId, ticker, tradePrice));
        kinesis.put_record(StreamName="OrdersAndTradesStream", Data=data2, PartitionKey="partitionkey")
        print(data2)
        tId+=1
        
    x+=1
 
# snippet-end:[kinesisanalytics.python.datagenerator.tworecordtypes]


