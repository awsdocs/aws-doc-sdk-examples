# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Relational Database Service (Amazon RDS)
unit tests.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber

class RdsStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon RDS unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Amazon RDS client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_db_cluster(
            self, cluster_name, db_name, admin_name, admin_password, error_code=None):
        expected_params = {
            'DatabaseName': db_name,
            'DBClusterIdentifier': cluster_name,
            'Engine': ANY,
            'EngineMode': ANY,
            'MasterUsername': admin_name,
            'MasterUserPassword': admin_password,
            'EnableHttpEndpoint': ANY}
        response = {'DBCluster': {
            'DatabaseName': db_name, 'DBClusterIdentifier': cluster_name}}
        self._stub_bifurcator(
            'create_db_cluster', expected_params, response, error_code=error_code)

    def stub_delete_db_cluster(self, cluster_name, error_code=None):
        expected_params = {
            'DBClusterIdentifier': cluster_name, 'SkipFinalSnapshot': True}
        self._stub_bifurcator(
            'delete_db_cluster', expected_params, error_code=error_code)
