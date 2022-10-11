# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to create and manage Amazon Aurora
DB clusters.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.aurora.helper.AuroraWrapper_full]
# snippet-start:[python.example_code.aurora.helper.AuroraWrapper_decl]
class AuroraWrapper:
    """Encapsulates Aurora DB cluster actions."""
    def __init__(self, rds_client):
        """
        :param rds_client: A Boto3 Amazon Relational Database Service (Amazon RDS) client.
        """
        self.rds_client = rds_client

    @classmethod
    def from_client(cls):
        """
        Instantiates this class from a Boto3 client.
        """
        rds_client = boto3.client('rds')
        return cls(rds_client)
# snippet-end:[python.example_code.aurora.helper.AuroraWrapper_decl]

    # snippet-start:[python.example_code.aurora.DescribeDBClusterParameterGroups]
    def get_parameter_group(self, parameter_group_name):
        """
        Gets a DB cluster parameter group.

        :param parameter_group_name: The name of the parameter group to retrieve.
        :return: The requested parameter group.
        """
        try:
            response = self.rds_client.describe_db_cluster_parameter_groups(
                DBClusterParameterGroupName=parameter_group_name)
            parameter_group = response['DBClusterParameterGroups'][0]
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
    # snippet-end:[python.example_code.aurora.DescribeDBClusterParameterGroups]

    # snippet-start:[python.example_code.aurora.CreateDBClusterParameterGroup]
    def create_parameter_group(self, parameter_group_name, parameter_group_family, description):
        """
        Creates a DB cluster parameter group that is based on the specified parameter group
        family.

        :param parameter_group_name: The name of the newly created parameter group.
        :param parameter_group_family: The family that is used as the basis of the new
                                       parameter group.
        :param description: A description given to the parameter group.
        :return: Data about the newly created parameter group.
        """
        try:
            response = self.rds_client.create_db_cluster_parameter_group(
                DBClusterParameterGroupName=parameter_group_name,
                DBParameterGroupFamily=parameter_group_family,
                Description=description)
        except ClientError as err:
            logger.error(
                "Couldn't create parameter group %s. Here's why: %s: %s", parameter_group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.aurora.CreateDBClusterParameterGroup]

    # snippet-start:[python.example_code.aurora.DeleteDBClusterParameterGroup]
    def delete_parameter_group(self, parameter_group_name):
        """
        Deletes a DB cluster parameter group.

        :param parameter_group_name: The name of the parameter group to delete.
        :return: Data about the parameter group.
        """
        try:
            response = self.rds_client.delete_db_cluster_parameter_group(
                DBClusterParameterGroupName=parameter_group_name)
        except ClientError as err:
            logger.error(
                "Couldn't delete parameter group %s. Here's why: %s: %s", parameter_group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.aurora.DeleteDBClusterParameterGroup]

    # snippet-start:[python.example_code.aurora.DescribeDBClusterParameters]
    def get_parameters(self, parameter_group_name, name_prefix='', source=None):
        """
        Gets the parameters that are contained in a DB cluster parameter group.

        :param parameter_group_name: The name of the parameter group to query.
        :param name_prefix: When specified, the retrieved list of parameters is filtered
                            to contain only parameters that start with this prefix.
        :param source: When specified, only parameters from this source are retrieved.
                       For example, a source of 'user' retrieves only parameters that
                       were set by a user.
        :return: The list of requested parameters.
        """
        try:
            kwargs = {'DBClusterParameterGroupName': parameter_group_name}
            if source is not None:
                kwargs['Source'] = source
            parameters = []
            paginator = self.rds_client.get_paginator('describe_db_cluster_parameters')
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
    # snippet-end:[python.example_code.aurora.DescribeDBClusterParameters]
    
    # snippet-start:[python.example_code.aurora.ModifyDBClusterParameterGroup]
    def update_parameters(self, parameter_group_name, update_parameters):
        """
        Updates parameters in a custom DB cluster parameter group.

        :param parameter_group_name: The name of the parameter group to update.
        :param update_parameters: The parameters to update in the group.
        :return: Data about the modified parameter group.
        """
        try:
            response = self.rds_client.modify_db_cluster_parameter_group(
                DBClusterParameterGroupName=parameter_group_name, Parameters=update_parameters)
        except ClientError as err:
            logger.error(
                "Couldn't update parameters in %s. Here's why: %s: %s", parameter_group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.aurora.ModifyDBClusterParameterGroup]

    # snippet-start:[python.example_code.aurora.DescribeDBClusters]
    def get_db_cluster(self, cluster_name):
        """
        Gets data about an Aurora DB cluster.

        :param cluster_name: The name of the DB cluster to retrieve.
        :return: The retrieved DB cluster.
        """
        try:
            response = self.rds_client.describe_db_clusters(
                DBClusterIdentifier=cluster_name)
            cluster = response['DBClusters'][0]
        except ClientError as err:
            if err.response['Error']['Code'] == 'DBClusterNotFoundFault':
                logger.info("Cluster %s does not exist.", cluster_name)
            else:
                logger.error(
                    "Couldn't verify the existence of DB cluster %s. Here's why: %s: %s", cluster_name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        else:
            return cluster
    # snippet-end:[python.example_code.aurora.DescribeDBClusters]

    # snippet-start:[python.example_code.aurora.CreateDBCluster]
    def create_db_cluster(
            self, cluster_name, parameter_group_name, db_name, db_engine,
            db_engine_version, admin_name, admin_password):
        """
        Creates a DB cluster that is configured to use the specified parameter group.
        The newly created DB cluster contains a database that uses the specified engine and
        engine version.

        :param cluster_name: The name of the DB cluster to create.
        :param parameter_group_name: The name of the parameter group to associate with
                                     the DB cluster.
        :param db_name: The name of the database to create.
        :param db_engine: The database engine of the database that is created, such as MySql.
        :param db_engine_version: The version of the database engine.
        :param admin_name: The user name of the database administrator.
        :param admin_password: The password of the database administrator.
        :return: The newly created DB cluster.
        """
        try:
            response = self.rds_client.create_db_cluster(
                DatabaseName=db_name,
                DBClusterIdentifier=cluster_name,
                DBClusterParameterGroupName=parameter_group_name,
                Engine=db_engine,
                EngineVersion=db_engine_version,
                MasterUsername=admin_name,
                MasterUserPassword=admin_password)
            cluster = response['DBCluster']
        except ClientError as err:
            logger.error(
                "Couldn't create database %s. Here's why: %s: %s", db_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return cluster
    # snippet-end:[python.example_code.aurora.CreateDBCluster]

    # snippet-start:[python.example_code.aurora.DeleteDBCluster]
    def delete_db_cluster(self, cluster_name):
        """
        Deletes a DB cluster.

        :param cluster_name: The name of the DB cluster to delete.
        """
        try:
            self.rds_client.delete_db_cluster(
                DBClusterIdentifier=cluster_name, SkipFinalSnapshot=True)
            logger.info("Deleted DB cluster %s.", cluster_name)
        except ClientError:
            logger.exception("Couldn't delete DB cluster %s.", cluster_name)
            raise
    # snippet-end:[python.example_code.aurora.DeleteDBCluster]
    
    # snippet-start:[python.example_code.aurora.CreateDBClusterSnapshot]
    def create_cluster_snapshot(self, snapshot_id, cluster_id):
        """
        Creates a snapshot of a DB cluster.

        :param snapshot_id: The ID to give the created snapshot.
        :param cluster_id: The DB cluster to snapshot.
        :return: Data about the newly created snapshot.
        """
        try:
            response = self.rds_client.create_db_cluster_snapshot(
                DBClusterSnapshotIdentifier=snapshot_id, DBClusterIdentifier=cluster_id)
            snapshot = response['DBClusterSnapshot']
        except ClientError as err:
            logger.error(
                "Couldn't create snapshot of %s. Here's why: %s: %s", cluster_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return snapshot
    # snippet-end:[python.example_code.aurora.CreateDBClusterSnapshot]

    # snippet-start:[python.example_code.aurora.DescribeDBClusterSnapshots]
    def get_cluster_snapshot(self, snapshot_id):
        """
        Gets a DB cluster snapshot.

        :param snapshot_id: The ID of the snapshot to retrieve.
        :return: The retrieved snapshot.
        """
        try:
            response = self.rds_client.describe_db_cluster_snapshots(
                DBClusterSnapshotIdentifier=snapshot_id)
            snapshot = response['DBClusterSnapshots'][0]
        except ClientError as err:
            logger.error(
                "Couldn't get DB cluster snapshot %s. Here's why: %s: %s", snapshot_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return snapshot
    # snippet-end:[python.example_code.aurora.DescribeDBClusterSnapshots]
        
    # snippet-start:[python.example_code.aurora.CreateDBInstance_InCluster]
    def create_instance_in_cluster(self, instance_id, cluster_id, db_engine, instance_class):
        """
        Creates a database instance in an existing DB cluster. The first database that is
        created defaults to a read-write DB instance.

        :param instance_id: The ID to give the newly created DB instance.
        :param cluster_id: The ID of the DB cluster where the DB instance is created.
        :param db_engine: The database engine of a database to create in the DB instance.
                          This must be compatible with the configured parameter group
                          of the DB cluster.
        :param instance_class: The DB instance class for the newly created DB instance.
        :return: Data about the newly created DB instance.
        """
        try:
            response = self.rds_client.create_db_instance(
                DBInstanceIdentifier=instance_id, DBClusterIdentifier=cluster_id,
                Engine=db_engine, DBInstanceClass=instance_class)
            db_inst = response['DBInstance']
        except ClientError as err:
            logger.error(
                "Couldn't create DB instance %s. Here's why: %s: %s", instance_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return db_inst
    # snippet-end:[python.example_code.aurora.CreateDBInstance_InCluster]

    # snippet-start:[python.example_code.aurora.DescribeDBEngineVersions]
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
    # snippet-end:[python.example_code.aurora.DescribeDBEngineVersions]

    # snippet-start:[python.example_code.aurora.DescribeOrderableDBInstanceOptions]
    def get_orderable_instances(self, db_engine, db_engine_version):
        """
        Gets DB instance options that can be used to create DB instances that are
        compatible with a set of specifications.

        :param db_engine: The database engine that must be supported by the DB instance.
        :param db_engine_version: The engine version that must be supported by the DB instance.
        :return: The list of DB instance options that can be used to create a compatible DB instance.
        """
        try:
            inst_opts = []
            paginator = self.rds_client.get_paginator('describe_orderable_db_instance_options')
            for page in paginator.paginate(Engine=db_engine, EngineVersion=db_engine_version):
                inst_opts += page['OrderableDBInstanceOptions']
        except ClientError as err:
            logger.error(
                "Couldn't get orderable DB instances. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return inst_opts
    # snippet-end:[python.example_code.aurora.DescribeOrderableDBInstanceOptions]

    # snippet-start:[python.example_code.aurora.DescribeDBInstances]
    def get_db_instance(self, instance_id):
        """
        Gets data about a DB instance.

        :param instance_id: The ID of the DB instance to retrieve.
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
                    "Couldn't get DB instance %s. Here's why: %s: %s", instance_id,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        else:
            return db_inst
    # snippet-end:[python.example_code.aurora.DescribeDBInstances]

    # snippet-start:[python.example_code.aurora.DeleteDBInstance]
    def delete_db_instance(self, instance_id):
        """
        Deletes a DB instance.

        :param instance_id: The ID of the DB instance to delete.
        :return: Data about the deleted DB instance.
        """
        try:
            response = self.rds_client.delete_db_instance(
                DBInstanceIdentifier=instance_id, SkipFinalSnapshot=True,
                DeleteAutomatedBackups=True)
            db_inst = response['DBInstance']
        except ClientError as err:
            logger.error(
                "Couldn't delete DB instance %s. Here's why: %s: %s", instance_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return db_inst
    # snippet-end:[python.example_code.aurora.DeleteDBInstance]
# snippet-end:[python.example_code.aurora.helper.AuroraWrapper_full]
