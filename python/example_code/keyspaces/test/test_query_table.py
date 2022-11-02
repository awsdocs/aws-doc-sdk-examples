# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
from unittest.mock import MagicMock, patch, mock_open
from botocore.exceptions import ClientError
import pytest

import query


def test_query_table(scenario_data, monkeypatch, capsys, input_mocker):
    execs = ['INSERT INTO', 'SELECT title', 'SELECT *']
    movie = {
        'title': 'test-title', 'year': 1984,
        'info': {'release_date': '1984-10-31T00:00:00Z', 'plot': 'test-plot'}}

    def verify_execute(stmt, parameters):
        stmt_start = execs.pop(0)
        assert stmt.query_string.startswith(stmt_start)
        mm_movie = MagicMock(
            title=movie['title'], year=movie['year'], release_date=movie['info']['release_date'],
            plot=movie['info']['plot'])
        return MagicMock(all=lambda: [mm_movie], one=lambda: mm_movie)

    input_mocker.mock_answers([1])
    scenario_data.scenario.ks_wrapper.table_name = 'test-table'
    monkeypatch.setattr(query, 'SSLContext', lambda x: MagicMock())
    monkeypatch.setattr(query, 'SigV4AuthProvider', lambda x: MagicMock())
    monkeypatch.setattr(query, 'ExecutionProfile', lambda **kw: MagicMock())
    session = MagicMock(execute=verify_execute)
    monkeypatch.setattr(
        query, 'Cluster', lambda x, **kw: MagicMock(connect=lambda x: session))

    with query.QueryManager('test-cert-path', MagicMock(), 'test-ks') as qm:
        with patch('builtins.open', mock_open(read_data=json.dumps([movie]))) as mock_file:
            scenario_data.scenario.query_table(qm)
            mock_file.assert_called_with('../../../resources/sample_files/movies.json', 'r')
            capt = capsys.readouterr()
            assert movie['title'] in capt.out
            assert f"Released: {movie['info']['release_date'].partition('T')[0]}" in capt.out
            assert f"Plot: {movie['info']['plot']}" in capt.out
