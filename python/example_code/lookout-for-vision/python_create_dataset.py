
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[create_dataset]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_create_dataset.complete]
# Creates an Amazon Lookout for Vision dataset

import boto3
import json
import time

# Creates a new dataset
# project_name - the name of the project in which you want to create a dataset.
# bucket - the bucket that contains the manifest file.
# manifest_file - the path and name of the manifest file.
# dataset_type - the type of the dataset (train or test).
def create_dataset(project_name, bucket, manifest_file, dataset_type):

    try: 

        client=boto3.client('lookoutvision')

        #Create a dataset
        print('Creating dataset...')
        dataset=json.loads('{ "GroundTruthManifest": { "S3Object": { "Bucket": "' + bucket + '", "Key": "'+ manifest_file + '" } } }')

        response=client.create_dataset(ProjectName=project_name, DatasetType=dataset_type, DatasetSource=dataset)
        print('Dataset Status: ' + response['DatasetMetadata']['Status'])
        print('Dataset Status Message: ' + response['DatasetMetadata']['StatusMessage'])
        print('Dataset Type: ' + response['DatasetMetadata']['DatasetType'])

            # Wait until either created or failed.
        while True:
            
            dataset_description=client.describe_dataset(ProjectName=project_name, DatasetType=dataset_type)
            status=dataset_description['DatasetDescription']['Status']

            if status == 'CREATE_IN_PROGRESS':
                print('Dataset creation in progress...')
                time.sleep(2)
                continue
            
            if status =='CREATE_COMPLETE':
                print ('Dataset created.')
                break
  
            if status =='CREATE_FAILED':
                print ('Dataset creation failed.')
                break

            print ('Failed. Unexpected state for dataset creation: ' + status)
            break

        print('Done...')

    
    except Exception as e:
        print(e)
    
def main():
    project_name='my-project' # Change to your project name.
    bucket = 'bucket' #C hange to the bucket with the manifest file.
    manifest_file = 'input.manifest' # Change to your mainifest file.
    dataset_type ='train' # or 'test' to create the test dataset.

    create_dataset(project_name, bucket, manifest_file, dataset_type)


if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_create_dataset.complete]