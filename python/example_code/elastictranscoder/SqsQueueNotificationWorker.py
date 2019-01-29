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
# snippet-sourcedescription:[SqsQueueNotificationWorker.py demonstrates how to create a queue for handling notifications for an Elastic Transcoder job.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Elastic Transcoder]
# snippet-service:[elastictranscoder]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[]
# snippet-sourceauthor:[AWS]
import boto.sqs

import json
import multiprocessing

class SqsQueueNotificationWorker():
    """ This class runs an SQS polling worker in a second process. """
    
    def __init__(self, region,  queue_url, max_messages=5, visibility_timeout=15, wait_time_seconds=5):
        self.shutdown = multiprocessing.Value('i', 0)
        self.handlers = set()
        self.args = (self.shutdown, region, queue_url, self.handlers, max_messages, visibility_timeout, wait_time_seconds)
        self.process = None

    def start(self):
        if self.process:
            raise RuntimeError('SqsNotificationWorker already running.')
        self.process = multiprocessing.Process(target=poll_and_handle_messages, args=self.args)
        self.process.start()

    def stop(self):
        if not self.process or not self.process.is_alive():
            raise RuntimeError('SqsNotificationWorker already stopped.')
        self.shutdown.value = 1
        self.process.join()

    def add_handler(self, handler):
        if self.process != None:
            raise RuntimeError('Cannot modify handlers once worker has been started.')
        elif not isinstance(handler, JobStatusNotificationHandler):
            raise ValueError('Provided handler must be an instance of JobStatusNotificationHandler.')
        self.handlers.add(handler)

    def remove_handler(self, handler):
        if self.process != None:
            raise RuntimeError('Cannot modify handlers once worker has been started.')
        self.handlers.remove(handler)

class JobStatusNotificationHandler():
    """ Class that can be implemented to handle SQS messages.  Overriding the
    handle method will allow the user to define custom handling. """
    
    def handle(self, notification):
        print 'Notification: ', json.dumps(notification, sort_keys=True)

def poll_and_handle_messages(shutdown, region, queue_url, handlers, max_messages, visibility_timeout, wait_time_seconds):
    """ Method which polls SQS for messages, calls the specified handlers, and
    deletes the message from the queue.  Handlers should be relatively quick or
    risk having the messages timeout and delivered to a different SQS worker. """
    
    connection = boto.sqs.connect_to_region(region)
    queue = boto.sqs.queue.Queue(connection=connection, url=queue_url, message_class=boto.sqs.message.RawMessage)

    while(not shutdown.value):
        messages = queue.get_messages(num_messages=max_messages, visibility_timeout=visibility_timeout, wait_time_seconds=wait_time_seconds)
        # If no messages are placed in the queue for wait_time_seconds, messages
        # will be returned as None.
        if not messages:
            continue

        for message in messages:
            # Load the Elastic Transcoder notification out of the SQS message.
            notification = json.loads(str(json.loads(message.get_body())['Message']))
            for handler in handlers:
                # Call each specified handler with the notification.
                handler.handle(notification)

            # Delete the message from the queue.
            queue.delete_message(message)

