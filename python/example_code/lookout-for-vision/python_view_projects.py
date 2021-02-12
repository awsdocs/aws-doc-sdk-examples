# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[list_projects]
# snippet-keyword:[list_models]
# snippet-keyword:[describe_project]
# snippet-keyword:[describe_model]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_describe_project.complete]

import boto3

def list_projects():

    try:
        client=boto3.client('lookoutvision')

        response=client.list_projects()

        for project in response['Projects']:

            print("Project: " + project['ProjectName'])
            print("\tARN: " + project['ProjectArn']) 
            print('\tCreated: ' + str(['CreationTimestamp']))
            
            print ('Datasets')
            project_description=client.describe_project(ProjectName=project['ProjectName'])
            if len(project_description['ProjectDescription']['Datasets'])==0:
                print('\tNo datasets')
            else:
                for dataset in project_description['ProjectDescription']['Datasets']:
                    print('\ttype: ' + dataset['DatasetType'])
                    print('\tStatus: ' + dataset['StatusMessage'])

            print ('Models')
            #list models
            response_models=client.list_models(ProjectName=project['ProjectName'])
            if len(response_models['Models'])==0:
                    print('\tNo models')
            else:
                for model in response_models['Models']:
                    print('\tVersion: ' + model['ModelVersion'])
                    print('\tARN: ' + model['ModelArn']) 
                    if 'Description'  in model:
                        print('\tDescription: ' + model['Description']) 

                    # Get model description
                    model_description=client.describe_model(ProjectName=project['ProjectName'], ModelVersion=model['ModelVersion'])
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
            
            print()

    
    except Exception as e:
        print(e)
        
    print('Done...')
    
def main():
    list_projects()

if __name__ == "__main__":
    main()    
  
# snippet-end:[lookoutvision.python.lookoutvision_python_describe_project.complete]
