# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[describe_dataset]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_describe_dataset.complete]
import boto3
import json

# Gets information about an Amazon Lookout for Vision dataset.
# project_name - the name of the project that contains the dataset that you want to describe.
# dataset_type - the type (train or test) of the dataset that you want to describe.
def describe_dataset(project_name, dataset_type):

    try: 
    
        client=boto3.client('lookoutvision')

        #Describe a dataset

        response=client.describe_dataset(ProjectName=project_name, DatasetType=dataset_type)
        print('Name: ' +  response['DatasetDescription']['ProjectName'])
        print('Type: ' + response['DatasetDescription']['DatasetType'])
        print('Status: ' + response['DatasetDescription']['Status'])
        print('Message: ' + response['DatasetDescription']['StatusMessage'])
        print('Images: ' + str(response['DatasetDescription']['ImageStats']['Total'] ))
        print('Labeled: ' + str(response['DatasetDescription']['ImageStats']['Labeled'] ))
        print('Normal: ' + str(response['DatasetDescription']['ImageStats']['Normal'] ))
        print('Anomaly: ' + str(response['DatasetDescription']['ImageStats']['Anomaly'] ))

        print('Done...')


    except Exception as e:
        print(e)
    
def main():
    project_name='my-release-project' # Change to the project name that contains the dataset.
    dataset_type ='train' # Change to the dataset type (train or test)
    describe_dataset(project_name,dataset_type)

if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_describe_dataset.complete]