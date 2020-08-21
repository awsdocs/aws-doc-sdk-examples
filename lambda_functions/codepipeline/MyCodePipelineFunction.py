# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[MyCodePipelineFunction.py demonstrates how to use an AWS Lambda function to create or update a stack based on a supplied AWS CloudFormation template and Lambda execution role.]
# snippet-service:[codepipeline]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS CodePipeline]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Invoke]
# snippet-sourcetype:[full-example]
# snippet-sourceauthor:[AWS]
# snippet-sourcedate:[2016-03-07]
# snippet-start:[codepipeline.python.MyCodePipelineFunction.complete]

from __future__ import print_function
from boto3.session import Session

import json
import urllib
import boto3
import zipfile
import tempfile
import botocore
import traceback

print('Loading function')

cf = boto3.client('cloudformation')
code_pipeline = boto3.client('codepipeline')

def find_artifact(artifacts, name):
    """Finds the artifact 'name' among the 'artifacts'
    
    Args:
        artifacts: The list of artifacts available to the function
        name: The artifact we wish to use
    Returns:
        The artifact dictionary found
    Raises:
        Exception: If no matching artifact is found
    
    """
    for artifact in artifacts:
        if artifact['name'] == name:
            return artifact
            
    raise Exception('Input artifact named "{0}" not found in event'.format(name))

def get_template(s3, artifact, file_in_zip):
    """Gets the template artifact
    
    Downloads the artifact from the S3 artifact store to a temporary file
    then extracts the zip and returns the file containing the CloudFormation
    template.
    
    Args:
        artifact: The artifact to download
        file_in_zip: The path to the file within the zip containing the template
        
    Returns:
        The CloudFormation template as a string
        
    Raises:
        Exception: Any exception thrown while downloading the artifact or unzipping it
    
    """
    tmp_file = tempfile.NamedTemporaryFile()
    bucket = artifact['location']['s3Location']['bucketName']
    key = artifact['location']['s3Location']['objectKey']
    
    with tempfile.NamedTemporaryFile() as tmp_file:
        s3.download_file(bucket, key, tmp_file.name)
        with zipfile.ZipFile(tmp_file.name, 'r') as zip:
            return zip.read(file_in_zip)   
   
def update_stack(stack, template):
    """Start a CloudFormation stack update
    
    Args:
        stack: The stack to update
        template: The template to apply
        
    Returns:
        True if an update was started, false if there were no changes
        to the template since the last update.
        
    Raises:
        Exception: Any exception besides "No updates are to be performed."
    
    """
    try:
        cf.update_stack(StackName=stack, TemplateBody=template)
        return True
        
    except botocore.exceptions.ClientError as e:
        if e.response['Error']['Message'] == 'No updates are to be performed.':
            return False
        else:
            raise Exception('Error updating CloudFormation stack "{0}"'.format(stack), e)

def stack_exists(stack):
    """Check if a stack exists or not
    
    Args:
        stack: The stack to check
        
    Returns:
        True or False depending on whether the stack exists
        
    Raises:
        Any exceptions raised .describe_stacks() besides that
        the stack doesn't exist.
        
    """
    try:
        cf.describe_stacks(StackName=stack)
        return True
    except botocore.exceptions.ClientError as e:
        if "does not exist" in e.response['Error']['Message']:
            return False
        else:
            raise e

def create_stack(stack, template):
    """Starts a new CloudFormation stack creation
    
    Args:
        stack: The stack to be created
        template: The template for the stack to be created with
        
    Throws:
        Exception: Any exception thrown by .create_stack()
    """
    cf.create_stack(StackName=stack, TemplateBody=template)
 
def get_stack_status(stack):
    """Get the status of an existing CloudFormation stack
    
    Args:
        stack: The name of the stack to check
        
    Returns:
        The CloudFormation status string of the stack such as CREATE_COMPLETE
        
    Raises:
        Exception: Any exception thrown by .describe_stacks()
        
    """
    stack_description = cf.describe_stacks(StackName=stack)
    return stack_description['Stacks'][0]['StackStatus']
  
