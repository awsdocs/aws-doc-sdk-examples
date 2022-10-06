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
            self, cluster_name, db_name, admin_name, admin_password, engine=ANY, engine_mode=ANY,
            enable_http=ANY, group_name=None, engine_version=None, error_code=None):
        expected_params = {
            'DatabaseName': db_name,
            'DBClusterIdentifier': cluster_name,
            'Engine': engine,
            'MasterUsername': admin_name,
            'MasterUserPassword': admin_password}
        if engine_mode is not None:
            expected_params['EngineMode'] = ANY
        if enable_http is not None:
            expected_params['EnableHttpEndpoint'] = ANY
        if group_name is not None:
            expected_params['DBClusterParameterGroupName'] = group_name
        if engine_version is not None:
            expected_params['EngineVersion'] = engine_version
        response = {'DBCluster': {
            'DatabaseName': db_name, 'DBClusterIdentifier': cluster_name}}
        self._stub_bifurcator(
            'create_db_cluster', expected_params, response, error_code=error_code)

    def stub_describe_db_clusters(self, cluster_name, error_code=None):
        expected_params = {'DBClusterIdentifier': cluster_name}
        response = {'DBClusters': [{'DBClusterIdentifier': cluster_name, 'Status': 'available'}]}
        self._stub_bifurcator(
            'describe_db_clusters', expected_params, response, error_code=error_code)

    def stub_delete_db_cluster(self, cluster_name, error_code=None):
        expected_params = {
            'DBClusterIdentifier': cluster_name, 'SkipFinalSnapshot': True}
        self._stub_bifurcator(
            'delete_db_cluster', expected_params, error_code=error_code)

    def stub_describe_db_cluster_parameter_groups(self, group_name, groups, error_code=None):
        expected_params = {'DBClusterParameterGroupName': group_name}
        response = {'DBClusterParameterGroups': groups}
        self._stub_bifurcator(
            'describe_db_cluster_parameter_groups', expected_params, response, error_code=error_code)

    def stub_describe_db_engine_versions(self, engine, versions, param_family=None, error_code=None):
        expected_params = {'Engine': engine}
        if param_family is not None:
            expected_params['DBParameterGroupFamily'] = param_family
        response = {'DBEngineVersions': versions}
        self._stub_bifurcator(
            'describe_db_engine_versions', expected_params, response, error_code=error_code)

    def stub_create_db_cluster_parameter_group(
            self, group_name, group_family, desc=ANY, error_code=None):
        expected_params = {
            'DBClusterParameterGroupName': group_name,
            'DBParameterGroupFamily': group_family,
            'Description': desc}
        response = {'DBClusterParameterGroup': {'DBClusterParameterGroupName': group_name}}
        self._stub_bifurcator(
            'create_db_cluster_parameter_group', expected_params, response, error_code=error_code)

    def stub_describe_db_cluster_parameters(self, group_name, params, source=None, error_code=None):
        expected_params = {'DBClusterParameterGroupName': group_name}
        if source is not None:
            expected_params['Source'] = source
        response = {'Parameters': params}
        self._stub_bifurcator(
            'describe_db_cluster_parameters', expected_params, response, error_code=error_code)

    def stub_modify_db_cluster_parameter_group(self, group_name, parameters, error_code=None):
        expected_params = {'DBClusterParameterGroupName': group_name, 'Parameters': parameters}
        response = {}
        self._stub_bifurcator(
            'modify_db_cluster_parameter_group', expected_params, response, error_code=error_code)

    def stub_delete_db_cluster_parameter_group(self, group_name, error_code=None):
        expected_params = {'DBClusterParameterGroupName': group_name}
        response = {}
        self._stub_bifurcator(
            'delete_db_cluster_parameter_group', expected_params, response, error_code=error_code)

    def stub_describe_db_instances(self, instance_id, error_code=None):
        expected_params = {'DBInstanceIdentifier': instance_id}
        response = {'DBInstances': [{'DBInstanceIdentifier': instance_id, 'DBInstanceStatus': 'available'}]}
        self._stub_bifurcator(
            'describe_db_instances', expected_params, response, error_code=error_code)

    def stub_create_db_instance(
            self, instance_id, db_engine, instance_class, cluster_id=None, db_name=None,
            param_group_name=None, db_engine_version=None, storage_type=None, allocated_storage=None,
            admin_name=None, admin_password=None, error_code=None):
        expected_params = {
            'DBInstanceIdentifier': instance_id,
            'Engine': db_engine,
            'DBInstanceClass': instance_class}
        if cluster_id is not None:
            expected_params['DBClusterIdentifier'] = cluster_id
        if db_name is not None:
            expected_params['DBName'] = db_name
        if param_group_name is not None:
            expected_params['DBParameterGroupName'] = param_group_name
        if db_engine_version is not None:
            expected_params['EngineVersion'] = db_engine_version
        if storage_type is not None:
            expected_params['StorageType'] = storage_type
        if allocated_storage is not None:
            expected_params['AllocatedStorage'] = allocated_storage
        if admin_name is not None:
            expected_params['MasterUsername'] = admin_name
        if admin_password is not None:
            expected_params['MasterUserPassword'] = admin_password
        response = {'DBInstance': {'DBInstanceIdentifier': instance_id, 'DBInstanceStatus': 'creating'}}
        self._stub_bifurcator(
            'create_db_instance', expected_params, response, error_code=error_code)

    def stub_describe_orderable_db_instance_options(
            self, db_engine, db_engine_version, options, error_code=None):
        expected_params = {'Engine': db_engine, 'EngineVersion': db_engine_version}
        response = {'OrderableDBInstanceOptions': options}
        self._stub_bifurcator(
            'describe_orderable_db_instance_options', expected_params, response, error_code=error_code)

    def stub_delete_db_instance(self, instance_id, error_code=None):
        expected_params = {
            'DBInstanceIdentifier': instance_id, 'SkipFinalSnapshot': True, 'DeleteAutomatedBackups': True}
        response = {'DBInstance': {'DBInstanceIdentifier': instance_id, 'DBInstanceStatus': 'deleting'}}
        self._stub_bifurcator(
            'delete_db_instance', expected_params, response, error_code=error_code)

    def stub_create_db_cluster_snapshot(self, snapshot_id, cluster_id, error_code=None):
        expected_params = {'DBClusterSnapshotIdentifier': snapshot_id, 'DBClusterIdentifier': cluster_id}
        response = {'DBClusterSnapshot': {'DBClusterSnapshotIdentifier': snapshot_id}}
        self._stub_bifurcator(
            'create_db_cluster_snapshot', expected_params, response, error_code=error_code)

    def stub_describe_db_cluster_snapshots(self, snapshot_id, error_code=None):
        expected_params = {'DBClusterSnapshotIdentifier': snapshot_id}
        response = {'DBClusterSnapshots': [{'DBClusterSnapshotIdentifier': snapshot_id, 'Status': 'available'}]}
        self._stub_bifurcator(
            'describe_db_cluster_snapshots', expected_params, response, error_code=error_code)

    def stub_describe_db_parameter_groups(self, group_name, error_code=None):
        expected_params = {'DBParameterGroupName': group_name}
        response = {'DBParameterGroups': [{'DBParameterGroupName': group_name}]}
        self._stub_bifurcator(
            'describe_db_parameter_groups', expected_params, response, error_code=error_code)

    def stub_create_db_parameter_group(self, group_name, group_family, desc, error_code=None):
        expected_params = {
            'DBParameterGroupName': group_name, 'DBParameterGroupFamily': group_family,
            'Description': desc}
        response = {
            'DBParameterGroup': {
                'DBParameterGroupName': group_name, 'DBParameterGroupFamily': group_family,
                'Description': desc}}
        self._stub_bifurcator(
            'create_db_parameter_group', expected_params, response, error_code=error_code)

    def stub_delete_db_parameter_group(self, group_name, error_code=None):
        expected_params = {'DBParameterGroupName': group_name}
        response = {}
        self._stub_bifurcator(
            'delete_db_parameter_group', expected_params, response, error_code=error_code)

    def stub_describe_db_parameters(self, group_name, params, source=None, error_code=None):
        expected_params = {'DBParameterGroupName': group_name}
        if source is not None:
            expected_params['Source'] = source
        response = {'Parameters': params}
        self._stub_bifurcator(
            'describe_db_parameters', expected_params, response, error_code=error_code)

    def stub_modify_db_parameter_group(self, group_name, params, error_code=None):
        expected_params = {'DBParameterGroupName': group_name, 'Parameters': params}
        response = {}
        self._stub_bifurcator(
            'modify_db_parameter_group', expected_params, response, error_code=error_code)

    def stub_create_db_snapshot(self, snapshot_id, instance_id, error_code=None):
        expected_params = {'DBSnapshotIdentifier': snapshot_id, 'DBInstanceIdentifier': instance_id}
        response = {'DBSnapshot': {'DBSnapshotIdentifier': snapshot_id}}
        self._stub_bifurcator(
            'create_db_snapshot', expected_params, response, error_code=error_code)

    def stub_describe_db_snapshots(self, snapshot_id, error_code=None):
        expected_params = {'DBSnapshotIdentifier': snapshot_id}
        response = {'DBSnapshots': [{'DBSnapshotIdentifier': snapshot_id, 'Status': 'available'}]}
        self._stub_bifurcator(
            'describe_db_snapshots', expected_params, response, error_code=error_code)
