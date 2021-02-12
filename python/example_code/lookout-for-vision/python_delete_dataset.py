# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[delete_dataset]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_delete_dataset.complete]


import boto3
import time

# Deletes an Amazon Lookout for Vision dataset
# project_name - the name of the project that contains the dataset that you want to delete.
# dataset_type = the type (train or test) of the dataset that you want to delete. 
def delete_dataset(project_name,dataset_type):

    client=boto3.client('lookoutvision')

    try: 
        #Delete the dataset
        print('Deleting dataset:' + dataset_type)
        client.delete_dataset(ProjectName=project_name,
          DatasetType=dataset_type)

        print('Done...')
    
    except Exception as e:
        print(e)
    
def main():
    project_name='my-project' # the desired project.
    dataset_type='train' # or 'test' to delete the test dataset.
    delete_dataset(project_name, dataset_type)

if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_delete_dataset.complete]