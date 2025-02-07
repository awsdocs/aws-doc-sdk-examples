# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
This module is used to create an AWS account alias, which is required by the deploy.py script.

It provides a function to create an account alias using the AWS CLI, as this specific
operation is not supported by the AWS CDK.
"""

import logging
import re
import subprocess

logger = logging.getLogger(__name__)


def _is_valid_alias(alias_name: str) -> bool:
    """
    Check if the provided alias name is valid according to AWS rules.

    AWS account alias must be unique and must be between 3 and 63 characters long.
    Valid characters are a-z, 0-9 and '-'.

    Args:
        alias_name (str): The alias name to validate.

    Returns:
        bool: True if the alias is valid, False otherwise.
    """
    pattern = r"^[a-z0-9](([a-z0-9]|-){0,61}[a-z0-9])?$"
    return bool(re.match(pattern, alias_name)) and 3 <= len(alias_name) <= 63


def _log_aws_cli_version() -> None:
    """
    Log the version of the AWS CLI installed on the system.
    """
    try:
        result = subprocess.run(["aws", "--version"], capture_output=True, text=True)
        logger.info(f"AWS CLI version: {result.stderr.strip()}")
    except Exception as e:
        logger.warning(f"Unable to determine AWS CLI version: {str(e)}")


def create_account_alias(alias_name: str) -> None:
    """
    Create a new account alias with the given name.

    This function exists because the CDK does not support the specific
    CreateAccountAliases API call. It attempts to create an account alias
    using the AWS CLI and logs the result.

    If the account alias is created successfully, it logs a success message.
    If the account alias already exists, it logs a message indicating that.
    If there is any other error, it logs the error message.

    Args:
        alias_name (str): The desired name for the account alias.
    """
    # Log AWS CLI version when the function is called
    _log_aws_cli_version()

    if not _is_valid_alias(alias_name):
        logger.error(
            f"Invalid alias name '{alias_name}'. It must be between 3 and 63 characters long and contain only lowercase letters, numbers, and hyphens."
        )
        return

    command = ["aws", "iam", "create-account-alias", "--account-alias", alias_name]

    try:
        subprocess.run(
            command,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            check=True,
        )
        logger.info(f"Account alias '{alias_name}' created successfully.")
    except subprocess.CalledProcessError as e:
        if "EntityAlreadyExists" in e.stderr:
            logger.info(f"Account alias '{alias_name}' already exists.")
        elif "AccessDenied" in e.stderr:
            logger.error(
                f"Access denied when creating account alias '{alias_name}'. Check your AWS credentials and permissions."
            )
        elif "ValidationError" in e.stderr:
            logger.error(
                f"Validation error when creating account alias '{alias_name}'. The alias might not meet AWS requirements."
            )
        else:
            logger.error(f"Error creating account alias '{alias_name}': {e.stderr}")
    except Exception as e:
        logger.error(
            f"Unexpected error occurred while creating account alias '{alias_name}': {str(e)}"
        )


def main():
    import argparse

    # Set up logging
    logging.basicConfig(
        level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
    )

    # Create argument parser
    parser = argparse.ArgumentParser(description="Create an AWS account alias")
    parser.add_argument("alias", help="The alias name for the AWS account")

    # Parse arguments
    args = parser.parse_args()

    # Call the function with the provided alias
    create_account_alias(args.alias)

if __name__ == "__main__":
    main()
