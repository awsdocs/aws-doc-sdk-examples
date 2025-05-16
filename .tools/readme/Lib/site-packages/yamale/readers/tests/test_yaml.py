import io
import pytest
from .. import yaml_reader
from yamale.tests import get_fixture

parsers = ['pyyaml', 'PyYAML', 'ruamel']
TYPES = get_fixture('types.yaml')
NESTED = get_fixture('nested.yaml')
KEYWORDS = get_fixture('keywords.yaml')


@pytest.mark.parametrize('parser', parsers)
@pytest.mark.parametrize('use_string', [True, False])
def test_parse(parser, use_string):
    if use_string:
        with io.open(TYPES, encoding='utf-8') as f:
            content = f.read()
        a = yaml_reader.parse_yaml(parser=parser, content=content)[0]
    else:
        a = yaml_reader.parse_yaml(TYPES, parser)[0]
    assert a['string'] == 'str()'


def test_parse_validates_arguments():
    with pytest.raises(TypeError):
        yaml_reader.parse_yaml(path=TYPES, content="name: Bob")
    with pytest.raises(TypeError):
        yaml_reader.parse_yaml(path=None, content=None)


@pytest.mark.parametrize('parser', parsers)
def test_types(parser):
    t = yaml_reader.parse_yaml(TYPES, parser)[0]
    assert t['string'] == 'str()'
    assert t['number'] == 'num()'
    assert t['boolean'] == 'bool()'
    assert t['integer'] == 'int()'


@pytest.mark.parametrize('parser', parsers)
def test_keywords(parser):
    t = yaml_reader.parse_yaml(KEYWORDS, parser)[0]
    assert t['optional_min'] == 'int(min=1, required=False)'


@pytest.mark.parametrize('parser', parsers)
def test_nested(parser):
    t = yaml_reader.parse_yaml(NESTED, parser)[0]
    assert t['list'][-1]['string'] == 'str()'
