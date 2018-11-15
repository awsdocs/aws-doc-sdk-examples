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


def retrieve_inventory(vault_name):
    """Initiate an Amazon Glacier inventory-retrieval job.

    To check the status of the job, call Glacier.Client.describe_job()
    To retrieve the output of the job, call Glacier.Client.get_job_output()

    :param vault_name: string
    :return: dict of information related to initiated job. If error, return None.
    """

    # Construct job parameters
    job_parms = {'Type': 'inventory-retrieval'}

    glacier = boto3.client('glacier')

    # Initiate the job
    try:
        response = glacier.initiate_job(vaultName=vault_name, jobParameters=job_parms)
    except Exception as e:
        return None
    return response


def main():
    test_vault_name = 'test-vault-name'

    response = retrieve_inventory(test_vault_name)
    if response is None:
        print('ERROR: Could not initiate inventory-retrieval job for {}.'.format(test_vault_name))
    else:
        print('Initiated inventory-retrieval job for {}'.format(test_vault_name))
        print(response)


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[retrieve_inventory_initiate.py demonstrates how to initiate an inventory-retrieval job for an Amazon Glacier vault.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Glacier]
# snippet-service:[glacier]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-13]
# snippet-sourceauthor:[scalwas (AWS)]
