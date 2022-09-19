# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to create and manage Amazon Relational
Database Service (Amazon RDS) instances.
"""

import json
import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.rds.helper.InstanceWrapper_full]
# snippet-start:[python.example_code.rds.helper.InstanceWrapper_decl]
class InstanceWrapper:
    """Encapsulates Amazon RDS instance actions."""
    def __init__(self, rds_client):
        """
        :param rds_client: A Boto3 Amazon RDS client.
        """
        self.rds_client = rds_client

    @classmethod
    def from_client(cls):
        """
        Instantiates this class from a Boto3 client.
        """
        rds_client = boto3.client('rds')
        return cls(rds_client)
# snippet-end:[python.example_code.rds.helper.InstanceWrapper_decl]

    # snippet-start:[python.example_code.rds.DescribeDBParameterGroups]
    def get_parameter_group(self, parameter_group_name):
        """
        Gets a DB parameter group.

        :param parameter_group_name: The name of the parameter group to retrieve.
        :return: The parameter group.
        """
        try:
            response = self.rds_client.describe_db_parameter_groups(
                DBParameterGroupName=parameter_group_name)
            parameter_group = response['DBParameterGroups'][0]
        except ClientError as err:
            if err.response['Error']['Code'] == 'DBParameterGroupNotFound':
                logger.info("Parameter group %s does not exist.", parameter_group_name)
            else:
                logger.error(
                    "Couldn't get parameter group %s. Here's why: %s: %s", parameter_group_name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        else:
            return parameter_group
    # snippet-end:[python.example_code.rds.DescribeDBParameterGroups]

    # snippet-start:[python.example_code.rds.CreateDBParameterGroup]
    def create_parameter_group(self, parameter_group_name, parameter_group_family, description):
        """
        Creates a DB parameter group that is based on the specified parameter group
        family.

        :param parameter_group_name: The name of the newly created parameter group.
        :param parameter_group_family: The family that is used as the basis of the new
                                       parameter group.
        :param description: A description given to the parameter group.
        :return: Data about the newly created parameter group.
        """
        try:
            response = self.rds_client.create_db_parameter_group(
                DBParameterGroupName=parameter_group_name,
                DBParameterGroupFamily=parameter_group_family,
                Description=description)
        except ClientError as err:
            logger.error(
                "Couldn't create parameter group %s. Here's why: %s: %s", parameter_group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.rds.CreateDBParameterGroup]

    # snippet-start:[python.example_code.rds.DeleteDBParameterGroup]
    def delete_parameter_group(self, parameter_group_name):
        """
        Deletes a DB parameter group.

        :param parameter_group_name: The name of the parameter group to delete.
        :return: Data about the parameter group.
        """
        try:
            self.rds_client.delete_db_parameter_group(
                DBParameterGroupName=parameter_group_name)
        except ClientError as err:
            logger.error(
                "Couldn't delete parameter group %s. Here's why: %s: %s", parameter_group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.rds.DeleteDBParameterGroup]

    # snippet-start:[python.example_code.rds.DescribeDBParameters]
    def get_parameters(self, parameter_group_name, name_prefix='', source=None):
        """
        Gets the parameters that are contained in a DB parameter group.

        :param parameter_group_name: The name of the parameter group to query.
        :param name_prefix: When specified, the retrieved list of parameters is filtered
                            to contain only parameters that start with this prefix.
        :param source: When specified, only parameters from this source are retrieved.
                       For example, a source of 'user' retrieves only parameters that
                       were set by a user.
        :return: The list of requested parameters.
        """
        try:
            kwargs = {'DBParameterGroupName': parameter_group_name}
            if source is not None:
                kwargs['Source'] = source
            parameters = []
            paginator = self.rds_client.get_paginator('describe_db_parameters')
            for page in paginator.paginate(**kwargs):
                parameters += [
                    p for p in page['Parameters'] if p['ParameterName'].startswith(name_prefix)]
        except ClientError as err:
            logger.error(
                "Couldn't get parameters for %s. Here's why: %s: %s", parameter_group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return parameters
    # snippet-end:[python.example_code.rds.DescribeDBParameters]

    # snippet-start:[python.example_code.rds.ModifyDBParameterGroup]
    def update_parameters(self, parameter_group_name, update_parameters):
        """
        Updates parameters in a custom DB parameter group.

        :param parameter_group_name: The name of the parameter group to update.
        :param update_parameters: The parameters to update in the group.
        :return: Data about the modified parameter group.
        """
        try:
            response = self.rds_client.modify_db_parameter_group(
                DBParameterGroupName=parameter_group_name, Parameters=update_parameters)
        except ClientError as err:
            logger.error(
                "Couldn't update parameters in %s. Here's why: %s: %s", parameter_group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.rds.ModifyDBParameterGroup]

    # snippet-start:[python.example_code.rds.CreateDBSnapshot]
    def create_snapshot(self, snapshot_id, instance_id):
        """
        Creates a snapshot of a DB instance.

        :param snapshot_id: The ID to give the created snapshot.
        :param instance_id: The ID of the instance to snapshot.
        :return: Data about the newly created snapshot.
        """
        try:
            response = self.rds_client.create_db_snapshot(
                DBSnapshotIdentifier=snapshot_id, DBInstanceIdentifier=instance_id)
            snapshot = response['DBSnapshot']
        except ClientError as err:
            logger.error(
                "Couldn't create snapshot of %s. Here's why: %s: %s", instance_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return snapshot
    # snippet-end:[python.example_code.rds.CreateDBSnapshot]

    # snippet-start:[python.example_code.rds.DescribeDBSnapshots]
    def get_snapshot(self, snapshot_id):
        """
        Gets a DB instance snapshot.

        :param snapshot_id: The ID of the snapshot to retrieve.
        :return: The retrieved snapshot.
        """
        try:
            response = self.rds_client.describe_db_snapshots(
                DBSnapshotIdentifier=snapshot_id)
            snapshot = response['DBSnapshots'][0]
        except ClientError as err:
            logger.error(
                "Couldn't get snapshot %s. Here's why: %s: %s", snapshot_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return snapshot
    # snippet-end:[python.example_code.rds.DescribeDBSnapshots]

    # snippet-start:[python.example_code.rds.DescribeDBEngineVersions]
    def get_engine_versions(self, engine, parameter_group_family=None):
        """
        Gets database engine versions that are available for the specified engine
        and parameter group family.

        :param engine: The database engine to look up.
        :param parameter_group_family: When specified, restricts the returned list of
                                       engine versions to those that are compatible with
                                       this parameter group family.
        :return: The list of database engine versions.
        """
        try:
            kwargs = {'Engine': engine}
            if parameter_group_family is not None:
                kwargs['DBParameterGroupFamily'] = parameter_group_family
            response = self.rds_client.describe_db_engine_versions(**kwargs)
            versions = response['DBEngineVersions']
        except ClientError as err:
            logger.error(
                "Couldn't get engine versions for %s. Here's why: %s: %s", engine,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return versions
    # snippet-end:[python.example_code.rds.DescribeDBEngineVersions]

    # snippet-start:[python.example_code.rds.DescribeOrderableDBInstanceOptions]
    def get_orderable_instances(self, db_engine, db_engine_version):
        """
        Gets DB instance options that can be used to create DB instances that are
        compatible with a set of specifications.

        :param db_engine: The database engine that must be supported by the instance.
        :param db_engine_version: The engine version that must be supported by the instance.
        :return: The list of instance options that can be used to create a compatible instance.
        """
        try:
            inst_opts = []
            paginator = self.rds_client.get_paginator('describe_orderable_db_instance_options')
            for page in paginator.paginate(Engine=db_engine, EngineVersion=db_engine_version):
                inst_opts += page['OrderableDBInstanceOptions']
        except ClientError as err:
            logger.error(
                "Couldn't get orderable instances. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return inst_opts
    # snippet-end:[python.example_code.rds.DescribeOrderableDBInstanceOptions]

    # snippet-start:[python.example_code.rds.DescribeDBInstances]
    def get_db_instance(self, instance_id):
        """
        Gets data about a DB instance.

        :param instance_id: The ID of the instance to retrieve.
        :return: The retrieved DB instance.
        """
        try:
            response = self.rds_client.describe_db_instances(
                DBInstanceIdentifier=instance_id)
            db_inst = response['DBInstances'][0]
        except ClientError as err:
            if err.response['Error']['Code'] == 'DBInstanceNotFound':
                logger.info("Instance %s does not exist.", instance_id)
            else:
                logger.error(
                    "Couldn't get db instance %s. Here's why: %s: %s", instance_id,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        else:
            return db_inst
    # snippet-end:[python.example_code.rds.DescribeDBInstances]

    # snippet-start:[python.example_code.rds.CreateDBInstance]
    def create_db_instance(
            self, db_name, instance_id, parameter_group_name, db_engine, db_engine_version,
            instance_class, storage_type, allocated_storage, admin_name, admin_password):
        """
        Creates a DB instance.

        :param db_name: The name of the database that is created in the instance.
        :param instance_id: The ID to give the newly created instance.
        :param parameter_group_name: A parameter group to associate with the instance.
        :param db_engine: The database engine of a database to create in the instance.
        :param db_engine_version: The engine version for the created database.
        :param instance_class: The instance class for the newly created instance.
        :param storage_type: The storage type of the instance.
        :param allocated_storage: The amount of storage allocated on the instance, in GiBs.
        :param admin_name: The name of the admin user for the created database.
        :param admin_password: The admin password for the created database.
        :return: Data about the newly created instance.
        """
        try:
            response = self.rds_client.create_db_instance(
                DBName=db_name,
                DBInstanceIdentifier=instance_id,
                DBParameterGroupName=parameter_group_name,
                Engine=db_engine,
                EngineVersion=db_engine_version,
                DBInstanceClass=instance_class,
                StorageType=storage_type,
                AllocatedStorage=allocated_storage,
                MasterUsername=admin_name,
                MasterUserPassword=admin_password)
            db_inst = response['DBInstance']
        except ClientError as err:
            logger.error(
                "Couldn't create db instance %s. Here's why: %s: %s", instance_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return db_inst
    # snippet-end:[python.example_code.rds.CreateDBInstance]

    # snippet-start:[python.example_code.rds.DeleteDBInstance]
    def delete_db_instance(self, instance_id):
        """
        Deletes a DB instance.

        :param instance_id: The ID of the instance to delete.
        :return: Data about the deleted instance.
        """
        try:
            response = self.rds_client.delete_db_instance(
                DBInstanceIdentifier=instance_id, SkipFinalSnapshot=True,
                DeleteAutomatedBackups=True)
            db_inst = response['DBInstance']
        except ClientError as err:
            logger.error(
                "Couldn't delete db instance %s. Here's why: %s: %s", instance_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return db_inst
    # snippet-end:[python.example_code.rds.DeleteDBInstance]
# snippet-end:[python.example_code.rds.helper.InstanceWrapper_full]
