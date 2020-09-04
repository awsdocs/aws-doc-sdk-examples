# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Amazon DynamoDB GettingStarted code example.
"""

from decimal import Decimal
import random
from unittest.mock import MagicMock, call

import boto3
from boto3.dynamodb.conditions import Key
from botocore.exceptions import ClientError

import pytest
import MoviesCreateTable
import MoviesDeleteTable
import MoviesItemOps01
import MoviesItemOps02
import MoviesItemOps03
import MoviesItemOps03a
import MoviesItemOps04
import MoviesItemOps05
import MoviesItemOps06
import MoviesListTables
import MoviesLoadData
import MoviesQuery01
import MoviesQuery02
import MoviesScan


def test_create_movie_table(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    dyn_stubber.stub_create_table(
        'Movies',
        schema=[
            {'name': 'year', 'key_type': 'HASH', 'type': 'N'},
            {'name': 'title', 'key_type': 'RANGE', 'type': 'S'}
        ],
        throughput={'read': 10, 'write': 10}
    )

    table = MoviesCreateTable.create_movie_table(dynamodb=dyn)
    assert table.name == 'Movies'
    assert table.table_status == 'CREATING'


def test_delete_movie_table(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    dyn_stubber.stub_delete_table('Movies')

    MoviesDeleteTable.delete_movie_table(dynamodb=dyn)


def test_put_movie(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    movie = {
        'title': '1984',
        'year': 1984,
        'info': {
            'plot': 'Big brother is watching.',
            'rating': 10
        }
    }

    dyn_stubber.stub_put_item('Movies', movie, http_status=200)

    resp = MoviesItemOps01.put_movie(
        movie['title'], movie['year'], movie['info']['plot'], movie['info']['rating'],
        dynamodb=dyn)
    assert resp['ResponseMetadata']['HTTPStatusCode'] == 200


def test_get_movie(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    in_movie = {
        'title': '1984',
        'year': 1984,
        'info': {
            'plot': 'Big brother is watching.',
            'rating': 10
        }
    }

    dyn_stubber.stub_get_item(
        'Movies', {'title': in_movie['title'], 'year': in_movie['year']}, in_movie)

    out_movie = MoviesItemOps02.get_movie(
        in_movie['title'], in_movie['year'], dynamodb=dyn)
    assert out_movie == in_movie


def test_update_movie(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    update = {'info': {
        'rating': Decimal(10),
        'plot': 'Big brother is watching.',
        'actors': ['Bill', 'Steve', 'Larry']
    }}

    dyn_stubber.stub_update_item(
        'Movies', {'year': 1984, 'title': '1984'}, update, 'UPDATED_NEW')

    update_response = MoviesItemOps03.update_movie(
        '1984', 1984, **update['info'], dynamodb=dyn)
    assert update == update_response['Attributes']


def test_update_movie_3a(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    in_movie = {
        'title': '1984',
        'year': 1984,
        'info': {
            'plot': 'Big brother is watching.',
            'rating': 10,
            'actors': ["Jeff", "Bill", "Steve"]
        }
    }

    dyn_stubber.stub_get_item(
        'Movies', {'year': in_movie['year'], 'title': in_movie['title']}, in_movie)
    dyn_stubber.stub_put_item('Movies', in_movie)
    dyn_stubber.stub_get_item(
        'Movies', {'year': in_movie['year'], 'title': in_movie['title']}, in_movie)

    update_response = MoviesItemOps03a.update_movie(
        '1984', 1984, **in_movie['info'], dynamodb=dyn)
    assert in_movie == update_response


def test_increase_rating(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    update_key = {'title': '1984', 'year': 1984}
    update = {
        'info': {
            'plot': 'Big brother is watching.',
            'rating': 10,
            'actors': ["Jeff", "Bill", "Steve"]
        }
    }

    dyn_stubber.stub_update_item(
        'Movies', update_key, update, 'UPDATED_NEW',
        expression='set info.rating = info.rating + :val',
        expression_attrs={':val': Decimal(1)})

    update_response = MoviesItemOps04.increase_rating('1984', 1984, 1, dynamodb=dyn)
    assert update == update_response['Attributes']


@pytest.mark.parametrize(
    "error_code", [None, 'ConditionalCheckFailedException', 'TestException'])
def test_remove_actors(make_stubber, error_code):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    update_key = {'title': '1984', 'year': 1984}

    dyn_stubber.stub_update_item(
        'Movies', update_key, {}, 'UPDATED_NEW',
        expression='remove info.actors[0]',
        condition='size(info.actors) > :num',
        expression_attrs={':num': 3},
        error_code=error_code)

    if not error_code or error_code == 'ConditionalCheckFailedException':
        MoviesItemOps05.remove_actors('1984', 1984, 3, dynamodb=dyn)
    else:
        with pytest.raises(ClientError) as exc_info:
            MoviesItemOps05.remove_actors('1984', 1984, 3, dynamodb=dyn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    "error_code", [None, 'ConditionalCheckFailedException', 'TestException'])
def test_delete_underrated_movie(make_stubber, error_code):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    delete_key = {'title': '1984', 'year': 1984}

    dyn_stubber.stub_delete_item(
        'Movies', delete_key,
        condition='info.rating <= :val',
        expression_attrs={':val': 10},
        error_code=error_code)

    if not error_code or error_code == 'ConditionalCheckFailedException':
        MoviesItemOps06.delete_underrated_movie(
            delete_key['title'], delete_key['year'], 10, dynamodb=dyn)
    else:
        with pytest.raises(ClientError) as exc_info:
            MoviesItemOps06.delete_underrated_movie(
                delete_key['title'], delete_key['year'], 10, dynamodb=dyn)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_print_tables(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    table_names = ['Test Table', 'Untested Table', 'Occasional Table', 'Oak Table']
    dyn_stubber.stub_list_tables(table_names)

    MoviesListTables.print_tables(dynamodb=dyn)


def test_load_movies(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    movies = [{
        'title': f'Test {index}',
        'year': random.randint(1900, 2020),
        'info': f'An extra {index} units of informational data.'
    } for index in range(1, 100)]

    for movie in movies:
        dyn_stubber.stub_put_item('Movies', movie)

    MoviesLoadData.load_movies(movies, dynamodb=dyn)


def test_query_movies(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    year = 1969
    stub_movies = [{
        'title': f'Test {index}',
        'year': year,
        'info': f'An extra {index} units of informational data.'
    } for index in range(1, 10)]

    dyn_stubber.stub_query('Movies', stub_movies, key_condition=Key('year').eq(year))

    movies = MoviesQuery01.query_movies(year, dynamodb=dyn)
    assert movies == stub_movies


def test_query_and_project_movies(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    year = 1975
    title_range = ('K', 'M')
    stub_movies = [{
        'title': f'Mystery {index}',
        'year': year,
        'info': {'actors': [f'Actor {index}'], 'genres': [f'Genre {index}']}
    } for index in range(1, 10)]

    dyn_stubber.stub_query(
        'Movies',
        stub_movies,
        key_condition=Key('year').eq(year) &
                      Key('title').between(title_range[0], title_range[1]),
        projection="#yr, title, info.genres, info.actors[0]",
        expression_attrs={"#yr": "year"}
    )

    movies = MoviesQuery02.query_and_project_movies(year, title_range, dynamodb=dyn)
    assert movies == stub_movies


def test_scan_movies(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    stub_movies = [{
        'title': f'Test {index}',
        'year': 1905,
        'info': f'An extra {index} units of informational data.'
    } for index in range(1, 100)]
    query_range= (1900, 1910)

    scan_kwargs = {
        'filter_expression': Key('year').between(*query_range),
        'projection_expression': "#yr, title, info.rating",
        'expression_attrs': {"#yr": "year"}
    }

    key = {'title': {'S': 'Last title'}, 'year': {'N': '2000'}}
    dyn_stubber.stub_scan('Movies', stub_movies[:50], **scan_kwargs, last_key=key)
    scan_kwargs['start_key'] = key
    dyn_stubber.stub_scan('Movies', stub_movies[50:], **scan_kwargs)

    callback = MagicMock()
    MoviesScan.scan_movies(query_range, callback, dynamodb=dyn)
    callback.assert_has_calls([call(stub_movies[:50]), call(stub_movies[50:])])
