# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for mysql_helper.py functions.
"""

import datetime
from chalicelib.mysql_helper import Table, Column, ForeignKey
import chalicelib.mysql_helper as mysql_helper


def make_table():
    table = Table('Test', [
        Column('TestID', int, nullable=False, auto_increment=True, primary_key=True),
        Column('FirstName', str),
        Column('LastName', str),
        Column('Birthday', datetime.date, nullable=False),
        Column('ForeignID', int, foreign_key=ForeignKey('OtherTable', 'OtherID'))
    ])
    return table


def make_foreign_table():
    table = Table('OtherTable', [
        Column('OtherID', int, nullable=False, auto_increment=True, primary_key=True),
        Column('OtherField', str),
        Column('ThirdCol', float)])
    return table


def test_create_table():
    table = make_table()
    sql = mysql_helper.create_table(table)
    assert sql == 'CREATE TABLE Test (' \
                  'TestID int NOT NULL AUTO_INCREMENT, ' \
                  'FirstName varchar(255), ' \
                  'LastName varchar(255), ' \
                  'Birthday DATE NOT NULL, ' \
                  'ForeignID int, ' \
                  'PRIMARY KEY (TestID), ' \
                  'FOREIGN KEY (ForeignID) REFERENCES OtherTable(OtherID))'


def test_insert():
    table = make_table()
    sql, param_sets = mysql_helper.insert(table, [{
        'FirstName': 'Bob',
        'LastName': 'Smith',
        'Birthday': datetime.date(1982, 1, 13),
        'ForeignID': 55}])
    assert sql == 'INSERT INTO Test (FirstName, LastName, Birthday, ForeignID) ' \
                  'VALUES (:FirstName, :LastName, :Birthday, :ForeignID)'
    assert param_sets == [[
        {'name': 'FirstName', 'value': {'stringValue': 'Bob'}},
        {'name': 'LastName', 'value': {'stringValue': 'Smith'}},
        {'name': 'Birthday', 'typeHint': 'DATE',
         'value': {'stringValue': '1982-01-13'}},
        {'name': 'ForeignID', 'value': {'longValue': 55}}]]


def test_insert_batch():
    table = make_table()
    sql, param_sets = mysql_helper.insert(table, [{
        'FirstName': 'Bob',
        'LastName': 'Smith',
        'Birthday': datetime.date(1982, 1, 13),
        'ForeignID': 55}, {
        'FirstName': 'Ted',
        'LastName': 'Jones',
        'Birthday': datetime.date(1915, 12, 4),
        'ForeignID': 100
    }])
    assert sql == 'INSERT INTO Test (FirstName, LastName, Birthday, ForeignID) ' \
                  'VALUES (:FirstName, :LastName, :Birthday, :ForeignID)'
    assert param_sets == [[
        {'name': 'FirstName', 'value': {'stringValue': 'Bob'}},
        {'name': 'LastName', 'value': {'stringValue': 'Smith'}},
        {'name': 'Birthday', 'typeHint': 'DATE',
         'value': {'stringValue': '1982-01-13'}},
        {'name': 'ForeignID', 'value': {'longValue': 55}}], [
        {'name': 'FirstName', 'value': {'stringValue': 'Ted'}},
        {'name': 'LastName', 'value': {'stringValue': 'Jones'}},
        {'name': 'Birthday', 'typeHint': 'DATE',
         'value': {'stringValue': '1915-12-04'}},
        {'name': 'ForeignID', 'value': {'longValue': 100}}]]


def test_update():
    table = make_table()
    sql, sql_params = mysql_helper.update(table.name, {'FirstName': 'Ted'}, [{
        'table': 'Test', 'column': 'TestID', 'op': '=', 'value': 1}])
    assert sql == "UPDATE Test SET FirstName=:set_FirstName " \
                  "WHERE Test.TestID = :Test_TestID"
    assert sql_params == [
        {'name': 'set_FirstName', 'value': {'stringValue': 'Ted'}},
        {'name': 'Test_TestID', 'value': {'longValue': 1}}]


def test_query():
    tables = {'Test': make_table(), 'OtherTable': make_foreign_table()}
    sql, columns, sql_params = mysql_helper.query('Test', tables, [
        {'table': 'Test', 'column': 'LastName', 'op': '=', 'value': 'Smith'},
        {'table': 'OtherTable', 'column': 'ThirdCol', 'op': '<', 'value': 0.33}])
    assert sql == "SELECT Test.TestID, Test.FirstName, Test.LastName, Test.Birthday, " \
                  "OtherTable.OtherID, OtherTable.OtherField, OtherTable.ThirdCol " \
                  "FROM Test " \
                  "INNER JOIN OtherTable ON Test.ForeignID=OtherTable.OtherID " \
                  "WHERE Test.LastName = :Test_LastName " \
                  "AND OtherTable.ThirdCol < :OtherTable_ThirdCol"
    cols = {}
    for table in tables.values():
        for col in table.cols:
            if not col.foreign_key:
                cols[f'{table.name}.{col.name}'] = col
    assert cols == columns
    assert sql_params == [
        {'name': 'Test_LastName', 'value': {'stringValue': 'Smith'}},
        {'name': 'OtherTable_ThirdCol', 'value': {'doubleValue': 0.33}}]


def test_unpack_query():
    columns = {
        'test1': Column('test1', str),
        'test2': Column('test2', int)
    }
    results = {'records': [[{'stringValue': 'Hello'}, {'longValue': 13}]]}
    output = mysql_helper.unpack_query_results(columns, results)
    assert output == [{'test1': 'Hello', 'test2': 13}]


def test_unpack_insert_results():
    results = {'generatedFields': [{'longValue': 88}]}
    assert mysql_helper.unpack_insert_results(results) == 88


def test_delete():
    table = make_table()
    sql, sql_param_sets = mysql_helper.delete(table, [{'TestID': 13}])
    assert sql == "DELETE FROM Test WHERE TestID=:TestID"
    assert sql_param_sets == [[{'name': 'TestID', 'value': {'longValue': 13}}]]
