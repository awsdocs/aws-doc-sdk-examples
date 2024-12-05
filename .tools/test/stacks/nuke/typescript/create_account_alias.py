import subprocess


def create_account_alias(alias_name):
    """
    Create a new account alias with the given name.
    This function exists because the CDK does not support
    this specific CreateAccountAliases API call.
    """
    command = ["aws", "iam", "create-account-alias", "--account-alias", alias_name]
    result = subprocess.run(
        command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True
    )
    if result.returncode == 0:
        print(f"Account alias '{alias_name}' created successfully.")
    elif "EntityAlreadyExists" in result.stderr:
        print(f"Account alias '{alias_name}' already exists.")
    else:
        print(f"Error creating account alias '{alias_name}': {result.stderr}")
