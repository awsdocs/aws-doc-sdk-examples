"""
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

This module contains functions to deploy resources to AWS accounts using AWS CDK.
"""

import argparse
import logging
import os
import re
import shutil
import subprocess
import time

import boto3
import yaml
from botocore.exceptions import ClientError, NoCredentialsError

from nuke.typescript.create_account_alias import create_account_alias
from nuke.typescript.upload_job_scripts import process_stack_and_upload_files

logger = logging.getLogger(__name__)
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)


def get_caller_identity():
    """
    Get the caller identity from AWS STS.

    Logs the account ID, ARN, and user ID of the caller.
    Logs an error if no credentials are found or if there is a client error.
    """
    try:
        # Create an STS client
        session = boto3.Session()
        sts_client = session.client("sts")

        # Get the caller identity
        caller_identity = sts_client.get_caller_identity()

        # Log the caller identity details
        logger.info(f"Credentials Account ID: {caller_identity['Account']}")
        logger.debug(f"Arn: {caller_identity['Arn']}")
        logger.debug(f"UserId: {caller_identity['UserId']}")

    except NoCredentialsError:
        logger.info("No credentials found in shared folder. Credentials wiped!")
    except ClientError as e:
        logger.error(f"An error occurred: {e}")


def delete_aws_directory():
    """
    Delete the .aws directory in the user's home directory.

    This function removes the .aws directory and all its contents from the user's
    home directory. If the directory does not exist, it logs a message.
    """
    # Path to the .aws directory
    aws_dir = os.path.expanduser("~/.aws")

    # Check if the directory exists
    if os.path.exists(aws_dir):
        try:
            # Recursively delete the directory and all its contents
            shutil.rmtree(aws_dir)
            logger.info(f"Deleted all contents under {aws_dir}.")
        except Exception as e:
            logger.error(f"Error deleting {aws_dir}: {e}")
    else:
        logger.info(f"{aws_dir} does not exist.")


def run_shell_command(command, env_vars=None):
    """
    Execute a given shell command securely and log its output.

    Args:
        command (list): The command and its arguments listed as separate items.
        env_vars (dict, optional): Additional environment variables to set for the command.

    Logs the command being executed and its output. In case of an error,
    it logs the error message and the stack trace.
    """
    # Prepare the environment
    env = os.environ.copy()
    if env_vars:
        env.update(env_vars)

    command_str = " ".join(command)
    logger.info(f"COMMAND: {command_str}")
    try:
        output = subprocess.check_output(command, stderr=subprocess.STDOUT, env=env)
        logger.info(f"STDOUT:\n{output.decode()}")
    except subprocess.CalledProcessError as e:
        logger.error(f"Error executing command: {e.output.decode()}")
        raise
    except Exception as e:
        logger.error(f"Exception executing command: {e!r}")
        raise


def validate_alphanumeric(value, name):
    """
    Validate that the given value is alphanumeric.

    Args:
        value (str): The value to validate.
        name (str): The name of the variable for error messages.

    Raises:
        ValueError: If the value is not alphanumeric.
    """
    if not re.match(r"^\w+$", value):
        raise ValueError(f"{name} must be alphanumeric. Received: {value}")


def get_tokens(account_id):
    """
    Get AWS tokens for the specified account.

    Args:
        account_id (str): The AWS account ID for which tokens will be obtained.

    Retrieves temporary AWS credentials for the specified account using a token tool
    and provider specified in environment variables. Sets the AWS_DEFAULT_REGION
    environment variable and logs the caller identity.
    """
    get_token_tool = os.getenv("TOKEN_TOOL")
    get_token_provider = os.getenv("TOKEN_PROVIDER")

    # Securely update tokens
    get_tokens_command = [
        get_token_tool,
        "credentials",
        "update",
        "--account",
        account_id,
        "--provider",
        get_token_provider,
        "--role",
        "weathertop-cdk-deployments",
        "--once",
    ]
    run_shell_command(get_tokens_command)
    os.environ["AWS_DEFAULT_REGION"] = "us-east-1"
    get_caller_identity()


def deploy_resources(account_id, account_name, dir_path, lang="typescript"):
    """
    Deploy resources to a specified account using configuration specified by directory and language.

    Args:
        account_id (str): The AWS account ID where resources will be deployed.
        account_name (str): A human-readable name for the account, used for environment variables.
        dir_path (str): The base directory containing deployment scripts or configurations. One of: admin, plugin, images
        lang (str, optional): The programming language of the deployment scripts. Defaults to 'typescript'.

    Changes to the desired directory, sets up necessary environment variables, and executes
    deployment commands.
    """
    validate_alphanumeric(account_id, "account_id")
    validate_alphanumeric(account_name, "account_name")

    if dir_path not in os.getcwd():
        os.chdir(os.path.join(dir_path, lang))

    # Deploy using CDK
    run_shell_command(["cdk", "acknowledge", "31885"])
    deploy_command = ["cdk", "deploy", "--require-approval", "never"]
    logger.info(" ".join(deploy_command))
    run_shell_command(deploy_command, env_vars={"TOOL_NAME": account_name})

    # Delay to avoid CLI conflicts
    # TODO: Add waiter
    time.sleep(15)

    # Wipe credentials
    delete_aws_directory()

    # Check to make sure credentials have been wiped
    get_caller_identity()


def main():
    """
    Main function to deploy resources to AWS accounts based on the specified stack type.
    """
    parser = argparse.ArgumentParser(
        description="Deploy admin, images, or plugin stack."
    )
    parser.add_argument("type", choices=["admin", "images", "plugin"])
    parser.add_argument("--language")
    args = parser.parse_args()

    accounts = None

    if args.type in {"admin", "images"}:
        try:
            with open("config/resources.yaml", "r") as file:
                data = yaml.safe_load(file)
                accounts = {
                    "admin": {
                        "account_id": f"{data['admin_acct']}",
                        "status": "enabled",
                    }
                }
        except Exception as e:
            logger.error(f"Failed to read config data: {e}")
    elif args.type == "plugin":
        try:
            with open("config/targets.yaml", "r") as file:
                accounts = yaml.safe_load(file)
        except Exception as e:
            logger.error(f"Failed to read config data: {e}")

    if accounts is None:
        raise ValueError(f"Could not load accounts for stack {args.type}")

    if args.language:
        items = [(args.language, accounts[args.language])]
    else:
        items = accounts.items()

    for account_name, account_info in items:
        logger.info(
            f"\n\n\n\n #### NEW DEPLOYMENT #### \n\n\n\n Deploying 🚀 {args.type} stack to account {account_name} with ID {account_info['account_id']}"
        )
        get_tokens(account_info["account_id"])
        deploy_resources(account_info["account_id"], account_name, args.type)
        if "plugin" in args.type:
            logger.info(
                f"Deploying ☢️  AWS-Nuke to account {account_name} with ID {account_info['account_id']}"
            )
            os.chdir("../..")

            get_tokens(account_info["account_id"])
            create_account_alias("weathertop-test")

            get_tokens(account_info["account_id"])
            deploy_resources(account_info["account_id"], account_name, "nuke")

            get_tokens(account_info["account_id"])
            process_stack_and_upload_files()

            os.chdir("../..")


if __name__ == "__main__":
    os.environ["JSII_SILENCE_WARNING_UNTESTED_NODE_VERSION"] = "true"
    main()
