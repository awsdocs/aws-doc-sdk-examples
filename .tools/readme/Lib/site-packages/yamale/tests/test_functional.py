import io
import pytest
import re
import yamale

from . import get_fixture
from .. import validators as val

types = {
    'schema': 'types.yaml',
    'bad': 'types_bad_data.yaml',
    'good': 'types_good_data.yaml'
}

nested = {
    'schema': 'nested.yaml',
    'bad': 'nested_bad_data.yaml',
    'good': 'nested_good_data.yaml'
}

custom = {
    'schema': 'custom_types.yaml',
    'bad': 'custom_types_bad.yaml',
    'good': 'custom_types_good.yaml'
}

keywords = {
    'schema': 'keywords.yaml',
    'bad': 'keywords_bad.yaml',
    'good': 'keywords_good.yaml'
}

lists = {
    'schema': 'lists.yaml',
    'bad': 'lists_bad.yaml',
    'bad2': 'lists_bad2.yaml',
    'good': 'lists_good.yaml'
}

maps = {
    'schema': 'map.yaml',
    'bad': 'map_bad.yaml',
    'bad2': 'map_bad2.yaml',
    'good': 'map_good.yaml'
}

anys = {
    'schema': 'any.yaml',
    'bad': 'any_bad.yaml',
    'good': 'any_good.yaml'
}

list_include = {
    'schema': 'list_include.yaml',
    'good': 'list_include_good.yaml'
}

issue_22 = {
    'schema': 'issue_22.yaml',
    'good': 'issue_22_good.yaml'
}

issue_50 = {
    'schema': 'issue_50.yaml',
    'good': 'issue_50_good.yaml'
}

regexes = {
    'schema': 'regex.yaml',
    'bad': 'regex_bad.yaml',
    'good': 'regex_good.yaml'
}

ips = {
    'schema': 'ip.yaml',
    'bad': 'ip_bad.yaml',
    'good': 'ip_good.yaml'
}

macs = {
    'schema': 'mac.yaml',
    'bad': 'mac_bad.yaml',
    'good': 'mac_good.yaml'
}

nested_map = {
    'schema': 'nested_map.yaml',
    'good': 'nested_map_good.yaml'
}

top_level_map = {
    'schema': 'top_level_map.yaml',
    'good': 'top_level_map_good.yaml'
}

include_validator = {
    'schema': 'include_validator.yaml',
    'good': 'include_validator_good.yaml',
    'bad': 'include_validator_bad.yaml'
}

strict_map = {
    'schema': 'strict_map.yaml',
    'good': 'strict_map_good.yaml',
    'bad': 'strict_map_bad.yaml'
}

mixed_strict_map = {
    'schema': 'mixed_strict_map.yaml',
    'good': 'mixed_strict_map_good.yaml',
    'bad': 'mixed_strict_map_bad.yaml'
}

strict_list = {
    'schema': 'strict_list.yaml',
    'good': 'strict_list_good.yaml',
    'bad': 'strict_list_bad.yaml'
}

nested_map2 = {
    'schema': 'nested_map2.yaml',
    'good': 'nested_map2_good.yaml',
    'bad': 'nested_map2_bad.yaml'
}

static_list = {
    'schema': 'static_list.yaml',
    'good': 'static_list_good.yaml',
    'bad': 'static_list_bad.yaml'
}

nested_issue_54 = {
    'schema': 'nested.yaml',
    'bad': 'nested_issue_54.yaml',
    'good': 'nested_good_data.yaml'
}

map_key_constraint = {
    'schema': 'map_key_constraint.yaml',
    'good': 'map_key_constraint_good.yaml',
    'bad_base': 'map_key_constraint_bad_base.yaml',
    'bad_nest': 'map_key_constraint_bad_nest.yaml',
    'bad_nest_con': 'map_key_constraint_bad_nest_con.yaml',
}

numeric_bool_coercion = {
    'schema': 'numeric_bool_coercion.yaml',
    'good': 'numeric_bool_coercion_good.yaml',
    'bad': 'numeric_bool_coercion_bad.yaml',
}

subset = {
    'schema': 'subset.yaml',
    'good': 'subset_good.yaml',
    'good2': 'subset_good2.yaml',
    'bad': 'subset_bad.yaml',
    'bad2': 'subset_bad2.yaml',
    'bad3': 'subset_bad3.yaml'
}

