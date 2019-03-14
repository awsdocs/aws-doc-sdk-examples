# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[StartSpeechSynthesisTask.py demonstrates how to to synthesize a speech and store it as an MP3 audio file in an Amazon S3 bucket.]
# snippet-service:[polly]
# snippet-keyword:[Amazon Polly]
# snippet-keyword:[Python]
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

# snippet-start:[polly.python.StartSpeechSynthesisTask.complete]
import boto3

# Start synthesizing some text and save it as an MP3 audio file in an S3 file
polly_client = boto3.client('polly')
response = polly_client.start_speech_synthesis_task(
    VoiceId='Joanna',
    OutputS3BucketName='synth-books-buckets',
    OutputS3KeyPrefix='key',
    OutputFormat='mp3',
    Text='This is sample text to synthesize.')

# Output the task ID
taskId = response['SynthesisTask']['TaskId']
print(f'Task id is {taskId}')

# Retrieve and output the current status of the task
task_status = polly_client.get_speech_synthesis_task(TaskId = taskId)
print(f'Status: {task_status}')
# snippet-end:[polly.python.StartSpeechSynthesisTask.complete]
