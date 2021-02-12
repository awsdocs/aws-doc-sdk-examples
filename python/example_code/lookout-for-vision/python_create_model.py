# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[create_model]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_create_model.complete]

import boto3
import json
import time

# Creates a version of an Amazon Lookout for Vision model.
# project_name - the name of the project in which you want to create a model.
# output_bucket - the output bucket in which to place training results.
# output_foler - the output folder in which to place training results.
# tag_key - the key for a tag to add to the model.
# tag_key_value - a value associated with the tag_key.
# Client_token - idempotent token value. Valid for 8 hours.
def create_model(project_name, output_bucket, output_folder, tag_key, tag_key_value, client_token):

    client=boto3.client('lookoutvision')

    try: 
        #Create a model
        print('Creating model...')

        output_config=json.loads('{ "S3Location": { "Bucket": "' + output_bucket + '", "Prefix": "'+ output_folder + '" } } ')
        tags=json.loads('[{"Key": "' + tag_key +'" ,"Value":"' + tag_key_value + '"}]')
        
        response=client.create_model(ProjectName=project_name, OutputConfig=output_config, Tags=tags, ClientToken=client_token)
        print('ARN: ' + response['ModelMetadata']['ModelArn'])
        print('Version: ' + response['ModelMetadata']['ModelVersion'])
        print('Started training...')
        
        while True:
            model_description=client.describe_model(ProjectName=project_name, ModelVersion=response['ModelMetadata']['ModelVersion'])
            status=model_description['ModelDescription']['Status']

            if status == 'TRAINING':
                print('Model training in progress...')
                time.sleep(600)
                continue
            
            if status =='TRAINED':
                print ('Model was successfully trained.')
                break
  
            if status =='TRAINING_FAILED':
                print ('Model training failed: ' + model_description['ModelDescription']['StatusMessage'])
                break

            print ('Failed. Unexpected state for training: ' + status)
            break
        
        print('Done...')

    
    except Exception as e:
        print(e)
    
def main():
    project_name='my-project' # the project in which to create a model.
    output_bucket = 'output-bucket' # bucket to store training output.
    output_folder = 'my-project-output-folder/' # folder for training output.
    tag_key = 'my-tag-key' # tag key.
    tag_key_value = 'my-tag-key-value' # tag value.
    client_token = '1' # idempotency token.

    create_model(project_name, output_bucket, output_folder, tag_key, tag_key_value, client_token)

if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_create_model.complete]