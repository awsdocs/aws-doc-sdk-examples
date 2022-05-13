# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for update_and_query.py.
"""

from decimal import Decimal
import json
from unittest.mock import patch
import boto3
from boto3.dynamodb.conditions import Key
from botocore.exceptions import ClientError
import pytest

import update_and_query as wrapper


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_update_item'),
    ('TestException', 'stub_query'),
])
def test_usage_demo(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    dynamodb_resource = boto3.resource('dynamodb')
    dynamodb_stubber = make_stubber(dynamodb_resource.meta.client)
    table_name = 'getting-started-scenario-test-table'
    table = dynamodb_resource.Table(table_name)
    movie_file_name = 'test/.test.moviedata.json'
    with open(movie_file_name) as data:
        movie_data = json.load(data, parse_float=Decimal)
    lotr = {'title': "The Lord of the Rings: The Fellowship of the Ring", 'year': 2001}
    rating_increase = 3.3
    one_dir = {'title': 'One Direction: This Is Us', 'year': 2013}
    year = 2000
    title_bounds = ('P', 'V')

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(dynamodb_stubber.stub_describe_table, table_name)
        runner.add(
            dynamodb_stubber.stub_update_item, table_name, lotr, lotr,
            'UPDATED_NEW',
            expression="set info.rating = info.rating + :val",
            expression_attrs={':val': Decimal(str(rating_increase))})
        runner.add(
            dynamodb_stubber.stub_update_item, table_name, lotr, lotr,
            'ALL_NEW',
            expression="remove info.actors[0]",
            condition="size(info.actors) > :num",
            expression_attrs={':num': 5},
            error_code='ConditionalCheckFailedException')
        runner.add(
            dynamodb_stubber.stub_update_item, table_name, lotr, lotr,
            'ALL_NEW',
            expression="remove info.actors[0]",
            condition="size(info.actors) > :num",
            expression_attrs={':num': 2})
        runner.add(
            dynamodb_stubber.stub_delete_item, table_name, one_dir,
            condition="info.rating <= :val",
            expression_attrs={":val": 2},
            error_code='ConditionalCheckFailedException')
        runner.add(
            dynamodb_stubber.stub_delete_item, table_name, one_dir,
            condition="info.rating <= :val",
            expression_attrs={":val": 5})
        runner.add(
            dynamodb_stubber.stub_query, table_name, movie_data,
            key_condition=(
                Key('year').eq(year) &
                Key('title').between(title_bounds[0], title_bounds[1])),
            projection="#yr, title, info.genres, info.actors[0]",
            expression_attrs={"#yr": "year"})

    if error_code is None:
        wrapper.usage_demo(table)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.usage_demo(table)
        assert exc_info.value.response['Error']['Code'] == error_code
