# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


import unittest
from unittest.mock import MagicMock, patch
from botocore.exceptions import ClientError
from neptune_scenario import describe_db_clusters

class TestDescribeDbClusters(unittest.TestCase):

    def setUp(self):
        self.cluster_id = "test-cluster"
        self.mock_client = MagicMock()

    def test_cluster_found_and_prints_info(self):
        # Simulate successful describe with one DBCluster
        mock_response = [{
            'DBClusters': [{
                'DBClusterIdentifier': 'test-cluster',
                'Status': 'available',
                'Engine': 'neptune',
                'EngineVersion': '1.2.0.0',
                'Endpoint': 'test-endpoint',
                'ReaderEndpoint': 'reader-endpoint',
                'AvailabilityZones': ['us-east-1a'],
                'DBSubnetGroup': 'default',
                'VpcSecurityGroups': [{'VpcSecurityGroupId': 'sg-12345'}],
                'StorageEncrypted': True,
                'IAMDatabaseAuthenticationEnabled': True,
                'BackupRetentionPeriod': 7,
                'PreferredBackupWindow': '07:00-09:00',
                'PreferredMaintenanceWindow': 'sun:05:00-sun:09:00'
            }]
        }]
        paginator_mock = MagicMock()
        paginator_mock.paginate.return_value = mock_response
        self.mock_client.get_paginator.return_value = paginator_mock

        # Just run the function and ensure no exception
        describe_db_clusters(self.mock_client, self.cluster_id)

        self.mock_client.get_paginator.assert_called_with('describe_db_clusters')
        paginator_mock.paginate.assert_called_with(DBClusterIdentifier=self.cluster_id)

    def test_cluster_not_found_raises_client_error(self):
        # Simulate paginator returning empty DBClusters
        mock_response = [{'DBClusters': []}]
        paginator_mock = MagicMock()
        paginator_mock.paginate.return_value = mock_response
        self.mock_client.get_paginator.return_value = paginator_mock

        with self.assertRaises(ClientError) as cm:
            describe_db_clusters(self.mock_client, self.cluster_id)

        err = cm.exception.response['Error']
        self.assertEqual(err['Code'], 'DBClusterNotFound')

    def test_client_error_from_paginate_is_propagated(self):
        # Simulate paginator throwing ClientError
        paginator_mock = MagicMock()
        paginator_mock.paginate.side_effect = ClientError(
            {"Error": {"Code": "AccessDeniedException", "Message": "Denied"}},
            "DescribeDBClusters"
        )
        self.mock_client.get_paginator.return_value = paginator_mock

        with self.assertRaises(ClientError) as cm:
            describe_db_clusters(self.mock_client, self.cluster_id)

        self.assertEqual(cm.exception.response['Error']['Code'], 'AccessDeniedException')


if __name__ == "__main__":
    unittest.main()