def put_job_success(job, message):
    """Notify CodePipeline of a successful job
    
    Args:
        job: The CodePipeline job ID
        message: A message to be logged relating to the job status
        
    Raises:
        Exception: Any exception thrown by .put_job_success_result()
    
    """
    print('Putting job success')
    print(message)
    code_pipeline.put_job_success_result(jobId=job)
  
def put_job_failure(job, message):
    """Notify CodePipeline of a failed job
    
    Args:
        job: The CodePipeline job ID
        message: A message to be logged relating to the job status
        
    Raises:
        Exception: Any exception thrown by .put_job_failure_result()
    
    """
    print('Putting job failure')
    print(message)
    code_pipeline.put_job_failure_result(jobId=job, failureDetails={'message': message, 'type': 'JobFailed'})
 
def continue_job_later(job, message):
    """Notify CodePipeline of a continuing job
    
    This will cause CodePipeline to invoke the function again with the
    supplied continuation token.
    
    Args:
        job: The JobID
        message: A message to be logged relating to the job status
        continuation_token: The continuation token
        
    Raises:
        Exception: Any exception thrown by .put_job_success_result()
    
    """
    
    # Use the continuation token to keep track of any job execution state
    # This data will be available when a new job is scheduled to continue the current execution
    continuation_token = json.dumps({'previous_job_id': job})
    
    print('Putting job continuation')
    print(message)
    code_pipeline.put_job_success_result(jobId=job, continuationToken=continuation_token)

def start_update_or_create(job_id, stack, template):
    """Starts the stack update or create process
    
    If the stack exists then update, otherwise create.
    
    Args:
        job_id: The ID of the CodePipeline job
        stack: The stack to create or update
        template: The template to create/update the stack with
    
    """
    if stack_exists(stack):
        status = get_stack_status(stack)
        if status not in ['CREATE_COMPLETE', 'ROLLBACK_COMPLETE', 'UPDATE_COMPLETE']:
            # If the CloudFormation stack is not in a state where
            # it can be updated again then fail the job right away.
            put_job_failure(job_id, 'Stack cannot be updated when status is: ' + status)
            return
        
        were_updates = update_stack(stack, template)
        
        if were_updates:
            # If there were updates then continue the job so it can monitor
            # the progress of the update.
            continue_job_later(job_id, 'Stack update started')  
            
        else:
            # If there were no updates then succeed the job immediately 
            put_job_success(job_id, 'There were no stack updates')    
    else:
        # If the stack doesn't already exist then create it instead
        # of updating it.
        create_stack(stack, template)
        # Continue the job so the pipeline will wait for the CloudFormation
        # stack to be created.
        continue_job_later(job_id, 'Stack create started') 

def check_stack_update_status(job_id, stack):
    """Monitor an already-running CloudFormation update/create
    
    Succeeds, fails or continues the job depending on the stack status.
    
    Args:
        job_id: The CodePipeline job ID
        stack: The stack to monitor
    
    """
    status = get_stack_status(stack)
    if status in ['UPDATE_COMPLETE', 'CREATE_COMPLETE']:
        # If the update/create finished successfully then
        # succeed the job and don't continue.
        put_job_success(job_id, 'Stack update complete')
        
    elif status in ['UPDATE_IN_PROGRESS', 'UPDATE_ROLLBACK_IN_PROGRESS', 
    'UPDATE_ROLLBACK_COMPLETE_CLEANUP_IN_PROGRESS', 'CREATE_IN_PROGRESS', 
    'ROLLBACK_IN_PROGRESS']:
        # If the job isn't finished yet then continue it
        continue_job_later(job_id, 'Stack update still in progress') 
       
    else:
        # If the Stack is a state which isn't "in progress" or "complete"
        # then the stack update/create has failed so end the job with
        # a failed result.
        put_job_failure(job_id, 'Update failed: ' + status)

