# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

"""
Tests the api_report module.

Test metadata Yaml files are stored in the test_api_report_yamls subfolder.
"""

from datetime import date
import os
from unittest.mock import patch, mock_open, Mock

# pylint: disable=redefined-outer-name
import pytest

import api_report


def mock_walk(examples_folder):
    """Mock the os.walk function to return a single file."""
    folder_list = []
    file_list = ["metadata.yaml"]
    return [("", folder_list, file_list)]


def verify_example_data(example):
    """Verifies basic correctness of example data."""
    if 'description' in example:
        assert isinstance(example['description'], str)
    assert isinstance(example['created'], date)
    assert len(example['files']) >= 1
    file = example['files'][0]
    assert isinstance(file['path'], str)
    name, ext = os.path.splitext(file['path'])
    assert name
    assert ext.lstrip('.') in api_report.EXT_LOOKUP
    assert len(file['apis']) >= 1
    api = file['apis'][0]
    assert isinstance(api['service'], str)
    assert len(api['operations']) >= 1
    assert isinstance(api['operations'][0], str)


def test_gather_data_complex(monkeypatch):
    """Reads complex metadata and verifies the output."""
    def mock_join(folder_name, file_name):
        return "test_api_report_yamls/complex_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk)
    monkeypatch.setattr(os.path, "join", mock_join)

    examples = api_report.gather_data("test_api_report_yamls")
    assert len(examples) == 1
    verify_example_data(examples[0])


def test_gather_data_multi_file(monkeypatch):
    """Reads metadata that contains multiple documents in one Yaml file."""
    def mock_join(folder_name, file_name):
        return "test_api_report_yamls/multi_file_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk)
    monkeypatch.setattr(os.path, "join", mock_join)

    examples = api_report.gather_data("test_api_report_yamls")
    assert len(examples) == 3
    for example in examples:
        verify_example_data(example)


def test_gather_data_bad_yaml(monkeypatch):
    """Reads a file that contains incorrect Yaml and verifies that it rejects the
    file but does not raise an exception."""
    def mock_join(folder_name, file_name):
        return "test_api_report_yamls/bad_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk)
    monkeypatch.setattr(os.path, "join", mock_join)

    examples = api_report.gather_data("test_api_report_yamls")
    assert len(examples) == 0


def test_gather_data_capitalization(monkeypatch):
    """Tests that capitalization does not break reading a metadata.yaml file."""
    def mock_walk_caps(examples_folder):
        folder_list = []
        file_list = ["METAdata.yaml"]
        return [("", folder_list, file_list)]

    def mock_join(folder_name, file_name):
        return "test_api_report_yamls/complex_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk_caps)
    monkeypatch.setattr(os.path, "join", mock_join)

    examples = api_report.gather_data("test_api_report_yamls")
    assert len(examples) == 1


def test_gather_data_no_metadata(monkeypatch):
    """Tests that a file list with no metadata.yaml in it does not fail."""
    def mock_walk_no_meta(examples_folder):
        folder_list = []
        file_list = ["something_else.yaml", "actual_file.py"]
        return [("", folder_list, file_list)]

    monkeypatch.setattr(os, "walk", mock_walk_no_meta)

    examples = api_report.gather_data("test_api_report_yamls")
    assert len(examples) == 0


@pytest.fixture
def mock_opened_file():
    """Mocks opening a file in a context manager."""
    with patch('builtins.open', mock_open()) as mock_file:
        yield mock_file


def test_write_empty_report(mock_opened_file):
    """Tests that writing an empty report does not cause failure."""
    api_report.write_report([], 'test.csv')
    mock_opened_file.assert_called_with('test.csv', 'w')
    handle = mock_opened_file()
    handle.write.assert_called()


def test_write_single_report(mock_opened_file):
    """Tests writing a single report."""
    created = date(2020, 2, 1)
    path = 'test_path.py'
    service = 'testsvc'
    operation = 'test_operation'
    api_report.write_report([{
        'metadata_path': 'metadata.yaml',
        'created': created,
        'files': [{
            'path': path,
            'apis': [{
                'service': service,
                'operations': [operation]
            }]
        }]
    }], 'test.csv')
    handle = mock_opened_file()
    handle.write.assert_called_with(
        "Created,File,Language,Service,Operation\n" +
        ",".join([str(created), api_report.GITHUB_URL + path,
                  'Python', service, operation]))


