# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the Amazon RDS Data Service to interact with an Amazon Aurora
database.

This file is deployed to AWS Lambda as part of the Chalice deployment.
"""

import datetime
import logging
import os
import boto3
from botocore.exceptions import ClientError
from .mysql_helper import Table, Column, ForeignKey
from .mysql_helper import (
    create_table, insert, update, query, unpack_query_results, unpack_insert_results,
    delete)


logger = logging.getLogger(__name__)


class DataServiceNotReadyException(Exception):
    pass


class Storage:
    """
    Wraps calls to the Amazon RDS Data Service.
    """
    def __init__(self, cluster, secret, db_name, rdsdata_client):
        """
        Initialize the storage object.

        Also initializes all of the definitions of the library tables.

        :param cluster: The Amazon Aurora cluster that contains the library database.
        :param secret: The AWS Secrets Manager secret that contains credentials used
                       to connect to the database.
        :param db_name: The name of the library database.
        :param rdsdata_client: The Boto3 RDS Data Service client.
        """
        self._cluster = cluster
        self._secret = secret
        self._db_name = db_name
        self._rdsdata_client = rdsdata_client
        self._tables = {
            'Authors': Table('Authors', [
                Column('AuthorID', int, nullable=False, auto_increment=True,
                       primary_key=True),
                Column('FirstName', str, nullable=False),
                Column('LastName', str, nullable=False)
            ]),
            'Books': Table('Books', [
                Column('BookID', int, nullable=False, auto_increment=True,
                       primary_key=True),
                Column('Title', str, nullable=False),
                Column('AuthorID', int, foreign_key=ForeignKey('Authors', 'AuthorID'))
            ]),
            'Patrons': Table('Patrons', [
                Column('PatronID', int, nullable=False, auto_increment=True,
                       primary_key=True),
                Column('FirstName', str, nullable=False),
                Column('LastName', str, nullable=False),
            ]),
            'Lending': Table('Lending', [
                Column('LendingID', int, nullable=False, auto_increment=True,
                       primary_key=True),
                Column('BookID', int, foreign_key=ForeignKey('Books', 'BookID')),
                Column('PatronID', int, foreign_key=ForeignKey('Patrons', 'PatronID')),
                Column('Lent', datetime.date, nullable=False),
                Column('Returned', datetime.date)
            ])}

    @classmethod
    def from_env(cls):
        """
        Creates a storage object based on environment variables.
        """
        cluster_name = os.environ.get('CLUSTER_NAME', '')
        secret_name = os.environ.get('SECRET_NAME', '')
        db_name = os.environ.get('DATABASE_NAME', '')
        cluster = boto3.client('rds').describe_db_clusters(
            DBClusterIdentifier=cluster_name)['DBClusters'][0]
        secret = boto3.client('secretsmanager').describe_secret(
            SecretId=secret_name)
        rdsdata_client = boto3.client('rds-data')
        return cls(cluster, secret, db_name, rdsdata_client)

    def _begin_transaction(self):
        """
        Begins a database transaction.

        :return: The transaction ID.
        """
        result = self._rdsdata_client.begin_transaction(
            database=self._db_name,
            resourceArn=self._cluster['DBClusterArn'],
            secretArn=self._secret['ARN'])
        return result['transactionId']

    def _commit_transaction(self, transaction_id):
        """
        Commits a database transaction.

        :return: The result of committing the transaction.
        """
        result = self._rdsdata_client.commit_transaction(
            resourceArn=self._cluster['DBClusterArn'],
            secretArn=self._secret['ARN'],
            transactionId=transaction_id)
        return result['transactionStatus']

    def _rollback_transaction(self, transaction_id):
        """
        Rolls back a database transaction.

        :return: The result of rolling back the transaction.
        """
        result = self._rdsdata_client.rollback_transaction(
            resourceArn=self._cluster['DBClusterArn'],
            secretArn=self._secret['ARN'],
            transactionId=transaction_id)
        return result['transactionStatus']

    def _run_statement(self, sql, sql_params=None, transaction_id=None):
        """
        Runs a SQL statement and associated parameters using RDS Data Service.

        :param sql: The SQL statement to run.
        :param sql_params: The parameters associated with the SQL statement.
        :transaction_id: The ID of a previously created transaction.
        :return: The result of running the SQL statement.
        """
        try:
            run_args = {
                'database': self._db_name,
                'resourceArn': self._cluster['DBClusterArn'],
                'secretArn': self._secret['ARN'],
                'sql': sql
            }
            if sql_params is not None:
                run_args['parameters'] = sql_params
            if transaction_id is not None:
                run_args['transactionId'] = transaction_id
            result = self._rdsdata_client.execute_statement(**run_args)
            logger.info("Ran statement on %s.", self._db_name)
        except ClientError as error:
            if (error.response['Error']['Code'] == 'BadRequestException'
                    and 'Communications link failure'
                    in error.response['Error']['Message']):
                raise DataServiceNotReadyException(
                    'The Aurora Data Service is not ready, probably because it entered '
                    'pause mode after five minutes of inactivity. Wait a minute for '
                    'your cluster to resume and try your request again.') from error
            logger.exception("Run statement on %s failed.", self._db_name)
            raise
        else:
            return result

    def _run_batch_statement(self, sql, sql_param_sets):
        """
        Runs a batch SQL statement and associated parameter sets using RDS Data Service.

        :param sql: The SQL statement to run.
        :param sql_param_sets: The parameter sets associated with the SQL statement.
                               Each parameter set represents an item in the batch.
        :return: The result of running the batch SQL statement.
        """
        try:
            run_args = {
                'database': self._db_name,
                'resourceArn': self._cluster['DBClusterArn'],
                'secretArn': self._secret['ARN'],
                'sql': sql,
                'parameterSets': sql_param_sets
            }
            result = self._rdsdata_client.batch_execute_statement(**run_args)
            logger.info("Ran batch statement on %s.", self._db_name)
        except ClientError:
            logger.exception("Run batch statement on %s failed.", self._db_name)
            raise
        else:
            return result

    def bootstrap_tables(self):
        """
        Creates tables in the database. The tables are defined in the constructor.
        """
        for table in self._tables.values():
            logger.info("Creating table %s.", table.name)
            sql = create_table(table)
            self._run_statement(sql)

    def add_books(self, books):
        """
        Adds a list of books and their authors to the database. The list of authors
        is first processed to remove duplicates.

        :param books: The list of books and their authors to add to the database.
        :return: The counts of authors and books added to the database.
        """
        authors = {
            book['author']: {
                'FirstName': ' '.join(book['author'].split(' ')[:-1]),
                'LastName': book['author'].split(' ')[-1]
            } for book in books
        }
        sql, sql_param_sets = insert(
            self._tables['Authors'], authors.values())
        result = self._run_batch_statement(sql, sql_param_sets)
        author_count = len(result['updateResults'])
        logger.info("Added %s authors to the database.", author_count)

        auth_ids = [field['generatedFields'][0]['longValue']
                    for field in result['updateResults']]
        for auth, auth_id in zip(authors.values(), auth_ids):
            auth['author_id'] = auth_id
        sql, sql_param_sets = insert(
            self._tables['Books'], [{
                'Title': book['title'],
                'AuthorID': authors[book['author']]['author_id']
            } for book in books])
        result = self._run_batch_statement(sql, sql_param_sets)
        book_count = len(result['updateResults'])
        logger.info("Added %s books to the database.", book_count)
        return author_count, book_count

    def get_books(self, author_id=None):
        """
        Gets books from the database.

        :param author_id: When specified, only books by this author are returned.
                          Otherwise, all books are returned.
        :returns: The list of books.
        """
        logger.info("Listing by author %s.", "All" if author_id is None else author_id)
        where_clauses = None if author_id is None else [{
            'table': 'Authors', 'column': 'AuthorID', 'op': '=',
            'value': author_id}]
        sql, columns, params = query('Books', self._tables, where_clauses)
        results = self._run_statement(sql, sql_params=params)
        output = unpack_query_results(columns, results)
        return output

    def add_book(self, book):
        """
        Adds a book and its author to the database. This function uses a database
        transaction to ensure that both the author and the book are added. If one
        of the inserts fails, the transaction is rolled back and nothing is
        added.

        :param book: The book and author to add.
        :return: The IDs of the added author and book.
        """
        logger.info("Adding book %s to the library.", book)
        auth_sql, auth_sql_param_sets = insert(self._tables['Authors'], [{
            'FirstName': book['Authors.FirstName'],
            'LastName': book['Authors.LastName']
        }])
        results = None
        transaction_id = self._begin_transaction()
        try:
            logger.info("Started transaction %s.", transaction_id)
            auth_results = self._run_statement(
                auth_sql, sql_params=auth_sql_param_sets[0],
                transaction_id=transaction_id)
            author_id = unpack_insert_results(auth_results)
            book_sql, book_sql_param_sets = insert(
                self._tables['Books'], [{
                    'Title': book['Books.Title'],
                    'AuthorID': author_id}])
            book_results = self._run_statement(
                book_sql, sql_params=book_sql_param_sets[0],
                transaction_id=transaction_id)
            book_id = unpack_insert_results(book_results)
            results = author_id, book_id
        except Exception:
            transaction_status = self._rollback_transaction(transaction_id)
            logger.warning(
                "Transaction %s rolled back with status %s.", transaction_id,
                transaction_status)
        else:
            transaction_status = self._commit_transaction(transaction_id)
            logger.info(
                "Transaction %s commited with status %s.", transaction_id,
                transaction_status)

        return results

    def get_authors(self):
        """
        Gets the authors in the database.

        :return: The authors in the database.
        """
        logger.info("Listing all authors.")
        sql, columns, _ = query('Authors', self._tables)
        results = self._run_statement(sql)
        output = unpack_query_results(columns, results)
        return output

    def get_patrons(self):
        """
        Gets the patrons in the database.

        :return: The patrons in the database.
        """
        logger.info("Listing all patrons.")
        sql, columns, _ = query('Patrons', self._tables)
        results = self._run_statement(sql)
        output = unpack_query_results(columns, results)
        return output

    def add_patron(self, patron):
        """
        Adds a patron to the database.

        :return: The ID of the added patron.
        """
        logger.info("Adding patron %s.", patron)
        sql, sql_param_sets = insert(self._tables['Patrons'], [patron])
        results = self._run_statement(sql, sql_params=sql_param_sets[0])
        return unpack_insert_results(results)

    def delete_patron(self, patron_id):
        """
        Deletes a patron from the database.

        :param patron_id: The ID of the patron to delete.
        """
        logger.info("Deleting patron %s.", patron_id)
        sql, sql_param_sets = delete(
            self._tables['Patrons'], [{'PatronID': patron_id}])
        self._run_statement(sql, sql_params=sql_param_sets[0])

    def get_borrowed_books(self):
        """
        Gets a list of books currently borrowed from the library. A borrowed book
        is one that has an entry in the Lending table where its 'Lent' date is in the
        past and it has no 'Returned' date.

        :return: The list of currently borrowed books.
        """
        logger.info("Listing all currently borrowed books.")
        sql, columns, params = query('Lending', self._tables, [{
            'table': 'Lending', 'column': 'Lent', 'op': '>=',
            'value': str(datetime.date.today())
        }, {
            'table': 'Lending', 'column': 'Returned', 'op': 'IS', 'value': None
        }])
        results = self._run_statement(sql, sql_params=params)
        return unpack_query_results(columns, results)

    def borrow_book(self, book_id, patron_id):
        """
        Records a book as borrowed by adding a record to the Lending table with
        the current date and no Returned date.

        :param book_id: The ID of the book to borrow.
        :param patron_id: The ID of the patron who is borrowing the book.
        :return: The ID of the record in the Lending table.
        """
        logger.info("Lending book %s to patron %s.", book_id, patron_id)
        sql, sql_param_sets = insert(
            self._tables['Lending'], [{
                'BookID': book_id,
                'PatronID': patron_id,
                'Lent': datetime.date.today(),
                'Returned': None
            }]
        )
        results = self._run_statement(sql, sql_params=sql_param_sets[0])
        return unpack_insert_results(results)

    def return_book(self, book_id, patron_id):
        """
        Returns a book to the library by updating the record in the Lending table so
        that its Returned column contains the current date.

        :param book_id: The ID of the book to return.
        :param patron_id: The ID of the patron who is returning the book.
        """
        logger.info("Returning book %s from patron %s.", book_id, patron_id)
        sql, sql_params = update(
            'Lending', {'Returned': datetime.date.today()}, [{
                'table': 'Lending', 'column': 'BookID', 'op': '=',
                'value': book_id
            }, {
                'table': 'Lending', 'column': 'PatronID', 'op': '=',
                'value': patron_id
            }, {
                'table': 'Lending', 'column': 'Returned', 'op': 'IS', 'value': None
            }])
        self._run_statement(sql, sql_params)
