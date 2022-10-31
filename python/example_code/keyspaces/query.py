# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from datetime import date
import json
from ssl import SSLContext, PROTOCOL_TLSv1_2, CERT_REQUIRED

from cassandra.cluster import Cluster, ExecutionProfile, EXEC_PROFILE_DEFAULT, DCAwareRoundRobinPolicy
from cassandra import ConsistencyLevel
from cassandra.query import SimpleStatement
from cassandra_sigv4.auth import SigV4AuthProvider


# snippet-start:[python.example_code.keyspaces.QueryManager.class]
class QueryManager:
    """
    Manages queries to an Amazon Keyspaces (for Apache Cassandra) keyspace.
    Queries are secured by TLS and authenticated by using the Signature V4 (SigV4)
    AWS signing protocol. This is more secure than sending username and password
    with a plain-text authentication provider.

    This example downloads a default certificate to secure TLS, or lets you specify
    your own.

    This example uses a table of movie data to demonstrate basic queries.
    """
    DEFAULT_CERT_FILE = 'sf-class2-root.crt'
    CERT_URL = f'https://certs.secureserver.net/repository/sf-class2-root.crt'

    def __init__(self, cert_file_path, boto_session, keyspace_name):
        """
        :param cert_file_path: The path and file name of the certificate used for TLS.
        :param boto_session: A Boto3 session. This is used to acquire your AWS credentials.
        :param keyspace_name: The name of the keyspace to connect.
        """
        self.cert_file_path = cert_file_path
        self.boto_session = boto_session
        self.ks_name = keyspace_name
        self.cluster = None
        self.session = None

    def __enter__(self):
        """
        Creates a session connection to the keyspace that is secured by TLS and
        authenticated by SigV4.
        """
        ssl_context = SSLContext(PROTOCOL_TLSv1_2)
        ssl_context.load_verify_locations(self.cert_file_path)
        ssl_context.verify_mode = CERT_REQUIRED
        auth_provider = SigV4AuthProvider(self.boto_session)
        contact_point = f"cassandra.{self.boto_session.region_name}.amazonaws.com"
        exec_profile = ExecutionProfile(
            consistency_level=ConsistencyLevel.LOCAL_QUORUM,
            load_balancing_policy=DCAwareRoundRobinPolicy())
        self.cluster = Cluster(
            [contact_point], ssl_context=ssl_context, auth_provider=auth_provider,
            port=9142, execution_profiles={EXEC_PROFILE_DEFAULT: exec_profile},
            protocol_version=4)
        self.cluster.__enter__()
        self.session = self.cluster.connect(self.ks_name)
        return self

    def __exit__(self, *args):
        """
        Exits the cluster. This shuts down all existing session connections.
        """
        self.cluster.__exit__(*args)

    def add_movies(self, table_name, movie_file_path):
        """
        Gets movies from a JSON file and adds them to a table in the keyspace.

        :param table_name: The name of the table.
        :param movie_file_path: The path and file name of a JSON file that contains movie data.
        """
        with open(movie_file_path, 'r') as movie_file:
            movies = json.loads(movie_file.read())
        stmt = self.session.prepare(
            f"INSERT INTO {table_name} (year, title, release_date, plot) VALUES (?, ?, ?, ?);")
        for movie in movies[:20]:
            self.session.execute(stmt, parameters=[
                movie['year'], movie['title'],
                date.fromisoformat(movie['info']['release_date'].partition('T')[0]),
                movie['info']['plot']])

    def get_movies(self, table_name, watched=None):
        """
        Gets the title and year of the full list of movies from the table.

        :param table_name: The name of the movie table.
        :param watched: When specified, the returned list of movies is filtered to
                        either movies that have been watched or movies that have not
                        been watched. Otherwise, all movies are returned.
        :return: A list of movies in the table.
        """
        if watched is None:
            stmt = SimpleStatement(f"SELECT title, year from {table_name}")
            params = None
        else:
            stmt = SimpleStatement(
                f"SELECT title, year from {table_name} WHERE watched = %s ALLOW FILTERING")
            params = [watched]
        return self.session.execute(stmt, parameters=params).all()

    def get_movie(self, table_name, title, year):
        """
        Gets a single movie from the table, by title and year.

        :param table_name: The name of the movie table.
        :param title: The title of the movie.
        :param year: The year of the movie's release.
        :return: The requested movie.
        """
        return self.session.execute(
            SimpleStatement(f"SELECT * from {table_name} WHERE title = %s AND year = %s"),
            parameters=[title, year]).one()

    def watched_movie(self, table_name, title, year):
        """
        Updates a movie as having been watched.

        :param table_name: The name of the movie table.
        :param title: The title of the movie.
        :param year: The year of the movie's release.
        """
        self.session.execute(
            SimpleStatement(f"UPDATE {table_name} SET watched=true WHERE title = %s AND year = %s"),
            parameters=[title, year])
# snippet-end:[python.example_code.keyspaces.QueryManager.class]
