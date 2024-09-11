# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Redshift unit tests.
"""


from test_tools.example_stubber import ExampleStubber


class RedshiftStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Redshift unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Step Functions client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_cluster(
        self,
        cluster_identifier,
        node_type,
        master_username,
        master_user_password,
        publiclyAccessible,
        numberOfNodes,
        error_code=None,
    ):
        expected_params = {
            "ClusterIdentifier": cluster_identifier,
            "NodeType": node_type,
            "MasterUsername": master_username,
            "MasterUserPassword": master_user_password,
            "PubliclyAccessible": publiclyAccessible,
            "NumberOfNodes": numberOfNodes,
        }
        response = {
            "Cluster": {
                "ClusterIdentifier": cluster_identifier,
                "NodeType": node_type,
                "MasterUsername": master_username,
                "PubliclyAccessible": publiclyAccessible,
                "NumberOfNodes": numberOfNodes,
            }
        }
        self._stub_bifurcator(
            "create_cluster", expected_params, response, error_code=error_code
        )

    def stub_delete_cluster(self, cluster_identifier, error_code=None):
        expected_params = {
            "ClusterIdentifier": cluster_identifier,
            "SkipFinalClusterSnapshot": True,
        }
        response = {"Cluster": {"ClusterIdentifier": cluster_identifier}}
        self._stub_bifurcator(
            "delete_cluster", expected_params, response, error_code=error_code
        )

    def stub_modify_cluster(
        self, cluster_identifier, preferred_maintenance_window, error_code=None
    ):
        expected_params = {
            "ClusterIdentifier": cluster_identifier,
            "PreferredMaintenanceWindow": preferred_maintenance_window,
        }
        response = {
            "Cluster": {
                "ClusterIdentifier": cluster_identifier,
                "PreferredMaintenanceWindow": preferred_maintenance_window,
            }
        }
        self._stub_bifurcator(
            "modify_cluster", expected_params, response, error_code=error_code
        )

    def stub_describe_clusters(self, cluster_identifier, error_code=None):
        expected_params = {"ClusterIdentifier": cluster_identifier}
        response = {
            "Clusters": [
                {
                    "ClusterIdentifier": cluster_identifier,
                    "NodeType": "dc2.large",
                    "MasterUsername": "XXXXX",
                }
            ]
        }
        self._stub_bifurcator(
            "describe_clusters", expected_params, response, error_code=error_code
        )
