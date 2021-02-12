# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[list_models]
# snippet-keyword:[describe_model]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_view_models.complete]

import boto3
import json

# Gets information about all models in an Amazon Lookout for Vsion project.
# project_name - the name of the project that you want to use.
def describe_models(project_name):

    try: 

        client=boto3.client('lookoutvision')

        #list models
        response=client.list_models(ProjectName=project_name)
        print('Project: ' + project_name)
        for model in response['Models']:
            print('Model version: ' + model['ModelVersion'])
            print('\tARN: ' + model['ModelArn']) 
            if 'Description'  in model:
                print('\tDescription: ' + model['Description']) 

            # Get model description
            model_description=client.describe_model(ProjectName=project_name, ModelVersion=model['ModelVersion'])
            print('\tStatus: ' + model_description['ModelDescription']['Status'])
            print('\tMessage: ' + model_description['ModelDescription']['StatusMessage'])
            print('\tCreated: ' + str(model_description['ModelDescription']['CreationTimestamp']))

            if model_description['ModelDescription']['Status'] == 'TRAINED':
                print('\tTraining duration: ' + str(model_description['ModelDescription']['EvaluationEndTimestamp']
                     - model_description['ModelDescription']['CreationTimestamp']))
                print('\tRecall: ' + str(model_description['ModelDescription']['Performance']['Recall']))
                print('\tPrecision: ' + str(model_description['ModelDescription']['Performance']['Precision']))
                print('\tF1: ' + str(model_description['ModelDescription']['Performance']['F1Score']))
                print('\tTraining output : s3://' + str(model_description['ModelDescription']['OutputConfig']['S3Location']['Bucket'])
                    + '/' + str(model_description['ModelDescription']['OutputConfig']['S3Location']['Prefix']))

            print()

        print('Done...')


    except Exception as e:
        print(e)
    
def main():
    project_name='my-project' #Change to the name of your project.
    describe_models(project_name)

if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_view_models.complete]