def test_write_multi_report(mock_opened_file):
    """Tests writing a list of reports."""
    examples = []
    lines = []
    for count in range(1, 5):
        created = date(2020, 2, count)
        path = f'test_path_{count}.cpp'
        service = f'testsvc{count}'
        operation = f'test_operation_{count}'
        examples.append({
            'metadata_path': 'metadata.yaml',
            'created': created,
            'files': [{
                'path': path,
                'apis': [{
                    'service': service,
                    'operations': [operation]
                }]
            }]
        })
        lines.append(','.join([str(created), api_report.GITHUB_URL + path,
                               'C++', service, operation]))

    api_count = api_report.write_report(examples, 'test.csv')
    assert api_count == count
    handle = mock_opened_file()
    handle.write.assert_called_with(
        "Created,File,Language,Service,Operation\n" +
        "\n".join(lines)
    )


def test_write_report_missing_key(mock_opened_file):
    """Tests that writing a report for an example with a missing key skips the
    example but does not otherwise fail."""
    created = date(2020, 2, 1)
    path = 'test_path.py'
    service = 'testsvc'
    operation = 'test_operation'

    api_count = api_report.write_report([{
        'metadata_path': 'metadata.yaml',
        'created': created,
        'wrong_key': [{
            'path': path,
            'apis': [{
                'service': service,
                'operations': [operation]
            }]
        }]
    }, {
        'metadata_path': 'metadata.yaml',
        'created': created,
        'files': [{
            'path': path,
            'apis': [{
                'service': service,
                'operations': [operation]
            }]
        }]

    }], 'test.csv')
    assert api_count == 1


@pytest.mark.parametrize(
    "examples,expected_api_count", [
        ([{
            'metadata_path': 'metadata.yaml',
            'created': '2020-02-01',
            'files': [{
                'path': 'test_path.py',
                'apis': [{
                    'service': 'testsvc',
                    'operations': ['test_op']
                }]
            }]
        }], 1),
        ([{
            'metadata_path': 'metadata.yaml',
            'created': '2020-02-01',
            'files': [{
                'path': 'test_path.py',
                'apis': [{
                    'service': 'testsvc',
                    'operations': ['test_op1', 'test_op2', 'test_op3']
                }]
            }]
        }], 3),
        ([{
            'metadata_path': 'metadata.yaml',
            'created': '2020-02-01',
            'files': [{
                'path': 'test_path.py',
                'apis': [{
                    'service': 'testsvc1',
                    'operations': ['test_op1', 'test_op2', 'test_op3']
                }, {
                    'service': 'testsvc2',
                    'operations': ['test_op1', 'test_op2']
                }]
            }]
        }], 5),
        ([{
            'metadata_path': 'metadata.yaml',
            'created': '2020-02-01',
            'files': [{
                'path': 'test_path.py',
                'apis': [{
                    'service': 'testsvc1',
                    'operations': ['test_op1', 'test_op2', 'test_op3']
                }, {
                    'service': 'testsvc2',
                    'operations': ['test_op1', 'test_op2']
                }]
            }],
        }, {
            'metadata_path': 'metadata.yaml',
            'created': '2020-02-01',
            'files': [{
                'path': 'test_path.cpp',
                'apis': [{
                    'service': 'testsvc1',
                    'operations': ['test_op1', 'test_op2', 'test_op3']
                }, {
                    'service': 'testsvc2',
                    'operations': ['test_op1', 'test_op2']
                }]
            }]
        }], 10),
        ([{
            'metadata_path': 'metadata.yaml',
            'created': '2020-02-01',
            'files': [{
                'path': 'test_path.py',
                'apis': [{
                    'service': 'testsvc1',
                    'operations': ['test_op1', 'test_op2', 'test_op3']
                }, {
                    'service': 'testsvc2',
                    'operations': ['test_op1', 'test_op2']
                }]
            }],
        }, {
            'metadata_path': 'metadata.yaml',
            'created': '2020-02-01',
            'files': [{
                'path': 'test_path.py',
                'apis': [{
                    'service': 'testsvc1',
                    'operations': ['test_op1', 'test_op5', 'test_op6']
                }, {
                    'service': 'testsvc2',
                    'operations': ['test_op1', 'test_op3']
                }]
            }]
        }], 8),
    ]
)
def test_count_apis(mock_opened_file, examples, expected_api_count):
    """Tests several scenarios to verify that unique APIs are counted correctly."""
    api_count = api_report.write_report(examples, 'test.csv')
    assert api_count == expected_api_count
