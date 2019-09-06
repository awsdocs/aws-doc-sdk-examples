# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[SqsQueueNotificationWorker.py demonstrates how to create a separate process to handle notification messages for an Elastic Transcoder job.]
# snippet-service:[elastictranscoder]
# snippet-keyword:[Amazon Elastic Transcoder]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-02-04]
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

# snippet-start:[elastictranscoder.python.create_sqs_notification_queue.import]

from ctypes import c_bool
from enum import Enum, auto
import json
import multiprocessing
import pprint

import boto3


class JobStatus(Enum):
    """Status of an Elastic Transcoder job"""
    SUCCESS = auto()        # Elastic Transcoder job finished successfully
    ERROR = auto()          # Elastic Transcoder job failed
    RUNNING = auto()        # Job is running
    UNKNOWN = auto()        # SqsWorker process was aborted


class ProcessStatus(Enum):
    """Status of an SqsWorker process"""
    READY = auto()          # Initialized, but not yet started
    IN_PROGRESS = auto()    # Started and monitoring notifications
    ABORTED = auto()        # Aborted before Elastic Transcoder job finished
    FINISHED = auto()       # Finished after handling all job notifications


class SqsWorker:
    """Monitors SQS notifications for an Elastic Transcoder job

    Each Elastic Transcoder job/JobMonitor must have its own SqsWorker
    object. The SqsWorker handles messages for the job. Messages for other
    jobs are ignored.

    The SysWorker performs its task in a separate process. The JobMonitor
    starts the process by calling SysWorker.start().

    While the SysWorker process is handling job notifications, the JobMonitor
    parent process can perform other tasks, including starting new jobs with
    new JobMonitor and SqsWorker objects.

    When the Transcoder job is finished, a SysWorker flag is set. The
    JobMonitor parent process must periodically retrieve the current setting
    of the flag by calling SysWorker.finished().

    When the Transcoder job has finished, the JobMonitor parent process must
    terminate the SysWorker process by calling SysWorker.stop().

    The final result of the completed job can be retrieved by calling
    SysWorker.job_status().
    """
    
    def __init__(self, job_id, sqs_queue_name):
        """Initialize an SqsWorker object to process SQS notification
        messages for a particular Elastic Transcoder job.

        :param job_id: string; Elastic Transcoder job ID to monitor
        :param sqs_queue_name: string; Name of SQS queue subscribed to receive
        notifications for job_id
        """

        self._job_id = job_id
        self._finished = multiprocessing.Value(c_bool, False)
        self._job_status = multiprocessing.Value('i', JobStatus.RUNNING.value)
        self._process_status = multiprocessing.Value('i', ProcessStatus.READY.value)
        self._args = (job_id, sqs_queue_name,
                      self._finished, self._job_status, self._process_status)
        self._process = None

    def start(self):
        """Start a new SqsWorker process to handle the job's notifications"""

        if self._process is not None:
            raise RuntimeError('SqsQueueNotificationWorker already running.')
        self._process = multiprocessing.Process(target=poll_and_handle_messages,
                                                args=self._args)
        self._process.start()
        self._process_status.value = ProcessStatus.IN_PROGRESS.value

    def stop(self):
        """Stop the SqsWorker process"""

        if self._process is None:
            raise RuntimeError('SqsQueueNotificationWorker already stopped.')
        if self._process.is_alive():
            # Aborting the process before the job is finished
            self._process_status.value = ProcessStatus.ABORTED.value
            self._job_status.value = JobStatus.UNKNOWN.value
        self._finished.value = True
        self._process.join()

    def finished(self):
        """Finished = Job completed successfully or job terminated with error
        or monitoring of notifications was aborted before receiving a
        job-completed notification
        """
        return self._finished.value

    def job_status(self):
        return JobStatus(self._job_status.value)

    def process_status(self):
        return ProcessStatus(self._process_status.value)

    def __repr__(self):
        return f'SqsWorker(Job ID: {self._job_id}, ' \
            f'Status: {ProcessStatus(self._process_status.value).name})'


def poll_and_handle_messages(job_id, sqs_queue_name,
                             finished, job_status, process_status):
    """Process SQS notifications for a particular Elastic Transcoder job

    This method runs as a separate process.

    :param job_id: string; Elastic Transcoder job ID to monitor
    :param sqs_queue_name: string; Name of SQS queue
    :param finished: boolean; Shared memory flag. While this method is running,
    the flag might be set externally if the JobMonitor parent process instructs
    us to stop before we receive notification that the job has finished.
    Otherwise, this method sets the finished flag when the Transcoder job
    finishes.
    :param job_status: int/JobStatus enum; Shared memory variable containing
    the Transcoder job status
    :param process_status: int/ProcessStatus enum; Shared memory variable
    containing the SysWorker process status
    """

    sqs_client = boto3.client('sqs')
    response = sqs_client.get_queue_url(QueueName=sqs_queue_name)
    sqs_queue_url = response['QueueUrl']

    # Loop until the job is finished or the JobMonitor parent process instructs
    # us to stop
    while not finished.value:
        response = sqs_client.receive_message(QueueUrl=sqs_queue_url,
                                              MaxNumberOfMessages=5,
                                              WaitTimeSeconds=5,)
        # Any messages received?
        if 'Messages' not in response:
            continue

        # Process each message
        for message in response['Messages']:
            # Extract the message part of the body
            notification = json.loads(json.loads(message['Body'])['Message'])

            # Show the notification information
            print('Notification:')
            pprint.pprint(notification)

            # Is the message for this job?
            if notification['jobId'] == job_id:
                # Delete the message from the queue
                sqs_client.delete_message(QueueUrl=sqs_queue_url,
                                          ReceiptHandle=message['ReceiptHandle'])

                # Did the job finish, either successfully or with error?
                if notification['state'] == 'COMPLETED':
                    # Set shared memory flags
                    job_status.value = JobStatus.SUCCESS.value
                    process_status.value = ProcessStatus.FINISHED.value
                    finished.value = True
                elif notification['state'] == 'ERROR':
                    job_status.value = JobStatus.ERROR.value
                    process_status.value = ProcessStatus.FINISHED.value
                    finished.value = True
# snippet-end:[elastictranscoder.python.create_sqs_notification_queue.import]
