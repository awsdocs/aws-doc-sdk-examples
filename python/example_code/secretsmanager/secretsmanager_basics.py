# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Secrets Manager to
create and manage secrets, and how to use a secret that contains database credentials
to access an Amazon Aurora database cluster.
"""

import argparse
import base64
import json
import logging
from pprint import pprint
import time
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.secrets-manager.SecretsManagerSecret]
class SecretsManagerSecret:
    """Encapsulates Secrets Manager functions."""
    def __init__(self, secretsmanager_client):
        """
        :param secretsmanager_client: A Boto3 Secrets Manager client.
        """
        self.secretsmanager_client = secretsmanager_client
        self.name = None
# snippet-end:[python.example_code.secrets-manager.SecretsManagerSecret]

    def _clear(self):
        self.name = None

# snippet-start:[python.example_code.secrets-manager.CreateSecret]
    def create(self, name, secret_value):
        """
        Creates a new secret. The secret value can be a string or bytes.

        :param name: The name of the secret to create.
        :param secret_value: The value of the secret.
        :return: Metadata about the newly created secret.
        """
        self._clear()
        try:
            kwargs = {'Name': name}
            if isinstance(secret_value, str):
                kwargs['SecretString'] = secret_value
            elif isinstance(secret_value, bytes):
                kwargs['SecretBinary'] = secret_value
            response = self.secretsmanager_client.create_secret(**kwargs)
            self.name = name
            logger.info("Created secret %s.", name)
        except ClientError:
            logger.exception("Couldn't get secret %s.", name)
            raise
        else:
            return response
# snippet-end:[python.example_code.secrets-manager.CreateSecret]

# snippet-start:[python.example_code.secrets-manager.DescribeSecret]
    def describe(self, name=None):
        """
        Gets metadata about a secret.

        :param name: The name of the secret to load. If `name` is None, metadata about
                     the current secret is retrieved.
        :return: Metadata about the secret.
        """
        if self.name is None and name is None:
            raise ValueError
        if name is None:
            name = self.name
        self._clear()
        try:
            response = self.secretsmanager_client.describe_secret(SecretId=name)
            self.name = name
            logger.info("Got secret metadata for %s.", name)
        except ClientError:
            logger.exception("Couldn't get secret metadata for %s.", name)
            raise
        else:
            return response
# snippet-end:[python.example_code.secrets-manager.DescribeSecret]

# snippet-start:[python.example_code.secrets-manager.GetSecretValue]
    def get_value(self, stage=None):
        """
        Gets the value of a secret.

        :param stage: The stage of the secret to retrieve. If this is None, the
                      current stage is retrieved.
        :return: The value of the secret. When the secret is a string, the value is
                 contained in the `SecretString` field. When the secret is bytes,
                 it is contained in the `SecretBinary` field.
        """
        if self.name is None:
            raise ValueError

        try:
            kwargs = {'SecretId': self.name}
            if stage is not None:
                kwargs['VersionStage'] = stage
            response = self.secretsmanager_client.get_secret_value(**kwargs)
            logger.info("Got value for secret %s.", self.name)
        except ClientError:
            logger.exception("Couldn't get value for secret %s.", self.name)
            raise
        else:
            return response
# snippet-end:[python.example_code.secrets-manager.GetSecretValue]

# snippet-start:[python.example_code.secrets-manager.GetRandomPassword]
    def get_random_password(self, pw_length):
        """
        Gets a randomly generated password.

        :param pw_length: The length of the password.
        :return: The generated password.
        """
        try:
            response = self.secretsmanager_client.get_random_password(
                PasswordLength=pw_length)
            password = response['RandomPassword']
            logger.info("Got random password.")
        except ClientError:
            logger.exception("Couldn't get random password.")
            raise
        else:
            return password
# snippet-end:[python.example_code.secrets-manager.GetRandomPassword]

# snippet-start:[python.example_code.secrets-manager.PutSecretValue]
    def put_value(self, secret_value, stages=None):
        """
        Puts a value into an existing secret. When no stages are specified, the
        value is set as the current ('AWSCURRENT') stage and the previous value is
        moved to the 'AWSPREVIOUS' stage. When a stage is specified that already
        exists, the stage is associated with the new value and removed from the old
        value.

        :param secret_value: The value to add to the secret.
        :param stages: The stages to associate with the secret.
        :return: Metadata about the secret.
        """
        if self.name is None:
            raise ValueError

        try:
            kwargs = {'SecretId': self.name}
            if isinstance(secret_value, str):
                kwargs['SecretString'] = secret_value
            elif isinstance(secret_value, bytes):
                kwargs['SecretBinary'] = secret_value
            if stages is not None:
                kwargs['VersionStages'] = stages
            response = self.secretsmanager_client.put_secret_value(**kwargs)
            logger.info("Value put in secret %s.", self.name)
        except ClientError:
            logger.exception("Couldn't put value in secret %s.", self.name)
            raise
        else:
            return response
# snippet-end:[python.example_code.secrets-manager.PutSecretValue]

# snippet-start:[python.example_code.secrets-manager.UpdateSecretVersionStage]
    def update_version_stage(self, stage, remove_from, move_to):
        """
        Updates the stage associated with a version of the secret.

        :param stage: The stage to update.
        :param remove_from: The ID of the version to remove the stage from.
        :param move_to: The ID of the version to add the stage to.
        :return: Metadata about the secret.
        """
        if self.name is None:
            raise ValueError

        try:
            response = self.secretsmanager_client.update_secret_version_stage(
                SecretId=self.name, VersionStage=stage, RemoveFromVersionId=remove_from,
                MoveToVersionId=move_to)
            logger.info("Updated version stage %s for secret %s.", stage, self.name)
        except ClientError:
            logger.exception(
                "Couldn't update version stage %s for secret %s.", stage, self.name)
            raise
        else:
            return response
# snippet-end:[python.example_code.secrets-manager.UpdateSecretVersionStage]

# snippet-start:[python.example_code.secrets-manager.DeleteSecret]
    def delete(self, without_recovery):
        """
        Deletes the secret.

        :param without_recovery: Permanently deletes the secret immediately when True;
                                 otherwise, the deleted secret can be restored within
                                 the recovery window. The default recovery window is
                                 30 days.
        """
        if self.name is None:
            raise ValueError

        try:
            self.secretsmanager_client.delete_secret(
                SecretId=self.name, ForceDeleteWithoutRecovery=without_recovery)
            logger.info("Deleted secret %s.", self.name)
            self._clear()
        except ClientError:
            logger.exception("Deleted secret %s.", self.name)
            raise
# snippet-end:[python.example_code.secrets-manager.DeleteSecret]

# snippet-start:[python.example_code.secrets-manager.ListSecrets]
    def list(self, max_results):
        """
        Lists secrets for the current account.

        :param max_results: The maximum number of results to return.
        :return: Yields secrets one at a time.
        """
        try:
            paginator = self.secretsmanager_client.get_paginator('list_secrets')
            for page in paginator.paginate(
                    PaginationConfig={'MaxItems': max_results}):
                for secret in page['SecretList']:
                    yield secret
        except ClientError:
            logger.exception("Couldn't list secrets.")
            raise
# snippet-end:[python.example_code.secrets-manager.ListSecrets]


def deploy(stack_name, cf_resource):
    """
    Deploys prerequisite resources used by the `usage_demo` script. The resources are
    defined in the associated `setup.yaml` AWS CloudFormation script and are deployed
    as a CloudFormation stack so they can be easily managed and destroyed.

    :param stack_name: The name of the CloudFormation stack.
    :param cf_resource: A Boto3 CloudFormation resource.
    """
    with open('setup.yaml') as setup_file:
        setup_template = setup_file.read()
    print(f"Creating {stack_name}.")
    stack = cf_resource.create_stack(
        StackName=stack_name,
        TemplateBody=setup_template,
        Capabilities=['CAPABILITY_NAMED_IAM'])
    print("Waiting for stack to deploy. This typically takes several minutes.")
    waiter = cf_resource.meta.client.get_waiter('stack_create_complete')
    waiter.wait(StackName=stack.name)
    stack.load()
    print(f"Stack status: {stack.stack_status}")
    print("Created resources:")
    for resource in stack.resource_summaries.all():
        print(f"\t{resource.resource_type}, {resource.physical_resource_id}")
    print("Outputs:")
    for oput in stack.outputs:
        print(f"\t{oput['OutputKey']}: {oput['OutputValue']}")


def sql_runner(rdsdata, resource_arn, secret_arn):
    """
    Creates a function that runs a SQL statement on an Amazon Aurora cluster.
    Because Amazon Aurora is serverless, the first time it is called the cluster might
    not be ready and will raise a BadRequestException. The runner function catches the
    exception, waits, and retries.

    :param rdsdata: A Boto3 Amazon RDS Data Service client.
    :param resource_arn: The Amazon Resource Name (ARN) of the Amazon Aurora cluster.
    :param secret_arn: The ARN of a secret that contains credentials required to
                       access the Amazon Aurora cluster.
    :return: A function that can be called to run SQL statements in the Amazon Aurora
             cluster.
    """
    def _run(statement, database=None):
        """
        Runs SQL statements in the specified Amazon Aurora cluster.

        :param statement: The SQL statement to run.
        :param database: When specified, the statement is run on this database.
                         Otherwise, the statement is run without a database context.
        :return: The response from Amazon RDS Data Service.
        """
        kwargs = {'resourceArn': resource_arn, 'secretArn': secret_arn,
                  'sql': statement}
        if database is not None:
            kwargs['database'] = database
        response = None
        tries = 5
        while tries > 0:
            try:
                response = rdsdata.execute_statement(**kwargs)
                break
            except rdsdata.exceptions.BadRequestException:
                print("Got BadRequestException. This occurs when the Aurora "
                      "database is not ready. Waiting and trying again...")
                time.sleep(10)
                tries -= 1
        return response
    return _run


# snippet-start:[python.example_code.secrets-manager.Scenario_CreateManageSecret]
def create_and_manage_secret_demo():
    """
    Shows how to use AWS Secrets Manager to create a secret, update its value and
    stage, and delete it.
    """
    secret = SecretsManagerSecret(boto3.client('secretsmanager'))

    print("Create a secret.")
    secret.create("doc-example-secretsmanager-secret", "Shh, don't tell.")
    print("Get secret value.")
    value = secret.get_value()
    print(f"Secret value: {value['SecretString']}")
    print("Get a random password.")
    password = secret.get_random_password(20)
    print(f"Got password: {password}")
    print("Put password as new secret value.")
    secret.put_value(password)
    print("Get current and previous values.")
    current = secret.get_value()
    previous = secret.get_value('AWSPREVIOUS')
    print(f"Current: {current['SecretString']}")
    print(f"Previous: {previous['SecretString']}")
    byteval = base64.b64encode("I'm a Base64 string!".encode('utf-8'))
    stage = 'CUSTOM_STAGE'
    print(f"Put byte value with a custom stage '{stage}'.")
    secret.put_value(byteval, [stage])
    time.sleep(1)
    print(f"Get secret value associated with stage '{stage}'.")
    got_val = secret.get_value(stage)
    print(f"Raw bytes value: {got_val['SecretBinary']}")
    print(f"Decoded value: {base64.b64decode(got_val['SecretBinary']).decode('utf-8')}")
    pprint(secret.describe())
    print("List 10 secrets for the account.")
    for sec in secret.list(10):
        print(f"Name: {sec['Name']}")
    print("Delete the secret.")
    secret.delete(True)
# snippet-end:[python.example_code.secrets-manager.Scenario_CreateManageSecret]


# snippet-start:[python.example_code.secrets-manager.Scenario_AuroraSecret]
def aurora_demo(resources):
    """
    Shows how to use AWS Secrets Manager to use an existing secret to run SQL
    statements on an Amazon Aurora cluster.

    :param resources: Resource identifiers that were output from the CloudFormation
                      stack that created prerequisite resources for the demo.
    """
    print('-'*88)
    print("Using a secret along with Amazon RDS Data Service to access an Amazon "
          "Aurora cluster.\n"
          "The secret and cluster were created by the CloudFormation stack included "
          "with this demo.")
    print('-'*88)
    secret = SecretsManagerSecret(boto3.client('secretsmanager'))
    cf_secret_arn = secret.describe(resources['SecretId'])['ARN']
    print(f"Secret ID: {resources['SecretId']}")
    print(f"Secret ARN: {cf_secret_arn}")
    secret_value = json.loads(secret.get_value()['SecretString'])
    print("Secret value:")
    pprint(secret_value)
    cluster_arn = resources['ClusterArn']
    rdsdata = boto3.client('rds-data')
    runner = sql_runner(rdsdata, cluster_arn, cf_secret_arn)
    print("Test connectivity by getting the current time from the Aurora cluster.")
    response = runner('SELECT NOW();')
    print(response['records'])

    db = 'DemoDatabase'
    table = 'People'
    print(f"Create a database '{db}' in the Aurora cluster, create a '{table}' table, "
          f"and insert some values.")
    runner(f"CREATE DATABASE {db};")
    runner(f"CREATE TABLE {table} (FirstName varchar(100), LastName varchar(100));", db)
    runner("INSERT INTO People VALUES ('Ted', 'Testerson');", db)
    runner("INSERT INTO People VALUES ('Edie', 'Exemplar');", db)
    runner("INSERT INTO People VALUES ('Chuck', 'Checkman');", db)
    print("Database created and populated.")

    print(f"Query the '{table}' table.")
    response = runner(f'SELECT * FROM {table};', db)
    print(f"Got {len(response['records'])} records:")
    pprint(response['records'])
# snippet-end:[python.example_code.secrets-manager.Scenario_AuroraSecret]


def destroy(stack, cf_resource):
    """
    Destroys the resources managed by the CloudFormation stack, and the CloudFormation
    stack itself.

    :param stack: The CloudFormation stack that manages the demo resources.
    :param cf_resource: A Boto3 CloudFormation resource.
    """
    print(f"Deleting stack {stack.name}.")
    stack.delete()
    print("Waiting for stack removal.")
    waiter = cf_resource.meta.client.get_waiter('stack_delete_complete')
    waiter.wait(StackName=stack.name)
    print("Stack delete complete.")


def main():
    parser = argparse.ArgumentParser(
        description="Runs the AWS Secrets Manager demo. "
                    "Run with the 'deploy' action to deploy prerequisite resources. "
                    "Run with the 'demo-secret' action to see the secret management demo. "
                    "Run with the 'demo-aurora' action to see the Amazon Aurora demo. "
                    "Run with the 'destroy' action to clean up all resources.")
    parser.add_argument(
        'action', choices=['deploy', 'demo-secret', 'demo-aurora', 'destroy'],
        help="Indicates the action the script performs.")
    args = parser.parse_args()

    print('-'*88)
    print("Welcome to the AWS Secrets Manager demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    cf_resource = boto3.resource('cloudformation')
    stack = cf_resource.Stack('python-example-code-secretsmanager-demo')

    if args.action == 'deploy':
        print("Deploying prerequisite resources for the demo.")
        deploy(stack.name, cf_resource)
        print('-'*88)
        print("To see example usage, run the script again with the 'demo-secret' or "
              "'demo-aurora' action.")
    elif args.action in ['demo-secret', 'demo-aurora']:
        print('-'*88)
        print("Demonstrating how to use AWS Secrets Manager to create and manage "
              "secrets.")
        print('-'*88)
        if args.action == 'demo-secret':
            create_and_manage_secret_demo()
        elif args.action == 'demo-aurora':
            aurora_demo({o['OutputKey']: o['OutputValue'] for o in stack.outputs})
        print('-'*88)
        print("To clean up all AWS resources created for the demo, run this script "
              "again with the 'destroy' action.")
    elif args.action == 'destroy':
        print("Destroying AWS resources created for the demo.")
        destroy(stack, cf_resource)

    print('-'*88)


if __name__ == '__main__':
    main()
