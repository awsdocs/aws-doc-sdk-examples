# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for stream data generators.
"""

import importlib
import random
import time
import boto3
import pytest


@pytest.mark.parametrize('module_name,repeat', [
    ('streams.dg_referrer', 1),
    ('streams.dg_columnlog', 1),
    ('streams.dg_regexlog', 1),
    ('streams.dg_stagger', 6),
    ('streams.dg_stockticker', 1),
    ('streams.dg_weblog', 1)])
def test_static_generator(make_stubber, monkeypatch, module_name, repeat):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    module = importlib.import_module(module_name)
    stream = module.STREAM_NAME
    data = module.get_data()
    partition_key = 'partitionkey'

    data_list = [data]
    monkeypatch.setattr(module, 'get_data', data_list.pop)
    monkeypatch.setattr(time, 'sleep', lambda x: None)

    for _ in range(repeat):
        kinesis_stubber.stub_put_record(stream, data, partition_key)

    with pytest.raises(IndexError):
        module.generate(stream, kinesis_client)


@pytest.mark.parametrize('module_name,data,rands,rates', [
    ('streams.dg_anomaly',
     [{'heartRate': 75, 'rateType': 'NORMAL'},
      {'heartRate': 175, 'rateType': 'HIGH'}],
     [.5, .001], [75, 175]),
    ('streams.dg_anomalyex',
     [{'BloodPressureLevel': 'LOW', 'Systolic': 60, 'Diastolic': 40},
      {'BloodPressureLevel': 'NORMAL', 'Systolic': 100, 'Diastolic': 70},
      {'BloodPressureLevel': 'HIGH', 'Systolic': 170, 'Diastolic': 120}],
     [.001, .5, .999], [60, 40, 100, 70, 170, 120])
])
def test_random_generator(
        make_stubber, monkeypatch, module_name, data, rands, rates):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    module = importlib.import_module(module_name)
    stream = module.STREAM_NAME
    partition_key = 'partitionkey'

    monkeypatch.setattr(random, 'random', lambda: rands.pop(0))
    monkeypatch.setattr(random, 'randint', lambda x, y: rates.pop(0))

    for item in data:
        kinesis_stubber.stub_put_record(stream, item, partition_key)

    with pytest.raises(IndexError):
        module.generate(stream, kinesis_client)


def test_hotspot_generator(make_stubber, monkeypatch):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    module = importlib.import_module('streams.dg_hotspots')
    stream = module.STREAM_NAME
    field = {'left': 0, 'width': 10, 'top': 0, 'height': 10}
    hotspot_size = 1
    hotspot_weight = 0.2
    batch_size = 3
    rands = [
        0.1, 0.1,  # used to make hotspot (0.9, 0.9)
        0.4, 0.5, 0.5, 0.4, 0.5, 0.5, 0.4, 0.5, 0.5,  # 3 non-hotspot points
        0.1, 0.5, 0.5, 0.1, 0.5, 0.5, 0.1, 0.5, 0.5,   # 3 hotspot points
    ]
    data = [
        [{'x': 5.0, 'y': 5.0, 'is_hot': 'N'} for _ in range(3)],
        [{'x': 1.4, 'y': 1.4, 'is_hot': 'Y'} for _ in range(3)]]
    partition_key = 'partition_key'

    monkeypatch.setattr(random, 'random', lambda: rands.pop(0))
    monkeypatch.setattr(time, 'sleep', lambda x: None)

    for batch in data:
        kinesis_stubber.stub_put_records(stream, batch, partition_key)

    with pytest.raises(IndexError):
        module.generate(
            stream, field, hotspot_size, hotspot_weight, batch_size, kinesis_client)


def test_tworecordtypes_generator(make_stubber, monkeypatch):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    module = importlib.import_module('streams.dg_tworecordtypes')
    stream = module.STREAM_NAME
    partition_key = module.PARTITION_KEY
    order_id = 1
    choices = ['AAAA', 'BBBB']
    stub_prices = [750, 1000, 1000, 600]
    run_ints = [750, 3, 1000, 1000, 600, 0]
    ints = stub_prices + run_ints

    monkeypatch.setattr(random, 'choice', lambda x: choices.pop(0))
    monkeypatch.setattr(random, 'randint', lambda x, y: ints.pop(0))

    data = [module.get_order(order_id, choices[0])]
    data += [module.get_trade(order_id, trade_id, choices[0])
             for trade_id in range(1, 3)]
    data.append(module.get_order(order_id + 1, choices[1]))

    for item in data:
        kinesis_stubber.stub_put_record(stream, item, partition_key)

    with pytest.raises(IndexError):
        module.generate(stream, kinesis_client)
