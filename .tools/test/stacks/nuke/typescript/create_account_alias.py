import logging
import subprocess

logger = logging.getLogger(__name__)


def create_account_alias(alias_name):
    """
    Create a new account alias with the given name.

    Args:
        alias_name (str): The desired name for the account alias.

    This function exists because the CDK does not support the specific
    CreateAccountAliases API call. It attempts to create an account alias
    using the AWS CLI and logs the result.

    If the account alias is created successfully, it logs a success message.
    If the account alias already exists, it logs a message indicating that.
    If there is any other error, it logs the error message.
    """
    command = ["aws", "iam", "create-account-alias", "--account-alias", alias_name]
    result = subprocess.run(
        command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True
    )
    if result.returncode == 0:
        logger.info(f"Account alias '{alias_name}' created successfully.")
    elif "EntityAlreadyExists" in result.stderr:
        logger.info(f"Account alias '{alias_name}' already exists.")
    else:
        logger.error(f"Error creating account alias '{alias_name}': {result.stderr}")
