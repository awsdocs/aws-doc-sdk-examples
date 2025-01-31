# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import os
import sys
import unittest
from unittest.mock import MagicMock, patch

import pytest

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from cleanup import clean_s3_object_locking
from demo import demo_s3_object_locking
from deploy import deploy_s3_object_locking
from main import main
from s3_operations import create_buckets, populate_buckets, update_retention_policy


@pytest.mark.unit
class TestCLIApplication(unittest.TestCase):
    @patch("main.input", create=True)
    @patch("deploy.deploy_s3_object_locking")
    @patch("demo.demo_s3_object_locking")
    @patch("cleanup.clean_s3_object_locking")
    def test_main(self, mock_cleanup, mock_demo, mock_deploy, mock_input):
        mock_input.side_effect = ["", "", ""]
        main()
        mock_deploy.assert_called_once()
        mock_demo.assert_called_once()
        mock_cleanup.assert_called_once()

    @patch("deploy.boto3.client")
    @patch("s3_operations.create_buckets")
    @patch("s3_operations.populate_buckets")
    @patch("s3_operations.update_retention_policy")
    def test_deploy_s3_object_locking(
        self,
        mock_update_retention_policy,
        mock_populate_buckets,
        mock_create_buckets,
        mock_boto3_client,
    ):
        mock_client = MagicMock()
        mock_boto3_client.return_value = mock_client
        mock_create_buckets.return_value = {"retention": "test-bucket"}

        result = deploy_s3_object_locking()

        mock_create_buckets.assert_called_once_with(mock_client)
        mock_update_retention_policy.assert_called_once_with(mock_client, "test-bucket")
        mock_populate_buckets.assert_called_once_with(
            mock_client, {"retention": "test-bucket"}
        )
        self.assertEqual(result, {"retention": "test-bucket"})

    @patch("demo.boto3.client")
    @patch("demo.read_bucket_names")
    @patch("demo.set_legal_hold")
    @patch("demo.set_retention")
    def test_demo_s3_object_locking(
        self,
        mock_set_retention,
        mock_set_legal_hold,
        mock_read_bucket_names,
        mock_boto3_client,
    ):
        mock_client = MagicMock()
        mock_boto3_client.return_value = mock_client
        mock_read_bucket_names.return_value = {
            "lock_enabled": "bucket1",
            "retention": "bucket2",
        }

        demo_s3_object_locking()

        mock_read_bucket_names.assert_called_once()
        mock_set_legal_hold.assert_any_call(mock_client, "bucket1", "file0.txt")
        mock_set_retention.assert_any_call(mock_client, "bucket1", "file1.txt", 1)
        mock_set_legal_hold.assert_any_call(mock_client, "bucket2", "file0.txt")
        mock_set_retention.assert_any_call(mock_client, "bucket2", "file1.txt", 1)

    @patch("cleanup.boto3.client")
    @patch("cleanup.remove_object_locks_and_delete")
    @patch("cleanup.disable_bucket_object_lock_configuration")
    @patch("builtins.open")
    def test_clean_s3_object_locking(
        self,
        mock_open,
        mock_disable_bucket,
        mock_remove_object_locks,
        mock_boto3_client,
    ):
        mock_file = MagicMock()
        mock_file.__enter__.return_value = ["lock_enabled=bucket1", "retention=bucket2"]
        mock_open.return_value = mock_file

        mock_client = MagicMock()
        mock_boto3_client.return_value = mock_client

        mock_client.list_object_versions.side_effect = [
            {
                "Versions": [{"Key": "file1.txt", "VersionId": "v1"}],
                "DeleteMarkers": [],
            },
            {
                "Versions": [{"Key": "file2.txt", "VersionId": "v2"}],
                "DeleteMarkers": [],
            },
        ]
        mock_remove_object_locks.return_value = "Success"
        mock_disable_bucket.return_value = "Success"

        clean_s3_object_locking()

        mock_open.assert_called_once_with("buckets.txt", "r")
        mock_client.list_object_versions.assert_any_call(Bucket="bucket1")
        mock_client.list_object_versions.assert_any_call(Bucket="bucket2")
        mock_remove_object_locks.assert_any_call(
            mock_client, "bucket1", "file1.txt", "v1"
        )
        mock_remove_object_locks.assert_any_call(
            mock_client, "bucket2", "file2.txt", "v2"
        )
        mock_disable_bucket.assert_any_call(mock_client, "bucket1")
        mock_disable_bucket.assert_any_call(mock_client, "bucket2")
        mock_client.delete_bucket.assert_any_call(Bucket="bucket1")
        mock_client.delete_bucket.assert_any_call(Bucket="bucket2")


@pytest.mark.integ
class TestIntegrationCLIApplication(unittest.TestCase):
    @patch("boto3.client")
    def test_integration_deploy_demo_cleanup(self, mock_boto3_client):
        mock_client = MagicMock()
        mock_boto3_client.return_value = mock_client

        # Mock the return values and behaviors
        # You can fill in with more detailed behavior as needed
        deploy_s3_object_locking()
        demo_s3_object_locking()
        clean_s3_object_locking()

        # Assertions for integration test
        # You can add more detailed assertions as needed
        self.assertTrue(mock_boto3_client.called)


if __name__ == "__main__":
    unittest.main()
