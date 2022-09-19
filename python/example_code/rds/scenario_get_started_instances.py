# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Relation Database Service
(Amazon RDS) to do the following:

* Create a custom DB parameter group and set parameter values.
* Create a DB instance that is configured to use the parameter group and contains a
  database.
* Take a snapshot of the instance.
* Delete the instance and parameter group.
"""

import logging
from pprint import pp
import sys
import uuid

from instance_wrapper import InstanceWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
import demo_tools.question as q
from demo_tools.retries import wait

logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.rds.Scenario_GetStartedInstances]
class RdsInstanceScenario:
    """Runs a scenario that shows how to get started using Amazon RDS instances."""
    def __init__(self, instance_wrapper):
        """
        :param instance_wrapper: An object that wraps Amazon RDS instance actions.
        """
        self.instance_wrapper = instance_wrapper

    def create_parameter_group(self, parameter_group_name, db_engine):
        """
        Shows how to get available engine versions for a specified database engine and
        create a DB parameter group that is compatible with a selected engine family.

        :param parameter_group_name: The name given to the newly created parameter group.
        :param db_engine: The database engine to use as a basis.
        :return: The newly created parameter group.
        """
        print(f"Checking for an existing instance parameter group named {parameter_group_name}.")
        parameter_group = self.instance_wrapper.get_parameter_group(parameter_group_name)
        if parameter_group is None:
            print(f"Getting available database engine versions for {db_engine}.")
            engine_versions = self.instance_wrapper.get_engine_versions(db_engine)
            families = list({ver['DBParameterGroupFamily'] for ver in engine_versions})
            family_index = q.choose("Which family do you want to use? ", families)
            print(f"Creating a parameter group.")
            self.instance_wrapper.create_parameter_group(
                parameter_group_name, families[family_index], 'Example parameter group.')
            parameter_group = self.instance_wrapper.get_parameter_group(parameter_group_name)
        print(f"Parameter group {parameter_group['DBParameterGroupName']}:")
        pp(parameter_group)
        print('-'*88)
        return parameter_group

    def update_parameters(self, parameter_group_name):
        """
        Shows how to get the parameters contained in a custom parameter group and
        update some of the parameter values in the group.

        :param parameter_group_name: The name of the parameter group to query and modify.
        """
        print("Let's set some parameter values in your parameter group.")
        auto_inc_parameters = self.instance_wrapper.get_parameters(
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
        self.instance_wrapper.update_parameters(parameter_group_name, update_params)
        print("You can get a list of parameters you've set by specifying a source of 'user'.")
        user_parameters = self.instance_wrapper.get_parameters(parameter_group_name, source='user')
        pp(user_parameters)
        print('-'*88)

    def create_instance(self, instance_name, db_name, db_engine, parameter_group):
        """
        Shows how to create a DB instance that contains a database of a specified
        type and is configured to use a custom DB parameter group.

        :param instance_name: The name given to the newly created instance.
        :param db_name: The name given to the created database.
        :param db_engine: The engine of the created database.
        :param parameter_group: The parameter group that is associated with the instance.
        :return: The newly created instance.
        """
        print("Checking for an existing database instance.")
        db_inst = self.instance_wrapper.get_db_instance(instance_name)
        if db_inst is None:
            print("Let's create a database instance.")
            admin_username = q.ask("Enter an administrator username for the database: ", q.non_empty)
            admin_password = q.ask(
                "Enter a password for the administrator (at least 8 characters): ", q.non_empty)
            engine_versions = self.instance_wrapper.get_engine_versions(
                db_engine, parameter_group['DBParameterGroupFamily'])
            engine_choices = [ver['EngineVersion'] for ver in engine_versions]
            print("The available engines for your parameter group are:")
            engine_index = q.choose("Which engine do you want to use? ", engine_choices)
            engine_selection = engine_versions[engine_index]
            print("The available micro instance classes for your database engine are:")
            inst_opts = self.instance_wrapper.get_orderable_instances(
                engine_selection['Engine'], engine_selection['EngineVersion'])
            inst_choices = list({opt['DBInstanceClass'] for opt in inst_opts if 'micro' in opt['DBInstanceClass']})
            inst_index = q.choose("Which micro instance class do you want to use? ", inst_choices)
            group_name = parameter_group['DBParameterGroupName']
            storage_type = 'standard'
            allocated_storage = 5
            print(f"Creating a database instance named {instance_name} and database {db_name}.\n"
                  f"The instance is configured to use your custom parameter group {group_name},\n"
                  f"selected engine {engine_selection['EngineVersion']},\n"
                  f"selected instance class {inst_choices[inst_index]},"
                  f"and {allocated_storage} GiB of {storage_type} storage.\n"
                  f"This typically takes several minutes.")
            db_inst = self.instance_wrapper.create_db_instance(
                db_name, instance_name, group_name, engine_selection['Engine'],
                engine_selection['EngineVersion'], inst_choices[inst_index], storage_type,
                allocated_storage, admin_username, admin_password)
            while db_inst.get('DBInstanceStatus') != 'available':
                wait(10)
                db_inst = self.instance_wrapper.get_db_instance(instance_name)
        print("Instance data:")
        pp(db_inst)
        print('-'*88)
        return db_inst

    @staticmethod
    def display_connection(db_inst):
        """
        Displays connection information about a DB instance and tips on how to
        connect to it.

        :param db_inst: The instance to display.
        """
        print("You can now connect to your database using your favorite MySql client.\n"
              "One way to connect is by using the 'mysql' shell on an Amazon EC2 instance\n"
              "that is running in the same VPC as your database cluster. Pass the endpoint,\n"
              "port, and administrator user name to 'mysql' and enter your password\n"
              "when prompted:\n")
        print(f"\n\tmysql -h {db_inst['Endpoint']['Address']} -P {db_inst['Endpoint']['Port']} "
              f"-u {db_inst['MasterUsername']} -p\n")
        print("For more information, see the User Guide for Amazon RDS:\n"
              "\thttps://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_GettingStarted.CreatingConnecting.MySQL.html#CHAP_GettingStarted.Connecting.MySQL")
        print('-'*88)

    def create_snapshot(self, instance_name):
        """
        Shows how to create an instance snapshot and wait until it's available.

        :param instance_name: The name of an instance to snapshot.
        """
        if q.ask("Do you want to create a snapshot of your database instance (y/n)? ", q.is_yesno):
            snapshot_id = f"{instance_name}-{uuid.uuid4()}"
            print(f"Creating a snapshot named {snapshot_id}. This typically takes a few minutes.")
            snapshot = self.instance_wrapper.create_snapshot(snapshot_id, instance_name)
            while snapshot.get('Status') != 'available':
                wait(10)
                snapshot = self.instance_wrapper.get_snapshot(snapshot_id)
            pp(snapshot)
            print('-'*88)

    def cleanup(self, db_inst, parameter_group_name):
        """
        Shows how to clean up a DB instance and parameter group.
        Before the parameter group can be deleted, all associated instances must first
        be deleted.

        :param db_inst: The DB instance to delete.
        :param parameter_group_name: The DB parameter group to delete.
        """
        if q.ask(
                "\nDo you want to delete the database instance and parameter group (y/n)? ",
                q.is_yesno):
            print(f"Deleting database instance {db_inst['DBInstanceIdentifier']}.")
            self.instance_wrapper.delete_db_instance(db_inst['DBInstanceIdentifier'])
            print("Waiting for the instance to delete. This typically takes several minutes.")
            while db_inst is not None:
                wait(10)
                db_inst = self.instance_wrapper.get_db_instance(db_inst['DBInstanceIdentifier'])
            print(f"Deleting parameter group {parameter_group_name}.")
            self.instance_wrapper.delete_parameter_group(parameter_group_name)

    def run_scenario(
            self, db_engine, parameter_group_name, instance_name, db_name):
        logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

        print('-'*88)
        print("Welcome to the Amazon Relational Database Service (Amazon RDS)\n"
              "get started with DB instances demo.")
        print('-'*88)

        parameter_group = self.create_parameter_group(parameter_group_name, db_engine)
        self.update_parameters(parameter_group_name)
        db_inst = self.create_instance(instance_name, db_name, db_engine, parameter_group)
        self.display_connection(db_inst)
        self.create_snapshot(instance_name)
        self.cleanup(db_inst, parameter_group_name)

        print("\nThanks for watching!")
        print('-'*88)


if __name__ == '__main__':
    try:
        scenario = RdsInstanceScenario(InstanceWrapper.from_client())
        scenario.run_scenario(
            'mysql', 'doc-example-parameter-group', 'doc-example-instance', 'docexampledb')
    except Exception:
        logging.exception("Something went wrong with the demo.")
# snippet-end:[python.example_code.rds.Scenario_GetStartedInstances]
