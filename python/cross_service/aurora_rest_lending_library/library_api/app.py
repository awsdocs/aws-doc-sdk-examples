# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement routing for a lending library REST API that uses AWS Chalice.
Book, patron, and lending data is accessed through a serverless Amazon Aurora database.

This file is deployed to AWS Lambda as part of the Chalice deployment.
"""

import logging
import urllib.parse
from chalice import Chalice
from chalice.app import RequestTimeoutError
import chalicelib.library_data

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

app = Chalice(app_name='library_api')
app.debug = True  # Set this to False for production use.

_STORAGE = None


def get_storage():
    """Creates or gets the storage object that calls the database."""
    global _STORAGE
    if _STORAGE is None:
        _STORAGE = chalicelib.library_data.Storage.from_env()
    return _STORAGE


def storage_timeout(func):
    def _timeout(*args, **kwargs):
        try:
            result = func(*args, **kwargs)
        except chalicelib.library_data.DataServiceNotReadyException as err:
            raise RequestTimeoutError(err)
        else:
            return result
    return _timeout


@app.route('/')
def index():
    """Briefly describes the REST API."""
    return {'description': 'A simple lending library REST API that runs entirely on '
                           'serverless components.'}


@app.route('/books', methods=['GET'])
@storage_timeout
def list_books():
    """
    Lists the books in the library.

    :return: The list of books.
    """
    return {'books': get_storage().get_books()}


@app.route('/books', methods=['POST'])
@storage_timeout
def add_book():
    """
    Adds a book to the library. The author is also added.
    The book must be in the request body as JSON in the following format:
        {
            "Books.Title": "Title of the Book",
            "Authors.FirstName": "Allison",
            "Authors.LastName": "Author"
        }

    :return: The IDs of the added author and book.
    """
    author_id, book_id = get_storage().add_book(app.current_request.json_body)
    return {'Authors.AuthorID': author_id, 'Books.BookID': book_id}


@app.route('/books/{author_id}', methods=['GET'])
@storage_timeout
def list_books_by_author(author_id):
    """
    Lists books in the library written by the specified author.

    :param author_id: The ID of the author to query.
    :return: The list of books written by the specified author.
    """
    author_id = int(urllib.parse.unquote(author_id))
    return {'books': get_storage().get_books(author_id=author_id)}


@app.route('/authors', methods=['GET'])
@storage_timeout
def list_authors():
    """
    Lists the authors in the library.

    :return: The list of authors.
    """
    return {'authors': get_storage().get_authors()}


@app.route('/patrons', methods=['GET'])
@storage_timeout
def list_patrons():
    """
    Lists the patrons of the library.

    :return: The list of patrons.
    """
    return {'patrons': get_storage().get_patrons()}


@app.route('/patrons', methods=['POST'])
@storage_timeout
def add_patron():
    """
    Adds a patron to the library.
    Patrons must be in the request body as JSON in the following format:
        {
            "Patrons.FirstName": "Paulo",
            "Patrons.LastName": "Patron"
        }

    :return: The ID of the added patron.
    """
    patron_id = get_storage().add_patron(app.current_request.json_body)
    return {'Patrons.PatronID': patron_id}


@app.route('/patrons/{patron_id}', methods=['DELETE'])
@storage_timeout
def delete_patron(patron_id):
    """
    Removes a patron from the library.

    :param patron_id: The ID of the patron to remove.
    """
    patron_id = int(urllib.parse.unquote(patron_id))
    get_storage().delete_patron(patron_id)


@app.route('/lending', methods=['GET'])
@storage_timeout
def list_borrowed_books():
    """
    Lists the books that are currently lent out from the library.

    :return: The list of currently borrowed books.
    """
    return {'books': get_storage().get_borrowed_books()}


@app.route('/lending/{book_id}/{patron_id}', methods=['PUT', 'DELETE'])
@storage_timeout
def book_lending(book_id, patron_id):
    """
    Borrows or returns a book.
    To borrow a book, PUT the book ID and the patron ID.
    To return a book, DELETE the bookID and the patron ID.

    :param book_id: The ID of the book.
    :param patron_id: The ID of the patron.
    """
    book_id = int(urllib.parse.unquote(book_id))
    patron_id = int(urllib.parse.unquote(patron_id))
    if app.current_request.method == 'PUT':
        get_storage().borrow_book(book_id, patron_id)
    elif app.current_request.method == 'DELETE':
        get_storage().return_book(book_id, patron_id)
