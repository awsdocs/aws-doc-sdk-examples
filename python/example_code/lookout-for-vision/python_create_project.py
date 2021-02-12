# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Lookout for Vision]
# snippet-keyword:[create_project]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2021-2-5]
# snippet-sourceauthor:[reesch (AWS)]

# snippet-start:[lookoutvision.python.lookoutvision_python_create_project.complete]

import boto3

# Creates a new Amazon Lookout for Vision project.
# project_name - the name for the new project.
def create_project(project_name):

    try: 
    
        client=boto3.client('lookoutvision')

        #Create a project
        print('Creating project:' + project_name)
        response=client.create_project(ProjectName=project_name)
        print('project ARN: ' + response['ProjectMetadata']['ProjectArn'])
        print('Done...')
    
    except Exception as e:
        print(e)
    
def main():
    project_name='my-project' # Change to the desired name.
    create_project(project_name)

if __name__ == "__main__":
    main()

# snippet-end:[lookoutvision.python.lookoutvision_python_create_project.complete]