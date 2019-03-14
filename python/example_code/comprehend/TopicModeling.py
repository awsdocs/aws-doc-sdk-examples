# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[TopicModeling.py demonstrates how to detect the topics in a document collection.]
# snippet-service:[comprehend]
# snippet-keyword:[Amazon Comprehend]
# snippet-keyword:[Python]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-03-13]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# snippet-start:[comprehend.python.TopicModeling.complete]
import boto3
import json

# Set these values before running the program
input_s3_url = "s3://INPUT_BUCKET/INPUT_PATH"
input_doc_format = "ONE_DOC_PER_FILE"
output_s3_url = "s3://OUTPUT_BUCKET/OUTPUT_PATH"
data_access_role_arn = "arn:aws:iam::ACCOUNT_ID:role/DATA_ACCESS_ROLE"
number_of_topics = 10

# Set up job configuration
input_data_config = {"S3Uri": input_s3_url, "InputFormat": input_doc_format}
output_data_config = {"S3Uri": output_s3_url}

# Begin a job to detect the topics in the document collection
comprehend = boto3.client('comprehend')
start_result = comprehend.start_topics_detection_job(
    NumberOfTopics=number_of_topics,
    InputDataConfig=input_data_config,
    OutputDataConfig=output_data_config,
    DataAccessRoleArn=data_access_role_arn)

# Output the results
print('Start Topic Detection Job: ' + json.dumps(start_result))
job_id = start_result['JobId']
print(f'job_id: {job_id}')

# Retrieve and output information about the job
describe_result = comprehend.describe_topics_detection_job(JobId=job_id)
print('Describe Job: ' + json.dumps(describe_result))

# List and output information about current jobs
list_result = comprehend.list_topics_detection_jobs()
print('list_topics_detection_jobs_result: ' + json.dumps(list_result))
# snippet-end:[comprehend.python.TopicModeling.complete]
