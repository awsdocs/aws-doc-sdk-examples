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


def upload_archive(vault_name, src_data):
    """Adds an archive to an Amazon Glacier vault. The upload occurs synchronously.

    :param vault_name: string
    :param src_data: bytes of data or string reference to file spec
    :return: If src_data was added to vault, return dict of archive information, otherwise None
    :raises FileNotFoundError, IOError: The src_data argument references a non-existent file spec
    :raises TypeError: The src_data argument is not of a supported type
    """

    # The src_data argument must be of type bytes or string
    # Construct body= parameter
    if isinstance(src_data, bytes):
        object_data = src_data
    elif isinstance(src_data, str):
        object_data = open(src_data, 'rb')      # possible FileNotFoundError/IOError exception
    else:
        raise TypeError('Type of ' + str(type(src_data)) + ' for the argument \'src_data\' is not supported.')

    glacier = boto3.client('glacier')
    try:
        archive = glacier.upload_archive(vaultName=vault_name, body=object_data)
    except Exception as e:
        # e.response['Error']['Code'] == 'ResourceNotFoundException' (if vault does not exist)
        return None
    finally:
        if isinstance(src_data, str):
            object_data.close()

    # archive dict contains 'location', 'checksum', and 'archiveId' information
    return archive


def main():
    test_vault_name = 'test-vault-name'
    filename = 'C:\\path\\to\\filename.ext'
    # Alternatively, specify object contents using bytes.
    # filename = b'This is the data to store in the Glacier vault.'
    try:
        archive = upload_archive(test_vault_name, filename)
    except (FileNotFoundError, IOError):
        print('ERROR: Could not find {}'.format(filename))
        exit(-1)
    except TypeError as e:
        print('ERROR: {}'.format(e))
        exit(-1)

    if archive is None:
        print('ERROR: Could not add archive to vault')
    else:
        print('Archive added to {}'.format(test_vault_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[upload_archive.py demonstrates how to add an archive to an Amazon Glacier vault.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Glacier]
# snippet-service:[glacier]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-12]
# snippet-sourceauthor:[scalwas (AWS)]
