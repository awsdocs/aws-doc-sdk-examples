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


def describe_job(vault_name, job_id):
    """Retrieve the status of an Amazon Glacier job, such as an inventory-retrieval job.

    To retrieve the output of the job, call Glacier.Client.get_job_output()

    :param vault_name: string
    :param job_id: string. The job ID was returned by Glacier.Client.initiate_job().
    :return: Dictionary of information related to the job. If error, return None.
    """

    glacier = boto3.client('glacier')

    try:
        response = glacier.describe_job(vaultName=vault_name, jobId=job_id)
    except Exception as e:
        # e.response['Error']['Code'] == 'ResourceNotFoundException' (vault or job ID was not found)
        return None
    return response


def main():
    test_vault_name = 'test-vault-name'
    test_job_id = 'test-job-id'

    response = describe_job(test_vault_name, test_job_id)
    if response is None:
        print('ERROR: Could not retrieve job status.')
    else:
        print('Job Type: {0}, Status: {1}'.format(response['Action'], response['StatusCode']))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[describe_job.py demonstrates how to retrieve the status of an Amazon Glacier inventory-retrieval job.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Glacier]
# snippet-service:[glacier]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-13]
# snippet-sourceauthor:[scalwas (AWS)]
