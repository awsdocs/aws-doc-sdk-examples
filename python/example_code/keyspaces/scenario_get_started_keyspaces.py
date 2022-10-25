# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Keyspaces (for Apache Cassandra)
to do the following:

* Create a keyspace.
* Create a table in the keyspace. The table is configured with a schema to hold movie data
  and has point-in-time recovery enabled.
* Connect to the keyspace with a connection secured by TLS and authenticated with
  Signature V4 (SigV4).
* Query the table by adding, retrieving, and updating movie data.
* Update the table by adding a column to track watched movies.
* Restore the table to a previous point in time.
* Delete the table and keyspace.
"""

from datetime import datetime
import logging
import os
from pprint import pp
import sys

import boto3
import requests

from query import QueryManager
from keyspace import KeyspaceWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
from demo_tools import demo_func
import demo_tools.question as q
from demo_tools.retries import wait

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.keyspaces.Scenario_GetStartedKeyspaces]
class KeyspaceScenario:
    """Runs an interactive scenario that shows how to get started using Amazon Keyspaces."""
    def __init__(self, ks_wrapper):
        """
        :param ks_wrapper: An object that wraps Amazon Keyspace actions.
        """
        self.ks_wrapper = ks_wrapper

    @demo_func
    def create_keyspace(self):
        """
        1. Creates a keyspace.
        2. Lists up to 10 keyspaces in your account.
        """
        print("Let's create a keyspace.")
        ks_name = q.ask(
            "Enter a name for your new keyspace.\nThe name can contain only letters, "
            "numbers and underscores: ", q.non_empty)
        if self.ks_wrapper.exists_keyspace(ks_name):
            print(f"A keyspace named {ks_name} exists.")
        else:
            ks_arn = self.ks_wrapper.create_keyspace(ks_name)
            ks_exists = False
            while not ks_exists:
                wait(3)
                ks_exists = self.ks_wrapper.exists_keyspace(ks_name)
            print(f"Created a new keyspace.\n\t{ks_arn}.")
        print("The first 10 keyspaces in your account are:\n")
        self.ks_wrapper.list_keyspaces(10)

    @demo_func
    def create_table(self):
        """
        1. Creates a table in the keyspace. The table is configured with a schema to hold
           movie data and has point-in-time recovery enabled.
        2. Waits for the table to be in an active state.
        3. Displays schema information for the table.
        4. Lists tables in the keyspace.
        """
        print("Let's create a table for movies in your keyspace.")
        table_name = q.ask("Enter a name for your table: ", q.non_empty)
        table = self.ks_wrapper.get_table(table_name)
        if table is not None:
            print(f"A table named {table_name} already exists in keyspace "
                  f"{self.ks_wrapper.ks_name}.")
        else:
            table_arn = self.ks_wrapper.create_table(table_name)
            print(f"Created table {table_name}:\n\t{table_arn}")
            table = {'status': None}
            print("Waiting for your table to be ready...")
            while table['status'] != 'ACTIVE':
                wait(5)
                table = self.ks_wrapper.get_table(table_name)
        print(f"Your table is {table['status']}. Its schema is:")
        pp(table['schemaDefinition'])
        print("\nThe tables in your keyspace are:\n")
        self.ks_wrapper.list_tables()

    @demo_func
    def ensure_tls_cert(self):
        """
        Ensures you have a TLS certificate available to use to secure the connection
        to the keyspace. This function downloads a default certificate or lets you
        specify your own.
        """
        print("To connect to your keyspace, you must have a TLS certificate.")
        print("Checking for TLS certificate...")
        cert_path = os.path.join(os.path.dirname(__file__), QueryManager.DEFAULT_CERT_FILE)
        if not os.path.exists(cert_path):
            if q.ask(f"Do you want to download one from {QueryManager.CERT_URL}? (y/n) ",
                     q.is_yesno):
                cert = requests.get(QueryManager.CERT_URL).text
                with open(cert_path, 'w') as cert_file:
                    cert_file.write(cert)
            else:
                cert_path = q.ask(
                    "Enter the full path to the TLS certificate you want to use: ", q.non_empty)
        print(f"Certificate {cert_path} will be used to secure the connection to your keyspace.")
        return cert_path

    @demo_func
    def query_table(self, qm):
        """
        1. Adds movies to the table from a sample movie data file.
        2. Gets a list of movies from the table and lets you select one.
        3. Displays more information about the selected movie.
        """
        qm.add_movies(self.ks_wrapper.table_name, '../../../resources/sample_files/movies.json')
        movies = qm.get_movies(self.ks_wrapper.table_name)
        print(f"Added {len(movies)} movies to the table:")
        sel = q.choose("Pick one to learn more about it: ", [m.title for m in movies])
        movie_choice = qm.get_movie(self.ks_wrapper.table_name, movies[sel].title, movies[sel].year)
        print(movie_choice.title)
        print(f"\tReleased: {movie_choice.release_date}")
        print(f"\tPlot: {movie_choice.plot}")

    @demo_func
    def update_and_restore_table(self, qm):
        """
        1. Updates the table by adding a column to track watched movies.
        2. Marks some of the movies as watched.
        3. Gets the list of watched movies from the table.
        4. Restores the table to a previous point in time.
        5. Gets the list of movies from the restored table.
        """
        print("Let's add a column to record which movies you've watched.")
        pre_update_timestamp = datetime.utcnow()
        print(f"Recorded the current UTC time of {pre_update_timestamp} so we can restore the table later.")
        self.ks_wrapper.update_table()
        print("Waiting for your table to update...")
        table = {'status': 'UPDATING'}
        while table['status'] != 'ACTIVE':
            wait(5)
            table = self.ks_wrapper.get_table(self.ks_wrapper.table_name)
        print("Column 'watched' added to table.")
        q.ask("Let's mark some of the movies as watched. Press Enter when you're ready.\n")
        movies = qm.get_movies(self.ks_wrapper.table_name)
        for movie in movies[:10]:
            qm.watched_movie(self.ks_wrapper.table_name, movie.title, movie.year)
            print(f"Marked {movie.title} as watched.")
        movies = qm.get_movies(self.ks_wrapper.table_name, watched=True)
        print('-'*88)
        print("The watched movies in our table are:\n")
        for movie in movies:
            print(movie.title)
        print('-'*88)
        if q.ask(
                "Do you want to restore the table to the way it was before all of these\n"
                "updates? Keep in mind, this can take up to 20 minutes. (y/n) ", q.is_yesno):
            table_name_restored = self.ks_wrapper.restore_table(pre_update_timestamp)
            table = {'status': 'RESTORING'}
            while table['status'] != 'ACTIVE':
                wait(10)
                table = self.ks_wrapper.get_table(table_name_restored)
            print(f"Restored {self.ks_wrapper.table_name} to {table_name_restored} "
                  f"at a point in time of {pre_update_timestamp}.")
            movies = qm.get_movies(table_name_restored)
            print("Now the movies in our table are:")
            for movie in movies:
                print(movie.title)

    def cleanup(self):
        """
        1. Deletes the table and waits for it to be removed.
        2. Deletes the keyspace.
        """
        if q.ask(f"Do you want to delete your {self.ks_wrapper.table_name} table and "
                 f"{self.ks_wrapper.ks_name} keyspace? (y/n) ", q.is_yesno):
            table_name = self.ks_wrapper.table_name
            self.ks_wrapper.delete_table()
            table = self.ks_wrapper.get_table(table_name)
            print("Waiting for the table to be deleted.")
            while table is not None:
                wait(5)
                table = self.ks_wrapper.get_table(table_name)
            print("Table deleted.")
            self.ks_wrapper.delete_keyspace()
            print("Keyspace deleted.")

    def run_scenario(self):
        logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

        print('-'*88)
        print("Welcome to the Amazon Keyspaces (for Apache Cassandra) demo.")
        print('-'*88)

        self.create_keyspace()
        self.create_table()
        cert_file_path = self.ensure_tls_cert()
        # Use a context manager to ensure the connection to the keyspace is closed.
        with QueryManager(
                cert_file_path, boto3.DEFAULT_SESSION, self.ks_wrapper.ks_name) as qm:
            self.query_table(qm)
            self.update_and_restore_table(qm)
        self.cleanup()

        print("\nThanks for watching!")
        print('-'*88)


if __name__ == '__main__':
    try:
        scenario = KeyspaceScenario(KeyspaceWrapper.from_client())
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong with the demo.")
# snippet-end:[python.example_code.keyspaces.Scenario_GetStartedKeyspaces]
