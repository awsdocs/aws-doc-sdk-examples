# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for scenario_getting_started_movies.py.
"""

from decimal import Decimal
import json
from unittest.mock import patch
import boto3
from boto3.dynamodb.conditions import Key
from botocore.exceptions import ClientError
import pytest

import scenario_getting_started_movies as scenario


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_describe_table'),
    ('TestException', 'stub_create_table'),
    ('TestException', 'stub_batch_write_item'),
    ('TestException', 'stub_put_item'),
    ('TestException', 'stub_update_item'),
    ('TestException', 'stub_get_item'),
    ('TestException', 'stub_query'),
    ('TestException', 'stub_scan'),
    ('TestException', 'stub_delete_table'),
])
def test_run_scenario(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    dynamodb_resource = boto3.resource('dynamodb')
    dynamodb_stubber = make_stubber(dynamodb_resource.meta.client)
    table_name = 'getting-started-scenario-test-table'
    movie_file_name = 'test/.test.moviedata.json'
    with open(movie_file_name) as data:
        movie_data = json.load(data, parse_float=Decimal)
    test_rating = 3.3
    test_movie = {
        'title': 'Test Movie Title!', 'year': 2001,
        'info': {'rating': Decimal(str(test_rating)), 'plot': 'Long and boring.'}}
    test_rating_update = 2.2
    test_plot_update = 'Better than I remember.'
    lotr = {'title': "The Lord of the Rings: The Fellowship of the Ring", 'year': 2001}
    year = 1985
    year_range = (1985, 2005)

    inputs = [
        test_movie['title'], test_movie['year'], str(test_rating), test_movie['info']['plot'],
        str(test_rating_update), test_plot_update,
        'y', year, *year_range, 3, 'y', 'y'
    ]
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            dynamodb_stubber.stub_describe_table, table_name,
            error_code='ResourceNotFoundException')
        runner.add(
            dynamodb_stubber.stub_create_table, table_name,
            schema=[
                {'name': 'year', 'key_type': 'HASH', 'type': 'N'},
                {'name': 'title', 'key_type': 'RANGE', 'type': 'S'}],
            throughput={'read': 10, 'write': 10})
        runner.add(dynamodb_stubber.stub_describe_table, table_name)
        runner.add(dynamodb_stubber.stub_put_item, table_name, test_movie)
        runner.add(
            dynamodb_stubber.stub_update_item, table_name,
            {'title': test_movie['title'], 'year': test_movie['year']},
            {'info': {'rating': str(test_rating_update), 'plot': test_plot_update}},
            'UPDATED_NEW',
            expression="set info.rating=:r, info.plot=:p",
            expression_attrs={':r': Decimal(str(test_rating_update)), ':p': test_plot_update})
        runner.add(dynamodb_stubber.stub_batch_write_item, {
            table_name: [{'PutRequest': {'Item': item}} for item in movie_data]})
        runner.add(dynamodb_stubber.stub_get_item, table_name, lotr, lotr)
        runner.add(
            dynamodb_stubber.stub_query, table_name, movie_data,
            key_condition=Key('year').eq(year))
        runner.add(
            dynamodb_stubber.stub_scan, table_name, movie_data,
            filter_expression=Key('year').between(year_range[0], year_range[1]),
            projection_expression="#yr, title, info.rating",
            expression_attrs={"#yr": "year"})
        runner.add(
            dynamodb_stubber.stub_delete_item, table_name,
            {'title': test_movie['title'], 'year': test_movie['year']})
        runner.add(dynamodb_stubber.stub_delete_table, table_name)

    if error_code is None:
        scenario.run_scenario(table_name, movie_file_name, dynamodb_resource)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.run_scenario(table_name, movie_file_name, dynamodb_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_tables(make_stubber, error_code):
    dynamodb_resource = boto3.resource('dynamodb')
    dynamodb_stubber = make_stubber(dynamodb_resource.meta.client)
    movies = scenario.Movies(dynamodb_resource)
    test_tables = [f'table-{index}' for index in range(1, 4)]

    dynamodb_stubber.stub_list_tables(test_tables, error_code=error_code)

    if error_code is None:
        got_tables = movies.list_tables()
        assert [t.name for t in got_tables] == test_tables
    else:
        with pytest.raises(ClientError) as exc_info:
            movies.list_tables()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.integ
def test_run_scenario_integ(monkeypatch):
    dynamodb_resource = boto3.resource('dynamodb')
    table_name = 'getting-started-scenario-test-table'
    movie_file_name = 'test/.test.moviedata.json'
    test_rating = 3.3
    test_movie = {
        'title': 'Test Movie Title!', 'year': 2001,
        'info': {'rating': Decimal(str(test_rating)), 'plot': 'Long and boring.'}}
    test_rating_update = 2.2
    test_plot_update = 'Better than I remember.'
    year = 2001
    year_range = (2001, 2018)

    inputs = [
        test_movie['title'], test_movie['year'], str(test_rating), test_movie['info']['plot'],
        str(test_rating_update), test_plot_update,
        'y', year, *year_range, 3, 'y', 'y'
    ]
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with patch('builtins.print') as mock_print:
        scenario.run_scenario(table_name, movie_file_name, dynamodb_resource)
        mock_print.assert_any_call("\nThanks for watching!")
