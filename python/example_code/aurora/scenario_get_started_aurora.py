# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Aurora to do the following:

* Create a custom DB cluster parameter group and set parameter values.
* Create an Aurora DB cluster that is configured to use the parameter group.
* Create a DB instance in the DB cluster that contains a database.
* Take a snapshot of the DB cluster.
* Delete the DB instance, DB cluster, and parameter group.
"""

import logging
from pprint import pp
import sys
import uuid

from aurora_wrapper import AuroraWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
import demo_tools.question as q
from demo_tools.retries import wait

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.aurora.Scenario_GetStartedClusters]
class AuroraClusterScenario:
    """Runs a scenario that shows how to get started using Aurora DB clusters."""
    def __init__(self, aurora_wrapper):
        """
        :param aurora_wrapper: An object that wraps Aurora DB cluster actions.
        """
        self.aurora_wrapper = aurora_wrapper

    def create_parameter_group(self, db_engine, parameter_group_name):
        """
        Shows how to get available engine versions for a specified database engine and
        create a DB cluster parameter group that is compatible with a selected engine family.

        :param db_engine: The database engine to use as a basis.
        :param parameter_group_name: The name given to the newly created parameter group.
        :return: The newly created parameter group.
        """
        print(f"Checking for an existing DB cluster parameter group named {parameter_group_name}.")
        parameter_group = self.aurora_wrapper.get_parameter_group(parameter_group_name)
        if parameter_group is None:
            print(f"Getting available database engine versions for {db_engine}.")
            engine_versions = self.aurora_wrapper.get_engine_versions(db_engine)
            families = list({ver['DBParameterGroupFamily'] for ver in engine_versions})
            family_index = q.choose("Which family do you want to use? ", families)
            print(f"Creating a DB cluster parameter group.")
            self.aurora_wrapper.create_parameter_group(
                parameter_group_name, families[family_index], 'Example parameter group.')
            parameter_group = self.aurora_wrapper.get_parameter_group(parameter_group_name)
        print(f"Parameter group {parameter_group['DBClusterParameterGroupName']}:")
        pp(parameter_group)
        print('-'*88)
        return parameter_group

    def set_user_parameters(self, parameter_group_name):
        """
        Shows how to get the parameters contained in a custom parameter group and
        update some of the parameter values in the group.

        :param parameter_group_name: The name of the parameter group to query and modify.
        """
        print("Let's set some parameter values in your parameter group.")
        auto_inc_parameters = self.aurora_wrapper.get_parameters(
            parameter_group_name, name_prefix='auto_increment')
        update_params = []
        for auto_inc in auto_inc_parameters:
            if auto_inc['IsModifiable'] and auto_inc['DataType'] == 'integer':
                print(f"The {auto_inc['ParameterName']} parameter is described as:")
                print(f"\t{auto_inc['Description']}")
                param_range = auto_inc['AllowedValues'].split('-')
                auto_inc['ParameterValue'] = str(q.ask(
                    f"Enter a value between {param_range[0]} and {param_range[1]}: ",
                    q.is_int, q.in_range(int(param_range[0]), int(param_range[1]))))
                update_params.append(auto_inc)
        self.aurora_wrapper.update_parameters(parameter_group_name, update_params)
        print("You can get a list of parameters you've set by specifying a source of 'user'.")
        user_parameters = self.aurora_wrapper.get_parameters(parameter_group_name, source='user')
        pp(user_parameters)
        print('-'*88)

    def create_cluster(self, cluster_name, db_engine, db_name, parameter_group):
        """
        Shows how to create an Aurora DB cluster that contains a database of a specified
        type. The database is also configured to use a custom DB cluster parameter group.

        :param cluster_name: The name given to the newly created DB cluster.
        :param db_engine: The engine of the created database.
        :param db_name: The name given to the created database.
        :param parameter_group: The parameter group that is associated with the DB cluster.
        :return: The newly created DB cluster.
        """
        print("Checking for an existing DB cluster.")
        cluster = self.aurora_wrapper.get_db_cluster(cluster_name)
        if cluster is None:
            admin_username = q.ask(
                "Enter an administrator user name for the database: ", q.non_empty)
            admin_password = q.ask(
                "Enter a password for the administrator (at least 8 characters): ", q.non_empty)
            engine_versions = self.aurora_wrapper.get_engine_versions(
                db_engine, parameter_group['DBParameterGroupFamily'])
            engine_choices = [ver['EngineVersion'] for ver in engine_versions]
            print("The available engines for your parameter group are:")
            engine_index = q.choose("Which engine do you want to use? ", engine_choices)
            print(f"Creating DB cluster {cluster_name} and database {db_name}.\n"
                  f"The DB cluster is configured to use\n"
                  f"your custom parameter group {parameter_group['DBClusterParameterGroupName']}\n"
                  f"and selected engine {engine_choices[engine_index]}.\n"
                  f"This typically takes several minutes.")
            cluster = self.aurora_wrapper.create_db_cluster(
                cluster_name, parameter_group['DBClusterParameterGroupName'], db_name,
                db_engine, engine_choices[engine_index], admin_username, admin_password)
            while cluster.get('Status') != 'available':
                wait(10)
                cluster = self.aurora_wrapper.get_db_cluster(cluster_name)
            print("Cluster created and available.\n")
        print("Cluster data:")
        pp(cluster)
        print('-'*88)
        return cluster

    def create_instance(self, cluster):
        """
        Shows how to create a DB instance in an existing Aurora DB cluster. A new DB cluster
        contains no DB instances, so you must add one. The first DB instance that is added
        to a DB cluster defaults to a read-write DB instance.

        :param cluster: The DB cluster where the DB instance is added.
        :return: The newly created DB instance.
        """
        print("Checking for an existing database instance.")
        cluster_name = cluster['DBClusterIdentifier']
        db_inst = self.aurora_wrapper.get_db_instance(cluster_name)
        if db_inst is None:
            print("Let's create a database instance in your DB cluster.")
            print("First, choose a DB instance type:")
            inst_opts = self.aurora_wrapper.get_orderable_instances(
                cluster['Engine'], cluster['EngineVersion'])
            inst_choices = list({opt['DBInstanceClass'] for opt in inst_opts})
            inst_index = q.choose("Which DB instance class do you want to use? ", inst_choices)
            print(f"Creating a database instance. This typically takes several minutes.")
            db_inst = self.aurora_wrapper.create_instance_in_cluster(
                cluster_name, cluster_name, cluster['Engine'], inst_choices[inst_index])
            while db_inst.get('DBInstanceStatus') != 'available':
                wait(10)
                db_inst = self.aurora_wrapper.get_db_instance(cluster_name)
        print("Instance data:")
        pp(db_inst)
        print('-'*88)
        return db_inst

    @staticmethod
    def display_connection(cluster):
        """
        Displays connection information about an Aurora DB cluster and tips on how to
        connect to it.

        :param cluster: The DB cluster to display.
        """
        print("You can now connect to your database using your favorite MySql client.\n"
              "One way to connect is by using the 'mysql' shell on an Amazon EC2 instance\n"
              "that is running in the same VPC as your database cluster. Pass the endpoint,\n"
              "port, and administrator user name to 'mysql' and enter your password\n"
              "when prompted:\n")
        print(f"\n\tmysql -h {cluster['Endpoint']} -P {cluster['Port']} -u {cluster['MasterUsername']} -p\n")
        print("For more information, see the User Guide for Aurora:\n"
              "\thttps://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_GettingStartedAurora.CreatingConnecting.Aurora.html#CHAP_GettingStartedAurora.Aurora.Connect")
        print('-'*88)

    def create_snapshot(self, cluster_name):
        """
        Shows how to create a DB cluster snapshot and wait until it's available.

        :param cluster_name: The name of a DB cluster to snapshot.
        """
        if q.ask("Do you want to create a snapshot of your DB cluster (y/n)? ", q.is_yesno):
            snapshot_id = f"{cluster_name}-{uuid.uuid4()}"
            print(f"Creating a snapshot named {snapshot_id}. This typically takes a few minutes.")
            snapshot = self.aurora_wrapper.create_cluster_snapshot(snapshot_id, cluster_name)
            while snapshot.get('Status') != 'available':
                wait(10)
                snapshot = self.aurora_wrapper.get_cluster_snapshot(snapshot_id)
            pp(snapshot)
            print('-'*88)

    def cleanup(self, db_inst, cluster, parameter_group):
        """
        Shows how to clean up a DB instance, DB cluster, and DB cluster parameter group.
        Before the DB cluster parameter group can be deleted, all associated DB instances and
        DB clusters must first be deleted.

        :param db_inst: The DB instance to delete.
        :param cluster: The DB cluster to delete.
        :param parameter_group: The DB cluster parameter group to delete.
        """
        cluster_name = cluster['DBClusterIdentifier']
        parameter_group_name = parameter_group['DBClusterParameterGroupName']
        if q.ask(
                "\nDo you want to delete the database instance, DB cluster, and parameter "
                "group (y/n)? ", q.is_yesno):
            print(f"Deleting database instance {db_inst['DBInstanceIdentifier']}.")
            self.aurora_wrapper.delete_db_instance(db_inst['DBInstanceIdentifier'])
            print(f"Deleting database cluster {cluster_name}.")
            self.aurora_wrapper.delete_db_cluster(cluster_name)
            print("Waiting for the DB instance and DB cluster to delete.\n"
                  "This typically takes several minutes.")
            while db_inst is not None or cluster is not None:
                wait(10)
                if db_inst is not None:
                    db_inst = self.aurora_wrapper.get_db_instance(db_inst['DBInstanceIdentifier'])
                if cluster is not None:
                    cluster = self.aurora_wrapper.get_db_cluster(cluster['DBClusterIdentifier'])
            print(f"Deleting parameter group {parameter_group_name}.")
            self.aurora_wrapper.delete_parameter_group(parameter_group_name)

    def run_scenario(self, db_engine, parameter_group_name, cluster_name, db_name):
        print('-'*88)
        print("Welcome to the Amazon Relational Database Service (Amazon RDS) get started\n"
              "with Aurora DB clusters demo.")
        print('-'*88)

        parameter_group = self.create_parameter_group(db_engine, parameter_group_name)
        self.set_user_parameters(parameter_group_name)
        cluster = self.create_cluster(cluster_name, db_engine, db_name, parameter_group)
        db_inst = self.create_instance(cluster)
        self.display_connection(cluster)
        self.create_snapshot(cluster_name)
        self.cleanup(db_inst, cluster, parameter_group)

        print("\nThanks for watching!")
        print('-'*88)


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    try:
        scenario = AuroraClusterScenario(AuroraWrapper.from_client())
        scenario.run_scenario(
            'aurora-mysql', 'doc-example-cluster-parameter-group', 'doc-example-aurora',
            'docexampledb')
    except Exception:
        logging.exception("Something went wrong with the demo.")
# snippet-end:[python.example_code.aurora.Scenario_GetStartedClusters]
