# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
AWS CDK Deployment Script for SDK Testing Infrastructure

This script manages the deployment of AWS infrastructure components needed for SDK testing.
It handles three main deployment scenarios:

1. ECR Repository Setup (--type images):
   Creates empty ECR private repositories for tools listed in targets.yaml

2. Admin Stack Deployment (--type admin):
   Deploys a stack that emits events and contains IAM policies allowing
   cross-account subscription from AWS accounts listed in targets.yaml

3. Plugin Stack Deployment (--type plugin):
   Deploys two stacks in each target account:
   - A plugin stack that subscribes to the admin stack's events
   - An account nuker stack that cleans up resources left by test executions

Prerequisites:
    - TOKEN_TOOL environment variable set to the token generation tool path
    - TOKEN_PROVIDER environment variable set to the token provider
    - Appropriate AWS credentials and permissions
    - Valid configuration in config/resources.yaml and config/targets.yaml

Note:
    This script uses subprocess to run CDK commands as CDK doesn't support
    direct module import. While this creates some brittleness, it provides
    necessary flexibility for cross-account deployments.
"""

import argparse
import logging
import os
import re
import shutil
import subprocess
import time
from pathlib import Path
from typing import Any, Dict, List, Optional, Union

import boto3
import yaml
from botocore.exceptions import ClientError, NoCredentialsError

from nuke.typescript.create_account_alias import create_account_alias
from nuke.typescript.upload_job_scripts import process_stack_and_upload_files

# Constants
AWS_DEFAULT_REGION = "us-east-1"
CDK_DEPLOYMENT_ROLE = "weathertop-cdk-deployments"
ACCOUNT_ALIAS = "weathertop-test"
CDK_ACKNOWLEDGE_ID = "31885"

# Configure logging
logger = logging.getLogger(__name__)
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)


class DeploymentError(Exception):
    """Custom exception for deployment-related errors."""

    pass


class ConfigurationManager:
    """Manages loading and validation of configuration files."""

    def __init__(self, config_dir: Path = Path("config")):
        self.config_dir = config_dir

    def load_admin_config(self) -> Dict[str, Dict[str, str]]:
        """Load admin configuration from resources.yaml."""
        try:
            with open(self.config_dir / "resources.yaml") as file:
                data = yaml.safe_load(file)
                return {
                    "admin": {
                        "account_id": str(data["admin_acct"]),
                        "status": "enabled",
                    }
                }
        except Exception as e:
            logger.error(f"Failed to read admin config: {e}")
            raise

    def load_target_accounts(self) -> Dict[str, Any]:
        """Load target accounts from targets.yaml."""
        try:
            with open(self.config_dir / "targets.yaml") as file:
                return yaml.safe_load(file)
        except Exception as e:
            logger.error(f"Failed to read targets config: {e}")
            raise


def get_caller_identity() -> None:
    """
    Get the caller identity from AWS STS.

    Logs the account ID, ARN, and user ID of the caller.
    Logs an error if no credentials are found or if there is a client error.
    """
    try:
        session = boto3.Session()
        sts_client = session.client("sts")
        caller_identity = sts_client.get_caller_identity()

        logger.info(f"Credentials Account ID: {caller_identity['Account']}")
        logger.debug(f"Arn: {caller_identity['Arn']}")
        logger.debug(f"UserId: {caller_identity['UserId']}")

    except NoCredentialsError:
        logger.info("No credentials found in shared folder. Credentials wiped!")
    except ClientError as e:
        logger.error(f"An error occurred: {e}")

def run_shell_command(
    command: List[str], env_vars: Optional[Dict[str, str]] = None
) -> None:
    """
    Execute a given shell command securely and log its output.

    Args:
        command: The command and its arguments listed as separate items.
        env_vars: Additional environment variables to set for the command.

    Raises:
        subprocess.CalledProcessError: If the command execution fails.
    """
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


def validate_alphanumeric(value: str, name: str) -> None:
    """
    Validate that the given value is alphanumeric.

    Args:
        value: The value to validate.
        name: The name of the variable for error messages.

    Raises:
        ValueError: If the value is not alphanumeric.
    """
    if not re.match(r"^\w+$", value):
        raise ValueError(f"{name} must be alphanumeric. Received: {value}")


def get_tokens(account_id: str) -> None:
    """
    Get AWS tokens for the specified account.

    Args:
        account_id: The AWS account ID for which tokens will be obtained.
    """
    get_token_tool = os.getenv("TOKEN_TOOL")
    get_token_provider = os.getenv("TOKEN_PROVIDER")

    if not all([get_token_tool, get_token_provider]):
        raise DeploymentError(
            "TOKEN_TOOL and TOKEN_PROVIDER environment variables must be set"
        )

    get_tokens_command = [
        get_token_tool,
        "credentials",
        "update",
        "--account",
        account_id,
        "--provider",
        get_token_provider,
        "--role",
        CDK_DEPLOYMENT_ROLE,
        "--once",
    ]
    run_shell_command(get_tokens_command)
    os.environ["AWS_DEFAULT_REGION"] = AWS_DEFAULT_REGION
    get_caller_identity()


def deploy_resources(
    account_id: str,
    account_name: str,
    dir_path: Union[str, Path],
    lang: str = "typescript",
) -> None:
    """
    Deploy resources to a specified account using configuration specified by directory and language.

    Args:
        account_id: The AWS account ID where resources will be deployed.
        account_name: A human-readable name for the account, used for environment variables.
        dir_path: The base directory containing deployment scripts or configurations.
        lang: The programming language of the deployment scripts.
    """
    validate_alphanumeric(account_id, "account_id")
    validate_alphanumeric(account_name, "account_name")

    if dir_path not in os.getcwd():
        os.chdir(os.path.join(dir_path, lang))

    run_shell_command(["cdk", "acknowledge", CDK_ACKNOWLEDGE_ID])
    deploy_command = ["cdk", "deploy", "--require-approval", "never"]
    run_shell_command(deploy_command, env_vars={"TOOL_NAME": account_name})

    # Delay to avoid CLI conflicts
    # TODO: Replace with proper waiter implementation
    time.sleep(15)

    get_caller_identity()


def deploy_stacks(
    stack_type: str, accounts: Dict[str, Any], language: Optional[str]
) -> None:
    """
    Deploy the specified stack type to all target accounts.

    Args:
        stack_type: Type of stack to deploy (admin, images, or plugin)
        accounts: Dictionary of account configurations
        language: Optional specific language to deploy for
    """
    items = [(language, accounts[language])] if language else accounts.items()

    for account_name, account_info in items:
        logger.info(
            f"\n\n\n\n #### NEW DEPLOYMENT #### \n\n\n\n"
            f"Deploying 🚀 {stack_type} stack to account {account_name}"
            f" with ID {account_info['account_id']}"
        )

        get_tokens(account_info["account_id"])
        deploy_resources(account_info["account_id"], account_name, stack_type)

        if stack_type == "plugin":
            logger.info(
                f"Deploying ☢️  AWS-Nuke to account {account_name}"
                f" with ID {account_info['account_id']}"
            )
            os.chdir("../..")

            get_tokens(account_info["account_id"])
            create_account_alias(ACCOUNT_ALIAS)

            get_tokens(account_info["account_id"])
            deploy_resources(account_info["account_id"], account_name, "nuke")

            get_tokens(account_info["account_id"])
            process_stack_and_upload_files()

            os.chdir("../..")


def main() -> None:
    """Execute the main deployment workflow."""
    parser = argparse.ArgumentParser(
        description="Deploy admin, images, or plugin stack.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "type", choices=["admin", "images", "plugin"], help="Type of stack to deploy"
    )
    parser.add_argument("--language", help="Specific language to deploy for")
    args = parser.parse_args()

    config_manager = ConfigurationManager()

    try:
        accounts = (
            config_manager.load_admin_config()
            if args.type in {"admin", "images"}
            else config_manager.load_target_accounts()
        )

        if not accounts:
            raise DeploymentError(f"No accounts found for stack type: {args.type}")

        deploy_stacks(args.type, accounts, args.language)

    except Exception as e:
        logger.error(f"Deployment failed: {e}")
        raise


if __name__ == "__main__":
    os.environ["JSII_SILENCE_WARNING_UNTESTED_NODE_VERSION"] = "true"
    main()
