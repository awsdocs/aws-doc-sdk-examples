import datetime
from yamale import validators as val


def test_length_min():
    v = val.String(min=2)
    assert v.is_valid('abcd')
    assert v.is_valid('ab')
    assert not v.is_valid('a')


def test_length_max():
    v = val.String(max=3)
    assert v.is_valid('abc')
    assert v.is_valid('ab')
    assert not v.is_valid('abcd')


def test_number_max():
    v = val.Number(min=.5)
    assert v.is_valid(4)
    assert v.is_valid(.5)
    assert not v.is_valid(.1)


def test_number_min():
    v = val.Integer(max=10)
    assert v.is_valid(4)
    assert v.is_valid(10)
    assert not v.is_valid(11)


def test_timestamp_min():
    v = val.Timestamp(min=datetime.datetime(2010, 1, 1))
    assert v.is_valid(datetime.datetime(2010, 1, 1))
    assert v.is_valid(datetime.datetime(2011, 2, 2))
    assert not v.is_valid(datetime.datetime(2009, 12, 31))


def test_timestamp_max():
    v = val.Timestamp(max=datetime.datetime(2010, 1, 1))
    assert v.is_valid(datetime.datetime(2010, 1, 1))
    assert v.is_valid(datetime.datetime(2009, 2, 2))
    assert not v.is_valid(datetime.datetime(2010, 2, 2))


def test_day_min():
    v = val.Day(min=datetime.date(2010, 1, 1))
    assert v.is_valid(datetime.date(2010, 1, 1))
    assert v.is_valid(datetime.date(2011, 2, 2))
    assert not v.is_valid(datetime.date(2009, 12, 31))


def test_day_max():
    v = val.Day(max=datetime.date(2010, 1, 1))
    assert v.is_valid(datetime.date(2010, 1, 1))
    assert v.is_valid(datetime.date(2009, 2, 2))
    assert not v.is_valid(datetime.date(2010, 2, 2))


def test_str_equals():
    v = val.String(equals='abcd')
    assert v.is_valid('abcd')
    assert not v.is_valid('abcde')
    assert not v.is_valid('c')


def test_str_equals_ignore_case():
    v = val.String(equals='abcd', ignore_case=True)
    assert v.is_valid('abCd')
    assert not v.is_valid('abcde')
    assert not v.is_valid('C')


def test_str_starts_with():
    v = val.String(starts_with='abc')
    assert v.is_valid('abcd')
    assert not v.is_valid('bcd')
    assert not v.is_valid('c')


def test_str_starts_with_ignore_case():
    v = val.String(starts_with='abC', ignore_case=True)
    assert v.is_valid('abCde')
    assert v.is_valid('abcde')
    assert not v.is_valid('bcd')
    assert not v.is_valid('C')


def test_str_ends_with():
    v = val.String(ends_with='abcd')
    assert v.is_valid('abcd')
    assert not v.is_valid('abcde')
    assert not v.is_valid('c')


def test_str_ends_with_ignore_case():
    v = val.String(ends_with='abC', ignore_case=True)
    assert v.is_valid('xyzabC')
    assert v.is_valid('xyzabc')
    assert not v.is_valid('cde')
    assert not v.is_valid('C')


def test_str_matches():
    v = val.String(matches=r'^(abc)\1?de$')
    assert v.is_valid('abcabcde')
    assert not v.is_valid('abcabcabcde')
    assert not v.is_valid('\12')

    v = val.String(matches=r'[a-z0-9]{3,}s\s$', ignore_case=True)
    assert v.is_valid('b33S\v')
    assert v.is_valid('B33s\t')
    assert not v.is_valid(' b33s ')
    assert not v.is_valid('b33s  ')

    v = val.String(matches=r'A.+\d$', ignore_case=False, multiline=True)
    assert v.is_valid('A_-3\n\n')
    assert not v.is_valid('a!!!!!5\n\n')

    v = val.String(matches=r'.*^Ye.*s\.', ignore_case=True, multiline=True, dotall=True)
    assert v.is_valid('YEeeEEEEeeeeS.')
    assert v.is_valid('What?\nYes!\nBEES.\nOK.')
    assert not v.is_valid('YES-TA-TOES?')
    assert not v.is_valid('\n\nYaes.')


def test_char_exclude():
    v = val.String(exclude='abcd')
    assert v.is_valid('efg')
    assert not v.is_valid('abc')
    assert not v.is_valid('c')


def test_char_exclude_igonre_case():
    v = val.String(exclude='abcd', ignore_case=True)
    assert v.is_valid('efg')
    assert v.is_valid('Efg')
    assert not v.is_valid('abc')
    assert not v.is_valid('Def')
    assert not v.is_valid('c')


def test_ip4():
    v = val.Ip(version=4)
    assert v.is_valid('192.168.1.1')
    assert v.is_valid('192.168.1.255')
    assert v.is_valid('192.168.3.1/24')
    assert not v.is_valid('2001:db8::')
    assert not v.is_valid('2001:db8::/64')

def test_ip6():
    v = val.Ip(version=6)
    assert not v.is_valid('192.168.1.1')
    assert not v.is_valid('192.168.1.255')
    assert not v.is_valid('192.168.3.1/24')
    assert v.is_valid('2001:db8::')
    assert v.is_valid('2001:db8::/64')
