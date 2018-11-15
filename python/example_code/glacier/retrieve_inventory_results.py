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

import json
import boto3


def retrieve_inventory_results(vault_name, job_id):
    """Retrieve the results of an Amazon Glacier inventory-retrieval job.

    :param vault_name: string
    :param job_id: string. The job ID was returned by Glacier.Client.initiate_job().
    :return: Dictionary containing the results of the inventory-retrieval job. If error, return None.
    """

    glacier = boto3.client('glacier')

    try:
        response = glacier.get_job_output(vaultName=vault_name, jobId=job_id)
    except Exception as e:
        # e.response['Error']['Code'] == 'ResourceNotFoundException' (vault or job ID was not found) or
        # 'InvalidParameterValueException' (job is not available for download)
        return None

    # Read results into dictionary
    return json.loads(response['body'].read())


def main():
    test_vault_name = 'test-vault-name'
    test_job_id = 'test-job-id'

    inventory = retrieve_inventory_results(test_vault_name, test_job_id)
    if inventory is None:
        print('ERROR: Could not retrieve inventory results.')
    else:
        # Show some of the inventory information on the console
        print('Vault ARN: {}'.format(inventory['VaultARN']))
        for archive in inventory['ArchiveList']:
            print('  Size: {:6d}  Archive ID: {}'.format(archive['Size'], archive['ArchiveId']))

        # Write the inventory info to a file
        with open('C:\\VaultInventory.json', 'w') as file:
            file.write(json.dumps(inventory))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[retrieve_inventory_results.py demonstrates how to retrieve the results of an Amazon Glacier inventory-retrieval job.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Glacier]
# snippet-service:[glacier]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-14]
# snippet-sourceauthor:[scalwas (AWS)]
