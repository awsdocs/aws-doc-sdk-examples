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


def delete_archive(vault_name, archive_id):
    """Delete an archive from an Amazon Glacier vault.

    :param vault_name: string
    :param archive_id: string
    :return: True if archive was deleted, otherwise False
    """

    glacier = boto3.client('glacier')

    try:
        response = glacier.delete_archive(vaultName=vault_name, archiveId=archive_id)
    except Exception as e:
        # e.response['Error']['Code'] == 'ResourceNotFoundException'
        return False
    return True


def main():
    test_vault_name = 'test-vault-name'
    test_archive_id = 'test-archive-id'

    success = delete_archive(test_vault_name, test_archive_id)
    if not success:
        print('ERROR: Could not delete archive.')
    else:
        print('Deleted archive.')


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_archive.py demonstrates how to delete an archive from an Amazon Glacier vault.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Glacier]
# snippet-service:[glacier]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-14]
# snippet-sourceauthor:[scalwas (AWS)]
