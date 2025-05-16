from pytest import raises

from .. import parser as par
from yamale.validators.validators import (
    Validator, String, Regex, Number, Integer, Boolean, List, Day, Timestamp,
    Ip, Mac)


def test_eval():
    assert eval('String()') == String()


def test_types():
    assert par.parse('String()') == String()
    assert par.parse('str()') == String()
    assert par.parse('regex()') == Regex()
    assert par.parse('num()') == Number()
    assert par.parse('int()') == Integer()
    assert par.parse('day()') == Day()
    assert par.parse('timestamp()') == Timestamp()
    assert par.parse('bool()') == Boolean()
    assert par.parse('list(str())') == List(String())
    assert par.parse('ip()') == Ip()
    assert par.parse('mac()') == Mac()


def test_custom_type():

    class my_validator(Validator):
        pass

    assert par.parse('custom()', {'custom': my_validator}) == my_validator()


def test_required():
    assert par.parse('str(required=True)').is_required
    assert par.parse('str(required=False)').is_optional


def test_syntax_error():
    with raises(SyntaxError):
        par.parse('eval()')
