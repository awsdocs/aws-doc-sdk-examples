# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_vault.py demonstrates how to delete an Amazon S3 Glacier vault.]
# snippet-service:[glacier]
# snippet-keyword:[Amazon Glacier]
# snippet-keyword:[Python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-12]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import logging
import boto3
from botocore.exceptions import ClientError


def delete_vault(vault_name):
    """Delete an Amazon S3Glacier vault

    :param vault_name: string
    :return: True if vault was deleted, otherwise False
    """

    # Delete the vault
    glacier = boto3.client('glacier')
    try:
        response = glacier.delete_vault(vaultName=vault_name)
    except ClientError as e:
        logging.error(e)
        return False
    return True


def main():
    """Exercise delete_vault()"""

    # Assign this value before running the program
    test_vault_name = 'VAULT_NAME'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Delete the vault
    success = delete_vault(test_vault_name)
    if success:
        logging.info(f'Deleted vault {test_vault_name}')


if __name__ == '__main__':
    main()
