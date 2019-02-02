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

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[StartSpeechSynthesisTask.py demonstrates how to to synthesize a long speech (up to 100,000 billed characters) and store it directly in an Amazon S3 bucket. ]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Polly]
# snippet-keyword:[StartSpeechSynthesisTask]
# snippet-keyword:[speech synthesis]
# snippet-service:[polly]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[ (AWS)]
# snippet-start:[polly.python.StartSpeechSynthesisTask.complete]

import boto3
import time

polly_client = boto3.Session(
                aws_access_key_id=’’,                  
    aws_secret_access_key=’’,
    region_name='eu-west-2').client('polly’)

response = polly_client.start_speech_synthesis_task(VoiceId='Joanna',
                OutputS3BucketName='synth-books-buckets',
                OutputS3KeyPrefix='key',
                OutputFormat='mp3', 
                Text = 'This is a sample text to be synthesized.')

taskId = response['SynthesisTask']['TaskId']

print "Task id is {} ".format(taskId)

task_status = polly_client.get_speech_synthesis_task(TaskId = taskId)

print task_status

              
# snippet-end:[polly.python.StartSpeechSynthesisTask.complete]
  