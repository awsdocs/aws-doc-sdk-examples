# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[describe_job.py demonstrates how to retrieve the status of an Amazon S3 Glacier inventory-retrieval job.]
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

import logging
import boto3
from botocore.exceptions import ClientError


def describe_job(vault_name, job_id):
    """Retrieve the status of an Amazon S3 Glacier job, such as an
    inventory-retrieval job

    To retrieve the output of the finished job, call Glacier.Client.get_job_output()

    :param vault_name: string
    :param job_id: string. The job ID was returned by Glacier.Client.initiate_job().
    :return: Dictionary of information related to the job. If error, return None.
    """

    # Retrieve the status of the job
    glacier = boto3.client('glacier')
    try:
        response = glacier.describe_job(vaultName=vault_name, jobId=job_id)
    except ClientError as e:
        logging.error(e)
        return None
    return response


def main():
    """Exercise describe_job()"""

    # Assign the following values before running the program
    test_vault_name = 'VAULT_NAME'
    test_job_id = 'JOB_ID'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Retrieve the job's status
    response = describe_job(test_vault_name, test_job_id)
    if response is not None:
        logging.info(f'Job Type: {response["Action"]}, '
                     f'Status: {response["StatusCode"]}')


if __name__ == '__main__':
    main()
