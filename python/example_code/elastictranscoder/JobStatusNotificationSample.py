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
# snippet-sourcedescription:[JobStatusNotificationSample.py demonstrates how to create a notification handler for an Elastic Transcoder job.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Elastic Transcoder]
# snippet-service:[elastictranscoder]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[]
# snippet-sourceauthor:[AWS]
import boto.elastictranscoder
from SqsQueueNotificationWorker import SqsQueueNotificationWorker, JobStatusNotificationHandler

import hashlib
import json
import multiprocessing
import time

# This is the ID of the Elastic Transcoder pipeline that was created when
# setting up your AWS environment:
# https://w.amazon.com/index.php/User:Ramsdenj/Samples/Environment_Setup/Create_Elastic_Transcoder_Pipeline#Create_the_Pipeline
pipeline_id = 'Enter your pipeline id here.'

# This is the URL of the SQS queue that was created when setting up your
# AWS environment.
# https://w.amazon.com/index.php/User:Ramsdenj/Samples/Environment_Setup/Create_SQS_Queue#Create_an_SQS_Queue
sqs_queue_url = 'Enter your queue url here.'

# This is the name of the input key that you would like to transcode.
input_key = 'Enter your input key here.'

# This will generate a 480p 16:9 mp4 output.
preset_id = '1351620000001-000020'

# All outputs will have this prefix prepended to their output key.
output_key_prefix = 'elastic-transcoder-samples/output/'

# Region where you setup your AWS resources.
region = 'us-east-1'


class WaitForCompletionHandler(JobStatusNotificationHandler):
    """ Class which extends JobStatusNotificationHandler and will signal when a specific
    job is complete.  Note that this implementation will not scale past a single
    machine because the JobStatusNotificationHandler is looking for a specific job ID.
    If there are multiple machines polling SQS for notifications, there is no
    guarantee that a particular machine will receive a particular notification.

    Attributes:
        terminal_states     Set of states which indicate a job is done processing.
    """
    
    terminal_states = ('COMPLETED', 'ERROR')
    def __init__(self, job_id):
        self.job_id = job_id
        self.complete = multiprocessing.Value('i', 0)

    def handle(self, notification):
        print 'Notification: ', json.dumps(notification, sort_keys=True)
        if self.job_id == notification['jobId'] and notification['state'] in WaitForCompletionHandler.terminal_states:
            self.complete.value = 1

    def wait_for_completion(self):
        while not self.complete.value:
            time.sleep(1)

def create_elastic_transcoder_job():
    # Setup the job input using the provided input key.
    job_input = { 'Key' : input_key }

    # Setup the job output using the provided input key to generate an output key.
    job_output = {
        'Key' : hashlib.sha256(input_key.encode('utf-8')).hexdigest(),
        'PresetId' : preset_id,
    }

    # Create a job on the specified pipeline and return the job ID.
    create_job_request = {
        'pipeline_id' : pipeline_id,
        'input_name' : job_input,
        'output_key_prefix' : output_key_prefix,
        'outputs' : [ job_output ]
    }
    transcoder_connection = boto.elastictranscoder.connect_to_region(region)
    return transcoder_connection.create_job(**create_job_request)['Job']['Id']


if __name__ == '__main__':

    # Create a job in Elasic Transcoder.
    job_id = create_elastic_transcoder_job()

    # Setup our notification worker.
    completion_handler = WaitForCompletionHandler(job_id)
    queue_worker = SqsQueueNotificationWorker(region, sqs_queue_url)
    queue_worker.add_handler(completion_handler)
    queue_worker.start()

    print 'Waiting for job to complete: ', job_id
    completion_handler.wait_for_completion()
    queue_worker.stop()

