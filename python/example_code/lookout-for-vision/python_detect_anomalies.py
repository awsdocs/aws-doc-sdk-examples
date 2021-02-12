# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[detect_anomalies]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_detect_anomalies.complete]

import boto3

# Detects anomalies in an image (jpg/png) by using your Amazon Lookout for Vision model.
# project_name - the name of the project that contains the model that you want to use.
# model_version - the version of the model that you want to use.
# photo - the path and name of the image in which you want to detect anomalies.  
def detect_anomalies(project_name,model_version,photo):
     
    try:

        client=boto3.client('lookoutvision')

        #Call detect_anomalies 
        
        with open(photo, 'rb') as image:
            response = client.detect_anomalies(ProjectName=project_name, 
            ContentType='image/jpeg', # or image/png for png format input image.
            Body=image.read(),
            ModelVersion=model_version)
        print ('Anomalous?: ' + str(response['DetectAnomalyResult']['IsAnomalous']))
        print ('Confidence: ' + str(response['DetectAnomalyResult']['Confidence']))

    except Exception as e:
        print(e)

def main():

    project_name='my-project' #Change to your project name.
    photo='image.jpg' # Chang to the path and name of the image that you want to use. Can also specify png format image. 
    model_version='1' #Change to the version of your model.
  
    anomalous=detect_anomalies(project_name,model_version,photo)
    

if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_detect_anomalies.complete]
