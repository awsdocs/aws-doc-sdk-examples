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
# snippet-sourcedescription:[JobStatusNotificationSample.py demonstrates how to create an Elastic Transcoder job and wait until it finishes.]
# snippet-service:[elastictranscoder]
# snippet-keyword:[Amazon Elastic Transcoder]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-sourcedate:[2019-02-04]
# snippet-sourceauthor:[AWS]
# snippet-start:[elastictranscoder.python.create_job_status_notification.import]

import time

import boto3
from botocore.exceptions import ClientError
from SqsQueueNotificationWorker import SqsWorker, JobStatus


# Job configuration settings. Set these values before running the script.
pipeline_id = 'PIPELINE_ID'          # Set to the ID of an existing Elastic Transcoder pipeline
input_file = 'FILE_TO_TRANSCODE'     # Set to the name of an existing file in the S3 input bucket
output_file = 'TRANSCODED_FILE'      # Set to the desired name of the transcoded output file
sqs_queue_name = 'ets-sample-queue'  # SQS queue subscribed to SNS ets-sample-topic notifications

# Other job configuration settings. Optionally change as desired.
preset_id = '1351620000001-000020'  # Elastic Transcoder preset ID (480p 16:9 MP4)
output_file_prefix = 'elastic-transcoder-samples/output/'    # Prefix for all output files

# Method used to wait for job to complete
monitor_sqs_messages = True     # Set to False to use Waiter object


class JobMonitor:
    """Monitors the SQS notifications received for an Elastic Transcoder job"""

    def __init__(self, job_id, sqs_queue_name):
        """Initialize new JobMonitor

        :param job_id: string; Elastic Transcoder job ID to monitor
        :param sqs_queue_name: string; Name of SQS queue subscribed to receive
        notifications
        """

        # Each JobMonitor has its own SqsWorker to monitor notifications
        self._sqs_worker = SqsWorker(job_id, sqs_queue_name)
        self._job_id = job_id

    def start(self):
        """Have the SqsWorker start monitoring notifications"""
        self._sqs_worker.start()

    def stop(self):
        """Instruct the SqsWorker to stop monitoring notifications

        If this occurs before the job has finished, the monitoring of
        notifications is aborted, but the Elastic Transcoder job itself
        continues.
        """
        self._sqs_worker.stop()

    def finished(self):
        return self._sqs_worker.finished()

    def status(self):
        return self._sqs_worker.job_status()

    def wait_for_completion(self):
        """Block until the job finishes"""
        while not self.finished():
            time.sleep(5)

        # Stop the SqsWorker
        self.stop()

    def __repr__(self):
        return f'JobMonitor(Job ID: {self._job_id}, Status: {self.status().name})'


def create_elastic_transcoder_job():
    """Create an Elastic Transcoder job

    All Elastic Transcoder set up operations must be completed before calling
    this function, such as defining the pipeline and specifying the S3 input
    and output buckets, etc.

    :return Dictionary containing information about the job
            JobComplete Waiter object
            None if job could not be created
    """

    etc_client = boto3.client('elastictranscoder')
    try:
        response = etc_client.create_job(PipelineId=pipeline_id,
                                         Input={'Key': input_file},
                                         Outputs=[{'Key': output_file, 'PresetId': preset_id}],
                                         OutputKeyPrefix=output_file_prefix,)
    except ClientError as e:
        print(f'ERROR: {e}')
        return None
    else:
        return response['Job'], etc_client.get_waiter('job_complete')


def main():
    # Create a job in Elastic Transcoder
    job_info, job_waiter = create_elastic_transcoder_job()
    if job_info is None:
        exit(1)

    # Wait for job to complete
    job_id = job_info['Id']
    print(f'Waiting for job {job_id} to complete...')

    # Two techniques for waiting are demonstrated
    if monitor_sqs_messages:
        # Method 1: Monitor SQS notifications until the job is finished
        job_monitor = JobMonitor(job_id, sqs_queue_name)
        job_monitor.start()

        # While waiting for the job to finish, we can do other tasks, including
        # starting other jobs. For this demo, we block until the job finishes.
        job_monitor.wait_for_completion()

        # Output the result
        status = job_monitor.status()
        if status == JobStatus.SUCCESS:
            print('Job completed successfully')
        elif status == JobStatus.ERROR:
            print('Job terminated with error')
        else:
            # An unexpected status
            print(f'Job status: {status.name}')
    else:
        # Method 2: Have the job_waiter wait until the job finishes
        # Polls every five seconds. The final status of the job is
        # not available.
        job_waiter.wait(Id=job_id, WaiterConfig={'Delay': 5})
        print('Job completed')


if __name__ == '__main__':
    main()
# snippet-end:[elastictranscoder.python.create_job_status_notification.import]
