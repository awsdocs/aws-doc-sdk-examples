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


def list_vaults(max_vaults=10, iter_marker=None):
    """List Amazon Glacier vaults owned by the AWS account.

    :param max_vaults: Maximum number of vaults to retrieve.
    :param iter_marker: Marker used to identify start of next batch of vaults to retrieve
    :return: List of dictionaries containing vault information
    :return: String marking the start of next batch of vaults to retrieve. Pass this string as the iter_marker argument
        in the next invocation of list_vaults().
    """

    glacier = boto3.client('glacier')
    # Retrieve vaults
    if iter_marker is None:
        vaults = glacier.list_vaults(limit=str(max_vaults))
    else:
        vaults = glacier.list_vaults(limit=str(max_vaults), marker=iter_marker)
    marker = vaults.get('Marker')       # None if no more vaults to retrieve
    return vaults['VaultList'], marker


def main():
    vaults, marker = list_vaults()
    while True:
        # Print info about retrieved vaults
        for vault in vaults:
            print('{:3d}  {:12d}  {}'.format(vault['NumberOfArchives'], vault['SizeInBytes'], vault['VaultName']))

        # If no more vaults exist, exit loop, otherwise retrieve the next batch
        if marker is None:
            break

        vaults, marker = list_vaults(iter_marker=marker)


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[list_vaults.py demonstrates how to list the Amazon Glacier vaults owned by the AWS account.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Glacier]
# snippet-service:[glacier]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-12]
# snippet-sourceauthor:[scalwas (AWS)]
