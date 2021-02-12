# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[stop_model]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_stop_model.complete]

import boto3
import time

# Stops a running Amazon Lookout for Vision Model
# project_Name - The name of the project that contains the version of the model that you want to stop hosting.
# model_version - the version of the nodel that you want to stop hosting.
def stop_model(project_name, model_version):

    try:

        client=boto3.client('lookoutvision')

        # Stop the model
        print('Stopping model version ' + model_version  + ' for project ' + project_name )
        response=client.stop_model(ProjectName=project_name,
            ModelVersion=model_version)
        print('Stopping...')
        status=response['Status']

        # Breaks when hosting has stopped.
        while True:            
            model_description=client.describe_model(ProjectName=project_name, ModelVersion=model_version)
            status=model_description['ModelDescription']['Status']

            if status == 'STOPPING_HOSTING':
                print('Host stopping in progress...')
                time.sleep(10)
                continue
            
            if status =='TRAINED':
                print ('Model is no longer hosted.')
                break

            print ('Failed. Unxexpected state for stopping model: ' + status)
            break
    except Exception as e:
        print(e)
        
    print('Done...')
    
def main():
    project='my-project' #Change to your project name.
    model_version='1' #Change to the version of your model.
    
    stop_model(project, model_version)

if __name__ == "__main__":
    main()   
  
# snippet-end:[lookoutvision.python.lookoutvision_python_stop_model.complete]