def get_user_params(job_data):
    """Decodes the JSON user parameters and validates the required properties.
    
    Args:
        job_data: The job data structure containing the UserParameters string which should be a valid JSON structure
        
    Returns:
        The JSON parameters decoded as a dictionary.
        
    Raises:
        Exception: The JSON can't be decoded or a property is missing.
        
    """
    try:
        # Get the user parameters which contain the stack, artifact and file settings
        user_parameters = job_data['actionConfiguration']['configuration']['UserParameters']
        decoded_parameters = json.loads(user_parameters)
            
    except Exception as e:
        # We're expecting the user parameters to be encoded as JSON
        # so we can pass multiple values. If the JSON can't be decoded
        # then fail the job with a helpful message.
        raise Exception('UserParameters could not be decoded as JSON')
    
    if 'stack' not in decoded_parameters:
        # Validate that the stack is provided, otherwise fail the job
        # with a helpful message.
        raise Exception('Your UserParameters JSON must include the stack name')
    
    if 'artifact' not in decoded_parameters:
        # Validate that the artifact name is provided, otherwise fail the job
        # with a helpful message.
        raise Exception('Your UserParameters JSON must include the artifact name')
    
    if 'file' not in decoded_parameters:
        # Validate that the template file is provided, otherwise fail the job
        # with a helpful message.
        raise Exception('Your UserParameters JSON must include the template file name')
    
    return decoded_parameters
    
def setup_s3_client(job_data):
    """Creates an S3 client
    
    Uses the credentials passed in the event by CodePipeline. These
    credentials can be used to access the artifact bucket.
    
    Args:
        job_data: The job data structure
        
    Returns:
        An S3 client with the appropriate credentials
        
    """
    key_id = job_data['artifactCredentials']['accessKeyId']
    key_secret = job_data['artifactCredentials']['secretAccessKey']
    session_token = job_data['artifactCredentials']['sessionToken']
    
    session = Session(aws_access_key_id=key_id,
        aws_secret_access_key=key_secret,
        aws_session_token=session_token)
    return session.client('s3', config=botocore.client.Config(signature_version='s3v4'))

def lambda_handler(event, context):
    """The Lambda function handler
    
    If a continuing job then checks the CloudFormation stack status
    and updates the job accordingly.
    
    If a new job then kick of an update or creation of the target
    CloudFormation stack.
    
    Args:
        event: The event passed by Lambda
        context: The context passed by Lambda
        
    """
    try:
        # Extract the Job ID
        job_id = event['CodePipeline.job']['id']
        
        # Extract the Job Data 
        job_data = event['CodePipeline.job']['data']
        
        # Extract the params
        params = get_user_params(job_data)
        
        # Get the list of artifacts passed to the function
        artifacts = job_data['inputArtifacts']
        
        stack = params['stack']
        artifact = params['artifact']
        template_file = params['file']
        
        if 'continuationToken' in job_data:
            # If we're continuing then the create/update has already been triggered
            # we just need to check if it has finished.
            check_stack_update_status(job_id, stack)
        else:
            # Get the artifact details
            artifact_data = find_artifact(artifacts, artifact)
            # Get S3 client to access artifact with
            s3 = setup_s3_client(job_data)
            # Get the JSON template file out of the artifact
            template = get_template(s3, artifact_data, template_file)
            # Kick off a stack update or create
            start_update_or_create(job_id, stack, template)  

    except Exception as e:
        # If any other exceptions which we didn't expect are raised
        # then fail the job and log the exception message.
        print('Function failed due to exception.') 
        print(e)
        traceback.print_exc()
        put_job_failure(job_id, 'Function exception: ' + str(e))
      
    print('Function complete.')   
    return "Complete."


# snippet-end:[codepipeline.python.MyCodePipelineFunction.complete]
