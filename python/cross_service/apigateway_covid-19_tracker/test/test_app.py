# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import datetime
import json
import unittest.mock
import chalice
import pytest

import app
import chalicelib


def test_list_states():
    states = app.list_states()
    assert len(states['states'].split(', ')) == 50


@pytest.mark.parametrize(
    'state,method,body,status_code', [
        ('Washington', 'GET', None, 200),
        ('Emergency', 'GET', None, 400),
        ('Washington', 'PUT', {'state': 'Washington', 'date': '2020-01-01'}, 200),
        ('Washington', 'PUT', {'state': 'Washington', 'date': '2000-01-01'}, 400),
        ('Idaho', 'PUT', {'state': 'Washington', 'date': '2020-01-01'}, 400),
        ('Washington', 'DELETE', {'state': 'Washington', 'date': '2020-01-01'}, 200),
        ('Washington', 'POST', {'state': 'Washington', 'date': '2020-01-01'}, 200),
    ])
def test_state_cases(monkeypatch, state, method, body, status_code):
    test_response = {
        'state': state,
        'date': datetime.date.today().isoformat(),
        'cases': 100
    }

    def check_params(respond=False):
        def _check_params(st, bod=None):
            assert st == state
            if bod is not None:
                assert bod == body
            if respond:
                return test_response
        return _check_params

    monkeypatch.setattr(
        app.app,
        'current_request',
        unittest.mock.MagicMock(method=method, json_body=body))
    monkeypatch.setattr(app.storage, 'get_state_data', check_params(True))
    monkeypatch.setattr(app.storage, 'put_state_data', check_params())
    monkeypatch.setattr(app.storage, 'delete_state_data', check_params())
    monkeypatch.setattr(app.storage, 'post_state_data', check_params())

    if status_code == 200:
        response_json = app.state_cases(state)
        if method == 'GET':
            got_response = json.loads(response_json)
            assert got_response == test_response
    else:
        with pytest.raises(chalice.BadRequestError) as exc_info:
            app.state_cases(state)
        assert exc_info.value.STATUS_CODE == status_code


@pytest.mark.parametrize(
    'state,date,method,status_code', [
        ('Iowa', datetime.date.today().isoformat(), 'GET', 200),
        ('Despair', datetime.date.today().isoformat(), 'GET', 400),
        ('Idaho', '2100-01-01', 'GET', 400),
        ('Florida', '2020-04-20', 'GET', 404),
        ('Florida', '2020-04-20', 'DELETE', 200),
    ])
def test_state_date_cases(monkeypatch, state, date, method, status_code):
    test_response = {
        'state': state,
        'date': datetime.date.today().isoformat(),
        'cases': 100
    }

    def check_params(respond=False):
        def _check_params(st, dt):
            assert st == state
            assert dt == date
            if respond:
                return test_response
        return _check_params

    monkeypatch.setattr(
        app.app, 'current_request', unittest.mock.MagicMock(method=method))
    monkeypatch.setattr(
        app.storage, 'get_state_date_data', check_params(status_code == 200))
    monkeypatch.setattr(app.storage, 'delete_state_date_data', check_params())

    if status_code == 200:
        response_json = app.state_date_cases(state, date)
        if method == 'GET':
            got_response = json.loads(response_json)
            assert got_response == test_response
    else:
        error = chalice.BadRequestError if status_code == 400 else chalice.NotFoundError
        with pytest.raises(error) as exc_info:
            app.state_date_cases(state, date)
        assert exc_info.value.STATUS_CODE == status_code
