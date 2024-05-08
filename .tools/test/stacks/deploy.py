# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import subprocess
import os
import yaml
import time
import traceback
import re


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
        print("Stack Trace:")
        traceback.print_exc()


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


def deploy_resources(account_id, account_name, dir, lang="typescript"):
    """
    Deploy resources to a specified account using configuration specified by directory and language.

    Args:
    account_id (str): The AWS account ID where resources will be deployed.
    account_name (str): A human-readable name for the account, used for environment variables.
    dir (str): The base directory containing deployment scripts or configurations.
    lang (str, optional): The programming language of the deployment scripts. Defaults to 'typescript'.

    Changes to the desired directory, sets up necessary environment variables, and executes
    deployment commands.
    """
    validate_alphanumeric(account_id, "account_id")
    validate_alphanumeric(account_name, "account_name")

    if dir not in os.getcwd():
        os.chdir(f"{dir}/{lang}")

    # Securely update tokens
    get_tokens_command = [
        "ada",
        "credentials",
        "update",
        "--account",
        account_id,
        "--provider",
        "isengard",
        "--role",
        "weathertop-cdk-deployments",
        "--once",
    ]
    run_shell_command(get_tokens_command)

    # Deploy using CDK
    deploy_command = ["cdk", "deploy", "--require-approval", "never"]
    print(" ".join(deploy_command))
    run_shell_command(deploy_command, env_vars={"TOOL_NAME": account_name})

    # Delay to avoid CLI conflicts
    # TODO: Add waiter
    time.sleep(15)


def main():
    parser = argparse.ArgumentParser(description="admin, images, or plugin flag.")
    parser.add_argument("type", choices=["admin", "images", "plugin"])
    args = parser.parse_args()

    if args.type in {"admin", "images"}:
        try:
            with open(".config/resources.yaml", "r") as file:
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
            with open(".config/targets.yaml", "r") as file:
                accounts = yaml.safe_load(file)
        except Exception as e:
            print(f"Failed to read config data: \n{e}")

    for account_name, account_info in accounts.items():
        print(
            f"Reading from account {account_name} with ID {account_info['account_id']}"
        )
        deploy_resources(account_info["account_id"], account_name, args.type)


if __name__ == "__main__":
    main()
