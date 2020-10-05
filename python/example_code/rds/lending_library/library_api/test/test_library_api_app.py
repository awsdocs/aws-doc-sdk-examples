# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the app.py functions. These tests use the chalice.test.Client
class provided by AWS Chalice to simplify route testing.
"""

import json
from unittest.mock import MagicMock
import pytest
from chalice.app import RequestTimeoutError
from chalice.test import Client
import app
from chalicelib.library_data import DataServiceNotReadyException


@pytest.fixture
def mock_storage(monkeypatch):
    _storage = MagicMock(
        get_books=MagicMock(return_value=['book1', 'book2']),
        add_book=MagicMock(return_value=('author1', 'book1')),
        get_authors=MagicMock(return_value=['author1', 'author2']),
        get_patrons=MagicMock(return_value=['patron1', 'patron2']),
        add_patron=MagicMock(return_value='patron1'),
        delete_patron=MagicMock(return_value='delete_patron'),
        get_borrowed_books=MagicMock(return_value=['book1', 'book2']),
        borrow_book=MagicMock(return_value='borrow_book'),
        return_book=MagicMock(return_value='return_book')
    )
    monkeypatch.setattr(app, 'get_storage', lambda: _storage)
    return _storage


def test_index():
    with Client(app.app) as client:
        response = client.http.get('/')
        assert response.json_body == {
            'description': 'A simple lending library REST API that runs entirely on '
            'serverless components.'}


def test_list_books(mock_storage):
    with Client(app.app) as client:
        response = client.http.get('/books')
        mock_storage.get_books.assert_called_with()
        assert response.json_body == {'books': ['book1', 'book2']}


def test_list_books_timeout(mock_storage):
    mock_storage.get_books = MagicMock(
        side_effect=DataServiceNotReadyException('Timeout test'))

    with Client(app.app) as client:
        response = client.http.get('/books')
        assert response.status_code == 408


def test_add_book(mock_storage):
    with Client(app.app) as client:
        test_book = {'Books.Title': 'test-book'}
        response = client.http.post(
            '/books', headers={'content-type': 'application/json'},
            body=json.dumps(test_book))
        mock_storage.add_book.assert_called_with(test_book)
        assert response.json_body == {
            'Authors.AuthorID': 'author1', 'Books.BookID': 'book1'}


def test_list_books_by_author(mock_storage):
    with Client(app.app) as client:
        author_id = 15
        response = client.http.get(f'/books/{author_id}')
        mock_storage.get_books.assert_called_with(author_id=author_id)
        assert response.json_body == {'books': ['book1', 'book2']}


def test_list_authors(mock_storage):
    with Client(app.app) as client:
        response = client.http.get('/authors')
        mock_storage.get_authors.assert_called_with()
        assert response.json_body == {'authors': ['author1', 'author2']}


def test_list_patrons(mock_storage):
    with Client(app.app) as client:
        response = client.http.get('/patrons')
        mock_storage.get_patrons.assert_called_with()
        assert response.json_body == {'patrons': ['patron1', 'patron2']}


def test_add_patron(mock_storage):
    with Client(app.app) as client:
        patron = {'Patrons.FirstName': 'Pierre'}
        response = client.http.post(
            '/patrons', headers={'content-type': 'application/json'},
            body=json.dumps(patron))
        mock_storage.add_patron.assert_called_with(patron)
        assert response.json_body == {'Patrons.PatronID': 'patron1'}


def test_delete_patron(mock_storage):
    with Client(app.app) as client:
        patron_id = 55
        client.http.delete(f'/patrons/{patron_id}')
        mock_storage.delete_patron.assert_called_with(patron_id)


def test_list_borrowed_books(mock_storage):
    with Client(app.app) as client:
        response = client.http.get('/lending')
        mock_storage.get_borrowed_books.assert_called_with()
        assert response.json_body == {'books': ['book1', 'book2']}


def test_borrow_book(mock_storage):
    with Client(app.app) as client:
        book_id = 5
        patron_id = 13
        client.http.put(f'/lending/{book_id}/{patron_id}')
        mock_storage.borrow_book(book_id, patron_id)


def test_return_book(mock_storage):
    with Client(app.app) as client:
        book_id = 5
        patron_id = 13
        client.http.delete(f'/lending/{book_id}/{patron_id}')
        mock_storage.return_book(book_id, patron_id)
