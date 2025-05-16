import pytest
from pytest import raises
from .. import parse_yaml

parsers = ['pyyaml', 'PyYAML', 'ruamel']


@pytest.mark.parametrize('parser', parsers)
def test_reader_error(parser):
    with raises(IOError):
        parse_yaml('wat', parser)
