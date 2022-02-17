# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon SQS unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


class SqsStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon SQS unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 SQS client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_queue(self, name, attributes, url, error_code=None):
        expected_params = {'QueueName': name, 'Attributes': attributes}
        response = {'QueueUrl': url}
        self._stub_bifurcator(
            'create_queue', expected_params, response, error_code=error_code)

    def stub_get_queue_attributes(self, url, arn, error_code=None):
        expected_params = {'AttributeNames': ['All'], 'QueueUrl': url}
        response = {'Attributes': {'QueueArn': arn}}
        self._stub_bifurcator(
            'get_queue_attributes', expected_params, response, error_code=error_code)

    def stub_list_dead_letter_source_queues(
            self, dl_url, source_urls, error_code=None):
        expected_params = {'QueueUrl': dl_url}
        response = {'queueUrls': source_urls}
        self._stub_bifurcator(
            'list_dead_letter_source_queues', expected_params, response,
            error_code=error_code)

    def stub_get_queue_url(self, name, url, error_code=None):
        expected_params = {'QueueName': name}
        response = {'QueueUrl': url}
        self._stub_bifurcator(
            'get_queue_url', expected_params, response, error_code=error_code)

    def stub_list_queues(self, urls, prefix=None, error_code=None):
        expected_params = {'QueueNamePrefix': prefix} if prefix else {}
        response = {'QueueUrls': urls}
        self._stub_bifurcator(
            'list_queues', expected_params, response, error_code=error_code)

    def stub_delete_queue(self, url, error_code=None):
        expected_params = {'QueueUrl': url}
        self._stub_bifurcator(
            'delete_queue', expected_params, error_code=error_code)

    def stub_send_message(self, url, body, attributes, message_id, error_code=None):
        expected_params = {
            'QueueUrl': url,
            'MessageBody': body,
            'MessageAttributes': attributes
        }
        response = {'MessageId': message_id}
        self._stub_bifurcator(
            'send_message', expected_params, response, error_code=error_code)

    def stub_send_message_batch(self, url, messages, error_code=None):
        expected_params = {
            'QueueUrl': url,
            'Entries': [{
                'Id': str(ind),
                'MessageBody': msg['body'],
                'MessageAttributes': msg['attributes']
            } for ind, msg in enumerate(messages)]
        }
        response = {
            'Successful': [{
                'Id': str(ind),
                'MessageId': f'msg-{ind}',
                'MD5OfMessageBody': 'Test-MD5-Body',
            } for ind in range(0, len(messages))],
            'Failed': []
        }
        self._stub_bifurcator(
            'send_message_batch', expected_params, response, error_code=error_code)

    def stub_receive_messages(
            self, url, messages, receive_count, error_code=None,
            message_attributes=['All'], omit_wait_time=False):
        expected_params = {'QueueUrl': url}
        if receive_count is not None:
            expected_params['MaxNumberOfMessages'] = receive_count
        if not omit_wait_time:
            expected_params['WaitTimeSeconds'] = ANY
        if message_attributes is not None:
            expected_params['MessageAttributeNames'] = ['All']
        if receive_count is None:
            receive_count = len(messages)
        response = {
            'Messages': [{
                'MessageId': f'msg-{ind}',
                'Body': msg['body'],
                'MD5OfBody': 'Test-MD5-Body',
                'ReceiptHandle': f'Receipt-{ind}'
            } for ind, msg in enumerate(messages) if ind < receive_count]
        }
        self._stub_bifurcator(
            'receive_message', expected_params, response, error_code=error_code)

    def stub_delete_message(
            self, url, message=None, receipt_handle=None, error_code=None):
        expected_params = {'QueueUrl': url}
        if message is not None:
            expected_params['ReceiptHandle'] = message.receipt_handle
        elif receipt_handle is not None:
            expected_params['ReceiptHandle'] = receipt_handle
        self._stub_bifurcator(
            'delete_message', expected_params, error_code=error_code)

    def stub_delete_message_batch(
            self, url, messages, successes, failures, error_code=None):
        expected_params = {
            'QueueUrl': url,
            'Entries': [{
                'Id': str(ind),
                'ReceiptHandle': msg.receipt_handle
            } for ind, msg in enumerate(messages)]
        }
        response = {
            'Successful': [{
                'Id': str(ind)
            } for ind in range(0, successes)],
            'Failed': [{
                'Id': str(ind),
                'Code': 'ReceiptHandleIsInvalid',
                'SenderFault': False
            } for ind in range(0, failures)]
        }
        self._stub_bifurcator(
            'delete_message_batch', expected_params, response, error_code=error_code)

    def stub_set_queue_attributes(self, queue_url, attributes, error_code=None):
        expected_params = {'QueueUrl': queue_url, 'Attributes': attributes}
        self._stub_bifurcator(
            'set_queue_attributes', expected_params, error_code=error_code)