subset_empty = {
    'schema': 'subset_empty.yaml',
    'good': 'subset_empty_good.yaml',
    'good2': 'subset_empty_good2.yaml'
}

subset_nodef = {
    'schema': 'subset_nodef.yaml'
}

test_data = [
    types, nested, custom,
    keywords, lists, maps,
    anys, list_include, issue_22,
    issue_50, regexes, ips, macs,
    nested_map, top_level_map,
    include_validator, strict_map,
    mixed_strict_map, strict_list,
    nested_map2, static_list,
    nested_issue_54,
    map_key_constraint,
    numeric_bool_coercion,
    subset, subset_empty
]

for d in test_data:
    for key in d.keys():
        if key == 'schema':
            d[key] = yamale.make_schema(get_fixture(d[key]))
        else:
            d[key] = yamale.make_data(get_fixture(d[key]))


def test_tests():
    """ Make sure the test runner is working."""
    assert 1 + 1 == 2


def test_flat_make_schema():
    assert isinstance(types['schema']._schema['string'], val.String)


def test_nested_schema():
    nested_schema = nested['schema']._schema
    assert isinstance(nested_schema['string'], val.String)
    assert isinstance(nested_schema['list'], (list, tuple))
    assert isinstance(nested_schema['list'][0], val.String)


@pytest.mark.parametrize('data_map', test_data)
def test_good(data_map):
    for k, v in data_map.items():
        if k.startswith('good'):
            yamale.validate(data_map['schema'], data_map[k])


def test_bad_validate():
    assert count_exception_lines(types['schema'], types['bad']) == 9


def test_bad_nested():
    assert count_exception_lines(nested['schema'], nested['bad']) == 2


def test_bad_nested_issue_54():
    exp = [
        'string: Required field missing',
        'number: Required field missing',
        'integer: Required field missing',
        'boolean: Required field missing',
        'date: Required field missing',
        'datetime: Required field missing',
        'nest: Required field missing',
        'list: Required field missing'
    ]
    match_exception_lines(nested_issue_54['schema'], nested_issue_54['bad'], exp)

def test_bad_custom():
    assert count_exception_lines(custom['schema'], custom['bad']) == 1


def test_bad_lists():
    assert count_exception_lines(lists['schema'], lists['bad']) == 6


def test_bad2_lists():
    assert count_exception_lines(lists['schema'], lists['bad2']) == 2


def test_bad_maps():
    assert count_exception_lines(maps['schema'], maps['bad']) == 7

def test_bad_maps2():
    assert count_exception_lines(maps['schema'], maps['bad2']) == 1

def test_bad_keywords():
    assert count_exception_lines(keywords['schema'], keywords['bad']) == 9


def test_bad_anys():
    assert count_exception_lines(anys['schema'], anys['bad']) == 5


def test_bad_regexes():
    assert count_exception_lines(regexes['schema'], regexes['bad']) == 4


def test_bad_include_validator():
    exp = ["key1: 'a_string' is not a int."]
    match_exception_lines(include_validator['schema'],
                          include_validator['bad'],
                          exp)


def test_bad_schema():
    with pytest.raises(SyntaxError) as excinfo:
        yamale.make_schema(get_fixture('bad_schema.yaml'))
    assert 'fixtures/bad_schema.yaml' in str(excinfo.value)


def test_empty_schema():
    with pytest.raises(ValueError) as excinfo:
        yamale.make_schema(get_fixture('empty_schema.yaml'))
    assert 'empty_schema.yaml is an empty file!' in str(excinfo.value)


@pytest.mark.parametrize(
    "schema_filename",
    ['bad_schema_rce.yaml', 'bad_schema_rce2.yaml', 'bad_schema_rce3.yaml', 'bad_schema_rce4.yaml']
)
def test_vulnerable_schema(schema_filename):
    with pytest.raises(SyntaxError) as excinfo:
        yamale.make_schema(get_fixture(schema_filename))
    assert schema_filename in str(excinfo.value)


def test_list_is_not_a_map():
    exp = [" : '[1, 2]' is not a map"]
    match_exception_lines(strict_map['schema'],
                          strict_list['good'],
                          exp)


def test_bad_strict_map():
    exp = ['extra: Unexpected element']
    match_exception_lines(strict_map['schema'],
                          strict_map['bad'],
                          exp,
                          strict=True)


