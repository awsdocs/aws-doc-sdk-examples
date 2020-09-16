import datetime
import boto3
from boto3.dynamodb.conditions import Key
from botocore.exceptions import ClientError
import pytest

from chalicelib.covid_data import Storage


def test_from_env(monkeypatch):
    table_name = 'test-table'
    monkeypatch.setenv('TABLE_NAME', table_name)

    storage = Storage.from_env()
    assert storage._table.name == table_name


@pytest.mark.parametrize('item_count,error_code', [
    (10, None),
    (5, 'TestException'),
    (0, None)
])
def test_get_state_data(make_stubber, monkeypatch, item_count, error_code):
    dyn_resource = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn_resource.meta.client)
    table = dyn_resource.Table('test-table')
    storage = Storage(table)
    state = 'Montana'
    items = [
        {'state': state,
         'date': (datetime.date.today() - datetime.timedelta(days=index)).isoformat(),
         'cases': index*100} for index in range(1, item_count+1)]

    dyn_stubber.stub_query(
        table.name, items, key_condition=Key('state').eq(state), error_code=error_code)
    if item_count == 0:
        item = {'state': state, 'date': datetime.date.today().isoformat()}
        monkeypatch.setattr(storage, '_generate_random_data', lambda st: item)
        dyn_stubber.stub_put_item(table.name, item)
        items.append(item)

    if error_code is None:
        got_items = storage.get_state_data(state)
        assert got_items == items
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.get_state_data(state)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('method,error_code', [
    ('PUT', None),
    ('PUT', 'TestException'),
    ('POST', None),
    ('POST', 'TestException'),
])
def test_put_or_post_state_data(make_stubber, method, error_code):
    dyn_resource = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn_resource.meta.client)
    table = dyn_resource.Table('test-table')
    storage = Storage(table)
    state = 'Georgia'
    item = {
        'state': state,
        'date': datetime.date.today().isoformat(),
        'cases': 5
    }

    dyn_stubber.stub_put_item(table.name, item, error_code=error_code)

    test_func = storage.put_state_data if method == 'PUT' else storage.post_state_data

    if error_code is None:
        test_func(state, item)
    else:
        with pytest.raises(ClientError) as exc_info:
            test_func(state, item)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('item_count,error_code,stop_on_method', [
    (10, None, None),
    (5, 'TestException', 'stub_query'),
    (0, None, None),
    (17, 'TestException', 'stub_batch_write_item')
])
def test_delete_state_data(
        make_stubber, stub_runner, item_count, error_code, stop_on_method):
    dyn_resource = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn_resource.meta.client)
    table = dyn_resource.Table('test-table')
    storage = Storage(table)
    state = 'Oregon'
    items = [{
        'state': state,
        'date': (datetime.date.today() - datetime.timedelta(days=index)).isoformat()
    } for index in range(1, item_count+1)]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            dyn_stubber.stub_query, table.name, items,
            key_condition=Key('state').eq(state))
        if item_count > 0:
            runner.add(
                dyn_stubber.stub_batch_write_item,
                request_items={
                    table.name: [{'DeleteRequest': {'Key': item}} for item in items]
                })

    if error_code is None:
        storage.delete_state_data(state)
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.delete_state_data(state)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('item_exists,error_code', [
    (True, None),
    (True, 'TestException'),
    (False, None)])
def test_get_state_date_data(make_stubber, item_exists, error_code):
    dyn_resource = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn_resource.meta.client)
    table = dyn_resource.Table('test-table')
    storage = Storage(table)
    state = 'California'
    date = '2020-15-02'
    key = {'state': state, 'date': date}
    item = key if item_exists else None

    dyn_stubber.stub_get_item(table.name, key, item, error_code=error_code)

    if error_code is None:
        got_item = storage.get_state_date_data(state, date)
        assert got_item == item
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.get_state_date_data(state, date)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_state_date_data(make_stubber, error_code):
    dyn_resource = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn_resource.meta.client)
    table = dyn_resource.Table('test-table')
    storage = Storage(table)
    state = 'California'
    date = '2020-15-02'
    key = {'state': state, 'date': date}

    dyn_stubber.stub_delete_item(table.name, key, error_code=error_code)

    if error_code is None:
        storage.delete_state_date_data(state, date)
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.delete_state_date_data(state, date)
        assert exc_info.value.response['Error']['Code'] == error_code
