import os

import pytest

from .. import command_line
from .. import yamale_error

dir_path = os.path.dirname(os.path.realpath(__file__))

parsers = ['pyyaml', 'PyYAML', 'ruamel']


@pytest.mark.parametrize('parser', parsers)
def test_bad_yaml(parser):
    with pytest.raises(ValueError) as e:
        command_line._router(
            'yamale/tests/command_line_fixtures/yamls/bad.yaml',
            'schema.yaml', 1, parser)
    assert "map.bad: '12.5' is not a str." in e.value.message


@pytest.mark.parametrize('parser', parsers)
def test_required_keys_yaml(parser):
    with pytest.raises(ValueError) as e:
        command_line._router(
            'yamale/tests/command_line_fixtures/yamls/required_keys_bad.yaml',
            'required_keys_schema.yaml', 1, parser)
    assert "map.key: Required field missing" in e.value.message


@pytest.mark.parametrize('parser', parsers)
def test_good_yaml(parser):
    command_line._router(
        'yamale/tests/command_line_fixtures/yamls/good.yaml',
        'schema.yaml', 1, parser)
    

@pytest.mark.parametrize('parser', parsers)
def test_good_relative_yaml(parser):
    command_line._router(
        'yamale/tests/command_line_fixtures/yamls/good.yaml',
        '../schema_dir/external.yaml', 1, parser)
    

@pytest.mark.parametrize('parser', parsers)
def test_external_glob_schema(parser):
    command_line._router(
        'yamale/tests/command_line_fixtures/yamls/good.yaml',
        os.path.join(dir_path, 'command_line_fixtures/schema_dir/ex*.yaml'), 1, parser)
    

def test_empty_schema_file():
    with pytest.raises(ValueError, match='is an empty file!'):
        command_line._router(
            'yamale/tests/command_line_fixtures/empty_schema/data.yaml',
            'empty_schema.yaml' , 1, 'PyYAML')


def test_external_schema():
    command_line._router(
        'yamale/tests/command_line_fixtures/yamls/good.yaml',
        os.path.join(dir_path, 'command_line_fixtures/schema_dir/external.yaml'), 1, 'PyYAML')


def test_bad_dir():
    with pytest.raises(ValueError):
        command_line._router(
            'yamale/tests/command_line_fixtures/yamls',
            'schema.yaml', 4, 'PyYAML')


def test_bad_strict():
    with pytest.raises(ValueError) as e:
        command_line._router(
            'yamale/tests/command_line_fixtures/yamls/required_keys_extra_element.yaml',
            'required_keys_schema.yaml',
            4, 'PyYAML', strict=True)
    assert "map.key2: Unexpected element" in e.value.message


def test_bad_issue_54():
    with pytest.raises(yamale_error.YamaleError) as e:
        command_line._router(
            'yamale/tests/fixtures/nested_issue_54.yaml',
            'nested.yaml',
            4, 'PyYAML', strict=True)
    assert 'string: Required field missing' in e.value.message
    assert 'number: Required field missing' in e.value.message
    assert 'integer: Required field missing' in e.value.message
    assert 'boolean: Required field missing' in e.value.message
    assert 'date: Required field missing' in e.value.message
    assert 'datetime: Required field missing' in e.value.message
    assert 'nest: Required field missing' in e.value.message
    assert 'list: Required field missing' in e.value.message

def test_nested_schema_issue_69():
    command_line._router('yamale/tests/command_line_fixtures/nestedYaml','schema.yaml', 1, 'PyYAML')

