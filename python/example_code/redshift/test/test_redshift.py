# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Redshift functions.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from redshift import RedshiftWrapper


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_cluster(make_stubber, error_code):
    redshift_client = boto3.client("redshift")
    redshift_stubber = make_stubber(redshift_client)
    wrapper = RedshiftWrapper(redshift_client)
    cluster_identifier = "mycluster"
    node_type = "dc2.large"
    master_username = "XXXXXX"
    master_user_password = "XXXXXX"
    publicly_accessible = True
    number_of_nodes = 2

    redshift_stubber.stub_create_cluster(
        cluster_identifier,
        node_type,
        master_username,
        master_user_password,
        publicly_accessible,
        number_of_nodes,
        error_code=error_code,
    )

    if error_code is None:
        cluster = wrapper.create_cluster(
            cluster_identifier,
            node_type,
            master_username,
            master_user_password,
            publicly_accessible,
            number_of_nodes,
        )

        assert cluster["Cluster"]["ClusterIdentifier"] == cluster_identifier
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_cluster(
                cluster_identifier,
                node_type,
                master_username,
                master_user_password,
                publicly_accessible,
                number_of_nodes,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_cluster(make_stubber, error_code):
    redshift_client = boto3.client("redshift")
    redshift_stubber = make_stubber(redshift_client)
    wrapper = RedshiftWrapper(redshift_client)
    cluster_identifier = "mycluster"

    redshift_stubber.stub_delete_cluster(cluster_identifier, error_code=error_code)

    if error_code is None:
        wrapper.delete_cluster(cluster_identifier)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_cluster(cluster_identifier)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_modify_cluster(make_stubber, error_code):
    redshift_client = boto3.client("redshift")
    redshift_stubber = make_stubber(redshift_client)
    wrapper = RedshiftWrapper(redshift_client)
    cluster_identifier = "mycluster"
    preferred_maintenance_window = "Mon:00:00-Mon:03:00"

    redshift_stubber.stub_modify_cluster(
        cluster_identifier, preferred_maintenance_window, error_code=error_code
    )

    if error_code is None:
        wrapper.modify_cluster(cluster_identifier, preferred_maintenance_window)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.modify_cluster(cluster_identifier, preferred_maintenance_window)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_clusters(make_stubber, error_code):
    redshift_client = boto3.client("redshift")
    redshift_stubber = make_stubber(redshift_client)
    wrapper = RedshiftWrapper(redshift_client)
    cluster_identifier = "mycluster"

    redshift_stubber.stub_describe_clusters(cluster_identifier, error_code=error_code)

    if error_code is None:
        clusters = wrapper.describe_clusters(cluster_identifier)
        assert clusters["Clusters"][0]["ClusterIdentifier"] == cluster_identifier
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_clusters(cluster_identifier)
        assert exc_info.value.response["Error"]["Code"] == error_code
