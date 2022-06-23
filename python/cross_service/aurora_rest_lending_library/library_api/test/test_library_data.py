# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for library_data.py functions.
"""

import datetime
import pytest
import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
from chalicelib.library_data import Storage

CLUSTER_ARN = 'arn:aws:rds:us-west-2:123456789012:cluster:test-cluster'
SECRET_ARN = 'arn:aws:secretsmanager:us-west-2:123456789012:secret:test-secret-111111'
DB_NAME = 'testdatabase'


def make_storage_n_stubber(make_stubber):
    rdsdata_client = boto3.client('rds-data')
    storage = Storage(
        {'DBClusterArn': CLUSTER_ARN}, {'ARN': SECRET_ARN}, DB_NAME, rdsdata_client)
    return storage, make_stubber(rdsdata_client)


def test_bootstrap_tables(make_stubber):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)

    for _ in storage._tables:
        rdsdata_stubber.stub_execute_statement(CLUSTER_ARN, SECRET_ARN, DB_NAME, ANY)

    storage.bootstrap_tables()


def test_add_books(make_stubber):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    books = [
        {'title': 'Book One', 'author': 'Francine First'},
        {'title': 'Second Book', 'author': 'Stephanie Second'},
        {'title': 'Book One 2 (the sequel)', 'author': 'Francine First'}]
    author_sql = "INSERT INTO Authors (FirstName, LastName) " \
                 "VALUES (:FirstName, :LastName)"
    authors = {book['author']: {
                'FirstName': ' '.join(book['author'].split(' ')[:-1]),
                'LastName': book['author'].split(' ')[-1]
            } for book in books}
    author_param_sets = [[
        {'name': 'FirstName',
         'value': {'stringValue': author['FirstName']}},
        {'name': 'LastName', 'value': {'stringValue': author['LastName']}}]
        for author in authors.values()]
    author_generated_field_sets = [[1], [2]]
    book_sql = "INSERT INTO Books (Title, AuthorID) VALUES (:Title, :AuthorID)"
    book_param_sets = [[
        {'name': 'Title', 'value': {'stringValue': book['title']}},
        {'name': 'AuthorID', 'value': {'longValue': author_id}}]
        for book, author_id in zip(books, [1, 2, 1])]
    book_generated_field_sets = [[11], [22], [33]]

    rdsdata_stubber.stub_batch_execute_statement(
        CLUSTER_ARN, SECRET_ARN, DB_NAME, author_sql, sql_param_sets=author_param_sets,
        generated_field_sets=author_generated_field_sets)
    rdsdata_stubber.stub_batch_execute_statement(
        CLUSTER_ARN, SECRET_ARN, DB_NAME, book_sql, sql_param_sets=book_param_sets,
        generated_field_sets=book_generated_field_sets)

    author_count, book_count = storage.add_books(books)
    assert author_count == 2
    assert book_count == 3


@pytest.mark.parametrize('author_id,error_code', [
    (None, None), (13, None), (None, 'TestException')])
def test_get_books(make_stubber, author_id, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    sql = "SELECT Books.BookID, Books.Title, Authors.AuthorID, " \
          "Authors.FirstName, Authors.LastName FROM Books " \
          "INNER JOIN Authors ON Books.AuthorID=Authors.AuthorID"
    sql_params = None
    if author_id is not None:
        sql += " WHERE Authors.AuthorID = :Authors_AuthorID"
        sql_params = [
            {'name': 'Authors_AuthorID',
             'value': {'longValue': author_id}}]
    records = [
        [1, 'Title One', 1, 'Freddy', 'Fake'],
        [2, 'Title Two', 13, 'Peter', 'Pretend']]

    rdsdata_stubber.stub_execute_statement(
        CLUSTER_ARN, SECRET_ARN, DB_NAME, sql, sql_params=sql_params, records=records,
        error_code=error_code)

    if error_code is None:
        got_books = storage.get_books(author_id)
        assert [list(book.values()) for book in got_books] == records
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.get_books(author_id)
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_execute_statement')])
def test_add_book(make_stubber, stub_runner, error_code, stop_on_method):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    transaction_id = 'trid-747'
    book = {'Books.Title': 'Test Book', 'Authors.FirstName': 'Teddy',
            'Authors.LastName': 'Tester'}
    author_sql = \
        "INSERT INTO Authors (FirstName, LastName) VALUES (:FirstName, :LastName)"
    author_params = [
        {'name': 'FirstName', 'value': {'stringValue': 'Teddy'}},
        {'name': 'LastName', 'value': {'stringValue': 'Tester'}}]
    author_id = 101
    book_sql = "INSERT INTO Books (Title, AuthorID) VALUES (:Title, :AuthorID)"
    book_params = [
        {'name': 'Title', 'value': {'stringValue': 'Test Book'}},
        {'name': 'AuthorID', 'value': {'longValue': author_id}}]
    book_id = 66

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            rdsdata_stubber.stub_begin_transaction, CLUSTER_ARN, SECRET_ARN, DB_NAME,
            transaction_id)
        runner.add(
            rdsdata_stubber.stub_execute_statement, CLUSTER_ARN, SECRET_ARN, DB_NAME,
            author_sql, author_params, transaction_id=transaction_id,
            generated_fields=[author_id])
        runner.add(
            rdsdata_stubber.stub_execute_statement, CLUSTER_ARN, SECRET_ARN, DB_NAME,
            book_sql, book_params, transaction_id=transaction_id,
            generated_fields=[book_id])
        runner.add(rdsdata_stubber.stub_commit_transaction, CLUSTER_ARN, SECRET_ARN,
                   transaction_id)
    if error_code is not None:
        rdsdata_stubber.stub_rollack_transaction(
            CLUSTER_ARN, SECRET_ARN, transaction_id)

    result = storage.add_book(book)
    if error_code is None:
        assert result == (author_id, book_id)
    else:
        assert result is None


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_authors(make_stubber, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    sql = "SELECT Authors.AuthorID, Authors.FirstName, Authors.LastName FROM Authors "
    records = [
        [1, 'Freddy', 'Fake'],
        [13, 'Peter', 'Pretend']]

    rdsdata_stubber.stub_execute_statement(
        CLUSTER_ARN, SECRET_ARN, DB_NAME, sql, records=records, error_code=error_code)

    if error_code is None:
        got_authors = storage.get_authors()
        assert [list(author.values()) for author in got_authors] == records
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.get_authors()
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_patrons(make_stubber, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    sql = "SELECT Patrons.PatronID, Patrons.FirstName, Patrons.LastName FROM Patrons "
    records = [
        [1, 'Randall', 'Reader'],
        [13, 'Bob', 'Booker']]

    rdsdata_stubber.stub_execute_statement(
        CLUSTER_ARN, SECRET_ARN, DB_NAME, sql, records=records, error_code=error_code)

    if error_code is None:
        got_patrons = storage.get_patrons()
        assert [list(patron.values()) for patron in got_patrons] == records
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.get_patrons()
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_patron(make_stubber, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    patron = {'Patrons.FirstName': 'Marguerite', 'Patrons.LastName': 'Magazine'}
    patron_sql = \
        "INSERT INTO Patrons (FirstName, LastName) VALUES (:FirstName, :LastName)"
    patron_params = [
        {'name': 'Patrons.FirstName', 'value': {'stringValue': 'Marguerite'}},
        {'name': 'Patrons.LastName', 'value': {'stringValue': 'Magazine'}}]
    patron_id = 36

    rdsdata_stubber.stub_execute_statement(CLUSTER_ARN, SECRET_ARN, DB_NAME,
        patron_sql, patron_params, generated_fields=[patron_id], error_code=error_code)

    if error_code is None:
        got_patron_id = storage.add_patron(patron)
        assert got_patron_id == patron_id
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.add_patron(patron)
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_patron(make_stubber, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    patron_id = 38
    patron_sql = \
        "DELETE FROM Patrons WHERE PatronID=:PatronID"
    patron_params = [{'name': 'PatronID', 'value': {'longValue': 38}}]

    rdsdata_stubber.stub_execute_statement(CLUSTER_ARN, SECRET_ARN, DB_NAME,
        patron_sql, patron_params, error_code=error_code)

    if error_code is None:
        storage.delete_patron(patron_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.delete_patron(patron_id)
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_borrowed_books(make_stubber, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    sql = "SELECT Lending.LendingID, Books.BookID, Books.Title, " \
          "Authors.AuthorID, Authors.FirstName, Authors.LastName, " \
          "Patrons.PatronID, Patrons.FirstName, Patrons.LastName, " \
          "Lending.Lent, Lending.Returned " \
          "FROM Lending " \
          "INNER JOIN Books ON Lending.BookID=Books.BookID " \
          "INNER JOIN Authors ON Books.AuthorID=Authors.AuthorID " \
          "INNER JOIN Patrons ON Lending.PatronID=Patrons.PatronID " \
          "WHERE Lending.Lent >= :Lending_Lent " \
          "AND Lending.Returned IS :Lending_Returned"
    sql_params = [{'name': 'Lending_Lent',
                  'value': {'stringValue': str(datetime.date.today())}},
                  {'name': 'Lending_Returned', 'value': {'isNull': True}}]
    records = [
        [1, 5, 'Writing Words', 10, 'Walter', 'Writer', 55, 'Randall', 'Reader',
         str(datetime.date.today())],
        [13, 39, 'Thirteen', 1300, 'Theodore', 'Three', 103, 'Bob', 'Booker',
         str(datetime.date(2018, 10, 11))]]

    rdsdata_stubber.stub_execute_statement(
        CLUSTER_ARN, SECRET_ARN, DB_NAME, sql, sql_params=sql_params, records=records,
        error_code=error_code)

    if error_code is None:
        got_books = storage.get_borrowed_books()
        assert [list(book.values()) for book in got_books] == records
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.get_borrowed_books()
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_borrow_book(make_stubber, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    book_id = 35
    patron_id = 405
    sql = \
        "INSERT INTO Lending (BookID, PatronID, Lent, Returned) " \
        "VALUES (:BookID, :PatronID, :Lent, :Returned)"
    sql_params = [
        {'name': 'BookID', 'value': {'longValue': 35}},
        {'name': 'PatronID', 'value': {'longValue': 405}},
        {'name': 'Lent', 'typeHint': 'DATE',
         'value': {'stringValue': str(datetime.date.today())}},
        {'name': 'Returned', 'value': {'isNull': True}}]
    lending_id = 5000

    rdsdata_stubber.stub_execute_statement(CLUSTER_ARN, SECRET_ARN, DB_NAME,
        sql, sql_params, generated_fields=[lending_id], error_code=error_code)

    if error_code is None:
        got_lending_id = storage.borrow_book(book_id, patron_id)
        assert got_lending_id == lending_id
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.borrow_book(book_id, patron_id)
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_return_book(make_stubber, error_code):
    storage, rdsdata_stubber = make_storage_n_stubber(make_stubber)
    book_id = 35
    patron_id = 405
    sql = \
        "UPDATE Lending SET Returned=:set_Returned " \
        "WHERE Lending.BookID = :Lending_BookID AND " \
        "Lending.PatronID = :Lending_PatronID AND " \
        "Lending.Returned IS :Lending_Returned"
    sql_params = [
        {'name': 'set_Returned', 'typeHint': 'DATE',
         'value': {'stringValue': str(datetime.date.today())}},
        {'name': 'Lending_BookID', 'value': {'longValue': 35}},
        {'name': 'Lending_PatronID', 'value': {'longValue': 405}},
        {'name': 'Lending_Returned', 'value': {'isNull': True}}]

    rdsdata_stubber.stub_execute_statement(CLUSTER_ARN, SECRET_ARN, DB_NAME,
        sql, sql_params, error_code=error_code)

    if error_code is None:
        storage.return_book(book_id, patron_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            storage.return_book(book_id, patron_id)
            assert exc_info.value.response['Error']['Code'] == error_code
