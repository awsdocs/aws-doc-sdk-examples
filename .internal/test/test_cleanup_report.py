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
Tests the cleanup_report module.

Test metadata Yaml files are stored in the test_cleanup_report_yamls subfolder.
"""

import os
from unittest.mock import patch, mock_open, call

# pylint: disable=redefined-outer-name
import pytest

import cleanup_report


def mock_walk(file_list):
    def _mock_walk(examples_folder, topdown=True):
        """Mock the os.walk function to return the specified files."""
        folder_list = []
        return [("", folder_list, file_list)]
    return _mock_walk


def verify_example_data(example):
    """Verifies basic correctness of example data."""
    assert len(example['files']) >= 1
    file = example['files'][0]
    assert isinstance(file['path'], str)
    name, ext = os.path.splitext(file['path'])
    assert name
    assert ext.lstrip('.') in cleanup_report.EXT_LOOKUP
    assert 'services' not in file or len(file['services']) >= 1
    service = file['services'][0]
    assert isinstance(service, str)


@pytest.mark.parametrize("file_list", [
    ["metadata.yaml"],
    ["metadata.yaml", "start_the_things.py", "something_random.py"],
    ["metadata.yaml", "start_the_things.py", "data/send_the_things.py",
     "test/test_the_things.py"],
    ["metadata.yaml", "something.py", "something.cs", "something.js", "something.java"],
    ["metadata.yaml", "something.ts", "something.xml", "something.sln", "something.txt"]
])
def test_gather_data_complex(monkeypatch, file_list):
    """Reads complex metadata and verifies the output."""
    def mock_join(folder_name, file_name):
        return "test_cleanup_report_yamls/complex_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk(file_list))
    monkeypatch.setattr(os.path, "join", mock_join)

    examples, files = cleanup_report.gather_data("test_cleanup_report_yamls")
    assert len(examples) == 1
    verify_example_data(examples[0])
    assert len(files) == len([
        f for f in file_list
        if os.path.splitext(f)[1].lstrip('.') in cleanup_report.EXT_LOOKUP
    ])


def test_gather_data_multi_file(monkeypatch):
    """Reads metadata that contains multiple documents in one Yaml file."""
    def mock_join(folder_name, file_name):
        return "test_cleanup_report_yamls/multi_file_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk(["metadata.yaml"]))
    monkeypatch.setattr(os.path, "join", mock_join)

    examples = cleanup_report.gather_data("test_cleanup_report_yamls")[0]
    assert len(examples) == 3
    for example in examples:
        verify_example_data(example)


def test_gather_data_bad_yaml(monkeypatch):
    """Reads a file that contains incorrect Yaml and verifies that it rejects the
    file but does not raise an exception."""
    def mock_join(folder_name, file_name):
        return "test_cleanup_report_yamls/bad_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk(["metadata.yaml"]))
    monkeypatch.setattr(os.path, "join", mock_join)

    examples = cleanup_report.gather_data("test_cleanup_report_yamls")[0]
    assert len(examples) == 0


def test_gather_data_capitalization(monkeypatch):
    """Tests that capitalization does not break reading a metadata.yaml file."""
    def mock_walk_caps(examples_folder, topdown):
        folder_list = []
        file_list = ["METAdata.yaml"]
        return [("", folder_list, file_list)]

    def mock_join(folder_name, file_name):
        return "test_cleanup_report_yamls/complex_metadata.yaml"

    monkeypatch.setattr(os, "walk", mock_walk_caps)
    monkeypatch.setattr(os.path, "join", mock_join)

    examples = cleanup_report.gather_data("test_cleanup_report_yamls")[0]
    assert len(examples) == 1


def test_gather_data_no_metadata(monkeypatch):
    """Tests that a file list with no metadata.yaml in it does not fail."""
    def mock_walk_no_meta(examples_folder, topdown):
        folder_list = []
        file_list = ["something_else.yaml", "actual_file.py"]
        return [("", folder_list, file_list)]

    monkeypatch.setattr(os, "walk", mock_walk_no_meta)

    examples, files = cleanup_report.gather_data("test_cleanup_report_yamls")
    assert len(examples) == 0
    assert len(files) == 1


@pytest.fixture
def mock_opened_file():
    """Mocks opening a file in a context manager."""
    with patch('builtins.open', mock_open()) as mock_file:
        yield mock_file


def make_expected_calls(example_count, clean_file_count, total_file_count, lines):
    """Make a list of calls expected to be made by the report function."""
    return [
        call(f"Total number of examples: {example_count}.\n"),
        call(f"Total number of cleaned files: {clean_file_count}.\n"),
        call(f"Total number of files: {total_file_count}.\n"),
        call(f"Percent clean: {clean_file_count/total_file_count:.0%}."),
        call("\n"),
        call(
            "File,Language,Service\n" + "\n".join(lines))
    ]


def test_write_empty_report(mock_opened_file):
    """Tests that writing an empty report does not cause failure."""
    cleanup_report.write_report([], [], 'test.csv')
    mock_opened_file.assert_called_with('test.csv', 'w')
    handle = mock_opened_file()
    handle.write.assert_called()


@pytest.mark.parametrize("summarize", [True, False])
def test_write_single_report(mock_opened_file, summarize):
    """Tests writing a report of a single file."""
    path = 'test_path.py'
    service = 'testsvc'
    cleanup_report.write_report([{
        'metadata_path': 'metadata.yaml',
        'files': [{
            'path': path,
            'services': [service]
        }]
    }], [cleanup_report.make_github_url('', path)], 'test.csv', summarize)
    handle = mock_opened_file()
    calls = make_expected_calls(
        1, 1, 1,
        [','.join([cleanup_report.GITHUB_URL + path, 'Python', service])]
    )
    if summarize:
        calls = calls[:-2]
    handle.write.assert_has_calls(calls)


def test_write_report_missing_file(mock_opened_file):
    """Tests writing a report where a file is listed in the metadata but does
    not exist in the repo."""
    handle = mock_opened_file()
    cleanup_report.write_report([{
        'metadata_path': 'metadata.yaml',
        'files': [
            {'path': 'example_path1.py', 'services': ['example_svc']},
            {'path': 'example_path2.py', 'services': ['example_svc']},
            {'path': 'example_path3.py', 'services': ['example_svc']}
        ]
    }], [
        cleanup_report.make_github_url('', 'example_path1.py'),
        cleanup_report.make_github_url('', 'example_path3.py')
    ], 'test.csv')
    calls = make_expected_calls(
        1, 2, 2, [
            ','.join([cleanup_report.make_github_url(
                '', 'example_path1.py'), 'Python', 'example_svc']),
            ','.join([cleanup_report.make_github_url(
                '', 'example_path3.py'), 'Python', 'example_svc'])
        ]
    )
    handle.write.assert_has_calls(calls)


@pytest.mark.parametrize("clean_files,repo_files,dirty", [
    (['example_path1.py'],
     ['example_path1.py', 'example_path2.py', 'example_path3.py'], True),
    (['example_path1.py'],
     ['example_path1.py', 'example_path2.py', 'example_path3.py'], False),
    (['Example_Path1.py'],
     ['example_path1.py', 'example_path2.py', 'example_path3.py'], True),
    (['example_path2.py'],
     ['Example_Path1.py', 'Example_Path2.py', 'Example_Path3.py'], True),
    (['example_path2.py'], ['Example_Path2.py'], True),
])
def test_write_report_unclean_files(mock_opened_file, clean_files, repo_files, dirty):
    """Tests writing a report when files exist in the repo that have not been
    cleaned."""
    handle = mock_opened_file()
    cleanup_report.write_report([{
        'metadata_path': 'metadata.yaml',
        'files': [
            {'path': file, 'services': ['example_svc']}
            for file in clean_files
        ]
    }], [
        cleanup_report.make_github_url('', file)
        for file in repo_files
    ], 'test.csv', summarize=False, dirty=dirty)
    calls = make_expected_calls(
        1, len(clean_files), len(repo_files), [
            ','.join([cleanup_report.make_github_url('', file),
                      'Python', 'example_svc'])
            for file in clean_files
        ])
    if dirty:
        repo_lookup = [file.lower() for file in repo_files]
        clean_lookup = [file.lower() for file in clean_files]
        dirty_files = sorted([file for file in repo_lookup if file not in clean_lookup])

        calls.append(call("\n"))
        if dirty_files:
            calls.append(call("**Dirty files found:**\n"))
            calls.append(call('\n'.join([
                cleanup_report.make_github_url('', file)
                for file in dirty_files
            ])))
        else:
            calls.append(call("**No dirty files found!**"))
    handle.write.assert_has_calls(calls)


def test_write_report_dup_files(mock_opened_file):
    """Tests writing a report when a file is listed twice in the metadata."""
    handle = mock_opened_file()
    cleanup_report.write_report([{
        'metadata_path': 'metadata.yaml',
        'files': [
            {'path': 'example_path1.py', 'services': ['example_svc']},
            {'path': 'example_path1.py', 'services': ['example_svc']},
        ]
    }], [
        cleanup_report.make_github_url('', 'example_path1.py'),
    ], 'test.csv')
    calls = make_expected_calls(
        1, 1, 1, [
            ','.join([cleanup_report.make_github_url(
                '', 'example_path1.py'), 'Python', 'example_svc'])
        ]
    )
    handle.write.assert_has_calls(calls)


def test_write_multi_report(mock_opened_file):
    """Tests writing a report for a list of examples."""
    examples = []
    files = []
    lines = []
    for count in range(1, 5):
        path = f'test_path_{count}.cpp'
        service = f'testsvc{count}'
        examples.append({
            'metadata_path': 'metadata.yaml',
            'files': [{
                'path': path,
                'services': [service]
            }]
        })
        lines.append(','.join([cleanup_report.GITHUB_URL + path,
                               'C++', service]))
        files.append(cleanup_report.make_github_url('', path))

    cleanup_report.write_report(examples, files, 'test.csv')
    handle = mock_opened_file()
    calls = make_expected_calls(len(lines), len(lines), len(lines), lines)
    handle.write.assert_has_calls(calls)


def test_write_report_missing_key(mock_opened_file):
    """Tests that writing a report for an example with a missing key skips the
    example but does not otherwise fail."""
    path = 'test_path.py'
    service = 'testsvc'

    cleanup_report.write_report([{
        'metadata_path': 'metadata.yaml',
        'wrong_key': [{
            'path': path,
            'services': [service]
        }]
    }, {
        'metadata_path': 'metadata.yaml',
        'files': [{
            'path': path,
            'services': [service]
        }]

    }], [cleanup_report.make_github_url('', path)], 'test.csv')
    calls = make_expected_calls(
        1, 1, 1,
        [','.join([cleanup_report.make_github_url('', path), 'Python', service])]
    )
    handle = mock_opened_file()
    handle.write.assert_has_calls(calls)


@pytest.mark.parametrize(
    "examples,repo_files,lines", [
        ([{
            'metadata_path': 'metadata.yaml',
            'files': [{
                'path': 'example_path.py',
                'services': ['test_svc']
            }]
        }], [
            cleanup_report.make_github_url('', 'example_path.py')
        ], [
            ','.join([cleanup_report.make_github_url(
                '', 'example_path.py'), 'Python', 'test_svc'])
        ]), ([{
            'metadata_path': 'metadata.yaml',
            'files': [{
                'path': 'example_path.py',
                'services': ['test_svc1', 'test_svc2', 'test_svc3']
            }]
        }], [
            cleanup_report.make_github_url('', 'example_path.py')
        ], [
            ','.join([cleanup_report.make_github_url(
                '', 'example_path.py'), 'Python', 'test_svc1']),
            ','.join([cleanup_report.make_github_url(
                '', 'example_path.py'), 'Python', 'test_svc2']),
            ','.join([cleanup_report.make_github_url(
                '', 'example_path.py'), 'Python', 'test_svc3'])
        ]), ([{
            'metadata_path': 'metadata.yaml',
            'files': [{
                'path': 'example_path.py',
                'services': ['test_svc']
            }],
        }, {
            'metadata_path': 'metadata.yaml',
            'files': [{
                'path': 'example_path.cpp',
                'services': ['test_svc']
            }]
        }], [
            cleanup_report.make_github_url('', 'example_path.py'),
            cleanup_report.make_github_url('', 'example_path.cpp'),
        ], [
            ','.join([cleanup_report.make_github_url(
                '', 'example_path.py'), 'Python', 'test_svc']),
            ','.join([cleanup_report.make_github_url(
                '', 'example_path.cpp'), 'C++', 'test_svc'])
        ]), ([{
            'metadata_path': 'metadata.yaml',
            'files': [{
                'path': 'example_path_1.py',
                'services': ['test_svc']
            }, {
                'path': 'example_path_2.py',
                'services': ['test_svc']
            }, {
                'path': 'supporting_path_1.py'
            }, {
                'path': 'test_path_1.py'
            }],
        }], [
             cleanup_report.make_github_url('', 'example_path_1.py'),
             cleanup_report.make_github_url('', 'example_path_2.py'),
             cleanup_report.make_github_url('', 'supporting_path_1.py'),
             cleanup_report.make_github_url('', 'test_path_1.py')
        ], [
            ','.join([cleanup_report.make_github_url(
                '', 'example_path_1.py'), 'Python', 'test_svc']),
            ','.join([cleanup_report.make_github_url(
                '', 'example_path_2.py'), 'Python', 'test_svc']),
            ','.join([cleanup_report.make_github_url(
                '', 'supporting_path_1.py'), 'Python', '']),
            ','.join([cleanup_report.make_github_url(
                '', 'test_path_1.py'), 'Python', ''])
        ]),
    ]
)
def test_count_files(mock_opened_file, examples, repo_files, lines):
    """Tests several scenarios to verify that files are counted correctly and
    reports are written as expected."""
    cleanup_report.write_report(examples, repo_files, 'test.csv')
    calls = make_expected_calls(len(examples), len(repo_files), len(repo_files), lines)
    handle = mock_opened_file()
    handle.write.assert_has_calls(calls)
