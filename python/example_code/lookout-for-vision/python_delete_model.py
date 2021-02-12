# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[delete_model]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_delete_model.complete]

import boto3
import time

# Deletes an Amazon Lookout for Vision model. The model must first be stopped and can't be in training.
# project_name - the name of the project that contains the desired model.
# model_version - the version of the model that you want to delete. 
def delete_model(project_name,model_version):

    try: 
        client=boto3.client('lookoutvision')

        #Delete the model
        print('Deleting model:' + model_version)
        response=client.delete_model(ProjectName=project_name,
            ModelVersion=model_version)
        
        model_exists = True

        while model_exists:
            model_exists=False
            response=client.list_models(ProjectName=project_name)
            for model in response['Models']:
                if model['ModelVersion'] == model_version:
                    model_exists=True

            if model_exists==False:  
                print('Model deleted')
            else:
                print("Model is being deleted...")
                time.sleep(2)

        print('Deleted Model: ' + model_version )
        print('Done...')
    
    except Exception as e:
        print(e)
    
def main():
    project_name='my-project' # The desired project
    model_version='1' # The version of the model that you want to delete.


    delete_model(project_name, model_version)

if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_delete_model.complete]