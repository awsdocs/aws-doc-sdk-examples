# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[start_model]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_start_model.complete]

import boto3
import time

# Starts the hosting of an Amazon Lookout for Vision model.
# project_name - the name of the project that contains the version of the model that you want to start hosting.
# model_version - the version of the model that you want to start hosting.
# min_inference_units - the number of inference units to use for hosting.
def start_model(project_name, model_version, min_inference_units):

    try:
    
        client=boto3.client('lookoutvision')


        # Start the model
        print('Starting model version ' + model_version  + ' for project ' + project_name )
        client.start_model(ProjectName=project_name,
            ModelVersion=model_version,
            MinInferenceUnits=min_inference_units)
        
        print('Starting hosting...')
        
        # Wait until either hosted or failed.
        while True:
            
            model_description=client.describe_model(ProjectName=project_name, ModelVersion=model_version)
            status=model_description['ModelDescription']['Status']

            if status == 'STARTING_HOSTING':
                print('Host starting in progress...')
                time.sleep(10)
                continue
            
            if status =='HOSTED':
                print ('Model is hosted and ready for use.')
                break
  
            if status =='HOSTING_FAILED':
                print ('Model hosting failed and the model can\'t be used.')
                break

            print ('Failed. Unexpected state for hosting: ' + status)
            break

    except Exception as e:
        print(e)
        
    print('Done...')
    
def main():
    project='my-project' # Change to your project name
    model_version='1' # Change to the version of your model
    min_inference_units=1 # Change to the number of inference units that you want to use for hosting.
    
    start_model(project, model_version, min_inference_units)
    
if __name__ == "__main__":
    main()    
  
# snippet-end:[lookoutvision.python.lookoutvision_python_start_model.complete]