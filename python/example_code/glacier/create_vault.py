# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


import boto3


def create_vault(vault_name):
    """Create an Amazon Glacier vault.

    :param vault_name: string
    :return: glacier.Vault object if vault was created, otherwise None
    """

    glacier = boto3.resource('glacier')
    try:
        vault = glacier.create_vault(vaultName=vault_name)
    except Exception as e:
        # e.response['ResponseMetadata']['HTTPStatusCode'] = 400 Bad Request
        # e.response['Error']['Code'] == InvalidParameterValueException (invalid vault name), etc.
        return None
    return vault


def main():
    test_vault_name = 'test-vault-name'
    vault = create_vault(test_vault_name)
    if vault is None:
        print('ERROR: Could not create vault {}'.format(test_vault_name))
    else:
        print('Created vault {}'.format(vault.name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[create_vault.py demonstrates how to create an Amazon Glacier vault.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Glacier]
# snippet-service:[glacier]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-12]
# snippet-sourceauthor:[scalwas (AWS)]
