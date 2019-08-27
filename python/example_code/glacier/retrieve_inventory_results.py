# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[retrieve_inventory_results.py demonstrates how to retrieve the results of an Amazon S3 Glacier inventory-retrieval job.]
# snippet-service:[glacier]
# snippet-keyword:[Amazon S3 Glacier]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
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

import json
import logging
import boto3
from botocore.exceptions import ClientError


def retrieve_inventory_results(vault_name, job_id):
    """Retrieve the results of an Amazon Glacier inventory-retrieval job

    :param vault_name: string
    :param job_id: string. The job ID was returned by Glacier.Client.initiate_job()
    :return: Dictionary containing the results of the inventory-retrieval job.
    If error, return None.
    """

    # Retrieve the job results
    glacier = boto3.client('glacier')
    try:
        response = glacier.get_job_output(vaultName=vault_name, jobId=job_id)
    except ClientError as e:
        logging.error(e)
        return None

    # Read the streaming results into a dictionary
    return json.loads(response['body'].read())


def main():
    """Exercise retrieve_inventory_result()"""

    # Assign these values before running the program
    test_vault_name = 'VAULT_NAME'
    test_job_id = 'JOB_ID'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Retrieve the job results
    inventory = retrieve_inventory_results(test_vault_name, test_job_id)
    if inventory is not None:
        # Output some of the inventory information
        logging.info(f'Vault ARN: {inventory["VaultARN"]}')
        for archive in inventory['ArchiveList']:
            logging.info(f'  Size: {archive["Size"]:6d}  '
                         f'Archive ID: {archive["ArchiveId"]}')


if __name__ == '__main__':
    main()