def test_bad_strict_list():
    exp = ['2: Unexpected element']
    match_exception_lines(strict_list['schema'],
                          strict_list['bad'],
                          exp,
                          strict=True)


def test_bad_mixed_strict_map():
    exp = ['field3.extra: Unexpected element']
    match_exception_lines(mixed_strict_map['schema'],
                          mixed_strict_map['bad'],
                          exp)


def test_bad_nested_map2():
    exp = ['field1.field1_1: Required field missing']
    match_exception_lines(nested_map2['schema'],
                          nested_map2['bad'],
                          exp)


def test_bad_static_list():
    exp = ['0: Required field missing']
    match_exception_lines(static_list['schema'],
                          static_list['bad'],
                          exp)


def test_bad_map_key_constraint_base():
    exp = [": Key error - 'bad' is not a int."]
    match_exception_lines(map_key_constraint['schema'],
                          map_key_constraint['bad_base'],
                          exp)


def test_bad_map_key_constraint_nest():
    exp = ["1.0: Key error - '100' is not a str."]
    match_exception_lines(map_key_constraint['schema'],
                          map_key_constraint['bad_nest'],
                          exp)


def test_bad_map_key_constraint_nest_con():
    exp = [
        "1.0: Key error - '100' is not a str.",
        "1.0: Key error - 'baz' contains excluded character 'z'",
    ]
    match_exception_lines(map_key_constraint['schema'],
                          map_key_constraint['bad_nest_con'],
                          exp)


def test_bad_numeric_bool_coercion():
    exp = [
        "integers.0: 'False' is not a int.",
        "integers.1: 'True' is not a int.",
        "numbers.0: 'False' is not a num.",
        "numbers.1: 'True' is not a num.",
    ]
    match_exception_lines(numeric_bool_coercion['schema'],
                          numeric_bool_coercion['bad'],
                          exp)

def test_bad_subset():
    exp = [
        "subset_list: 'subset' may not be an empty set."
    ]
    match_exception_lines(subset['schema'],
                          subset['bad'],
                          exp)

def test_bad_subset2():
    exp = [
        "subset_list: '[1]' is not a int.",
        "subset_list: '[1]' is not a str."
    ]
    match_exception_lines(subset['schema'],
                          subset['bad2'],
                          exp)

def test_bad_subset3():
    exp = [
        "subset_list: '{'a': 1}' is not a int.",
        "subset_list: '{'a': 1}' is not a str."
    ]
    match_exception_lines(subset['schema'],
                          subset['bad3'],
                          exp)

def test_nodef_subset_schema():
    with pytest.raises(ValueError) as e:
        yamale.make_schema(get_fixture(subset_nodef['schema']))

    assert "'subset' requires at least one validator!" in str(e.value)

@pytest.mark.parametrize("use_schema_string,use_data_string,expected_message_re", [
    (False, False, "^Error validating data '.*?' with schema '.*?'\n\t"),
    (True, False, "^Error validating data '.*?'\n\t"),
    (False, True, "^Error validating data with schema '.*?'\n\t"),
    (True, True, "^Error validating data\n\t"),
])
def test_validate_errors(use_schema_string, use_data_string, expected_message_re):
    schema_path = get_fixture('types.yaml')
    data_path = get_fixture('types_bad_data.yaml')
    if use_schema_string:
        with io.open(schema_path, encoding='utf-8') as f:
            schema = yamale.make_schema(content=f.read())
    else:
        schema = yamale.make_schema(schema_path)
    if use_data_string:
        with io.open(data_path, encoding='utf-8') as f:
            data = yamale.make_data(content=f.read())
    else:
        data = yamale.make_data(data_path)
    with pytest.raises(yamale.yamale_error.YamaleError) as excinfo:
        yamale.validate(schema, data)
    assert re.match(expected_message_re, excinfo.value.message, re.MULTILINE), \
        'Message {} should match {}'.format(
            excinfo.value.message, expected_message_re
        )


def match_exception_lines(schema, data, expected, strict=False):
    with pytest.raises(ValueError) as e:
        yamale.validate(schema, data, strict)

    got = e.value.results[0].errors
    got.sort()
    expected.sort()
    assert got == expected


def count_exception_lines(schema, data, strict=False):
    with pytest.raises(ValueError) as e:
        yamale.validate(schema, data, strict)
    result = e.value.results[0]
    return len(result.errors)
