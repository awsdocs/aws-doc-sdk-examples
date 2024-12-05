# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import subprocess
import os
import yaml
import time
import re
from nuke.typescript.upload_job_scripts import process_stack_and_upload_files
from nuke.typescript.create_account_alias import create_account_alias

import boto3
from botocore.exceptions import ClientError, NoCredentialsError
import shutil

def get_caller_identity():
    try:
        # Create an STS client
        session = boto3.Session()
        sts_client = session.client('sts')

        # Get the caller identity
        caller_identity = sts_client.get_caller_identity()

        # Print the caller identity details
        print("Account ID:", caller_identity['Account'])
        print("Arn:", caller_identity['Arn'])
        print("UserId:", caller_identity['UserId'])

        # Check if temporary credentials are being used
        if 'SessionContext' in caller_identity:
            session_context = caller_identity['SessionContext']
            if session_context is not None:
                print("Temporary Credentials:")
                print("SessionName:", session_context.get('SessionName', 'N/A'))
                print("CreationDate:", session_context.get('CreationDate', 'N/A'))
                print("ExpirationDate:", session_context.get('Expiration', 'N/A'))
        else:
            print("Long-term Credentials")

    except NoCredentialsError:
        print("No credentials found in shared folder. Credentials wiped!")
    except ClientError as e:
        print(f"An error occurred: {e}")

def delete_aws_directory():
    # Path to the .aws directory
    aws_dir = os.path.expanduser('~/.aws')

    # Check if the directory exists
    if os.path.exists(aws_dir):
        try:
            # Recursively delete the directory and all its contents
            shutil.rmtree(aws_dir)
            print(f"Deleted all contents under {aws_dir}.")
        except Exception as e:
            print(f"Error deleting {aws_dir}: {e}")
    else:
        print(f"{aws_dir} does not exist.")

# Call the function
def run_shell_command(command, env_vars=None):
    """
    Execute a given shell command securely and return its output.

    Args:
    command (list): The command and its arguments listed as separate items.
    env_vars (dict, optional): Additional environment variables to set for the command.

    Outputs the result of the command execution to the console. In case of an error,
    it outputs the error message and the stack trace.
    """
    # Prepare the environment
    env = os.environ.copy()
    if env_vars:
        env.update(env_vars)

    command_str = " ".join(command)
    print("COMMAND: " + command_str)
    try:
        output = subprocess.check_output(command, stderr=subprocess.STDOUT, env=env)
        print(f"Command output: {output.decode()}")
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {e.output.decode()}")
        raise
    except Exception as e:
        print(f"Exception executing command: {e!r}")
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
    Get AWS tokens
    """
    get_token_tool = os.getenv('TOKEN_TOOL')
    get_token_provider = os.getenv('TOKEN_PROVIDER')

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
    os.environ['AWS_DEFAULT_REGION'] = 'us-east-1'
    get_caller_identity()

def deploy_resources(account_id, account_name, dir, lang="typescript"):
    """
    Deploy resources to a specified account using configuration specified by directory and language.

    Args:
    account_id (str): The AWS account ID where resources will be deployed.
    account_name (str): A human-readable name for the account, used for environment variables.
    dir (str): The base directory containing deployment scripts or configurations. One of: admin, plugin, images
    lang (str, optional): The programming language of the deployment scripts. Defaults to 'typescript'.

    Changes to the desired directory, sets up necessary environment variables, and executes
    deployment commands.
    """
    validate_alphanumeric(account_id, "account_id")
    validate_alphanumeric(account_name, "account_name")

    if dir not in os.getcwd():
        os.chdir(f"{dir}/{lang}")

    # Deploy using CDK
    run_shell_command(["cdk", "acknowledge", "31885"])
    deploy_command = ["cdk", "deploy", "--require-approval", "never"]
    print(" ".join(deploy_command))
    run_shell_command(deploy_command, env_vars={"TOOL_NAME": account_name})

    # Delay to avoid CLI conflicts
    # TODO: Add waiter
    time.sleep(15)

    # Wipe credentials
    delete_aws_directory()

    # Check to make sure credentials have been wiped
    get_caller_identity()


def main():
    parser = argparse.ArgumentParser(description="admin, images, or plugin stack.")
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
            print(f"Failed to read config data: \n{e}")
    elif args.type in {"plugin"}:
        try:
            with open("config/targets.yaml", "r") as file:
                accounts = yaml.safe_load(file)
        except Exception as e:
            print(f"Failed to read config data: \n{e}")
    
    if accounts is None:
        raise ValueError(f"Could not load accounts for stack {args.type}")

    if args.language:
        items = [(args.language, accounts[args.language])]
    else:
        items = accounts.items()
        
    for account_name, account_info in items:

        print(
            f"\n\n\n\n #### NEW DEPLOYMENT #### \n\n\n\n Deploying 🚀 Plugin stack to account {account_name} with ID {account_info['account_id']}"
        )
        get_tokens(account_info["account_id"])
        deploy_resources(
            account_info["account_id"],
            account_name,
            args.type
        )
        if 'plugin' in args.type:
            print(
                f"Deploying ☢️  AWS-Nuke to account {account_name} with ID {account_info['account_id']}"
            )
            os.chdir("../..")

            get_tokens(account_info["account_id"])
            create_account_alias('weathertop-test')

            get_tokens(account_info["account_id"])
            deploy_resources(account_info["account_id"], account_name, 'nuke')

            get_tokens(account_info["account_id"])
            process_stack_and_upload_files()

            os.chdir("../..")



if __name__ == "__main__":
    os.environ['JSII_SILENCE_WARNING_UNTESTED_NODE_VERSION'] = 'true'
    main()
