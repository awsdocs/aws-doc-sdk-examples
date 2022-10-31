# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import os
from unittest.mock import mock_open, patch, MagicMock
import requests

from query import QueryManager


def test_ensure_tls_cert_download(scenario_data, monkeypatch, input_mocker):
    input_mocker.mock_answers('y')
    monkeypatch.setattr(os.path, "join", lambda x, y: f"test-path/{y}")
    monkeypatch.setattr(os.path, "exists", lambda x: False)
    monkeypatch.setattr(requests, "get", lambda x: MagicMock(text="Test cert"))

    with patch('builtins.open', mock_open()) as mock_file:
        cert_path = scenario_data.scenario.ensure_tls_cert()
        mock_file.assert_called_with(f'test-path/{QueryManager.DEFAULT_CERT_FILE}', 'w')
        assert cert_path == f'test-path/{QueryManager.DEFAULT_CERT_FILE}'


def test_ensure_tls_cert_custom(scenario_data, monkeypatch, input_mocker):
    input_mocker.mock_answers(['n', 'custom-cert-file'])
    monkeypatch.setattr(os.path, "join", lambda x, y: f"test-path/{y}")
    monkeypatch.setattr(os.path, "exists", lambda x: False)

    cert_path = scenario_data.scenario.ensure_tls_cert()
    assert cert_path == 'custom-cert-file'


def test_ensure_tls_cert_exists(scenario_data, monkeypatch, input_mocker):
    monkeypatch.setattr(os.path, "join", lambda x, y: f"test-path/{y}")
    monkeypatch.setattr(os.path, "exists", lambda x: True)

    cert_path = scenario_data.scenario.ensure_tls_cert()
    assert cert_path == f'test-path/{QueryManager.DEFAULT_CERT_FILE}'
