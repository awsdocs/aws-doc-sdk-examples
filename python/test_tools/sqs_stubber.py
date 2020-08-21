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

    def stub_create_queue(self, name, attributes, url):
        self.add_response(
            'create_queue',
            expected_params={
                'QueueName': name,
                'Attributes': attributes
            },
            service_response={'QueueUrl': url}
        )

    def stub_get_queue_attributes(self, url, arn):
        self.add_response(
            'get_queue_attributes',
            expected_params={
                'AttributeNames': ['All'],
                'QueueUrl': url
            },
            service_response={'Attributes': {'QueueArn': arn}}
        )

    def stub_list_dead_letter_source_queues(self, dl_url, source_urls):
        self.add_response(
            'list_dead_letter_source_queues',
            expected_params={'QueueUrl': dl_url},
            service_response={'queueUrls': source_urls}
        )

    def stub_get_queue_url(self, name, url, error_code=None):
        expected_params = {'QueueName': name}

        if not error_code:
            self.add_response(
                'get_queue_url',
                expected_params=expected_params,
                service_response={'QueueUrl': url}
            )
        else:
            self.add_client_error(
                'get_queue_url',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_list_queues(self, urls, prefix=None):
        expected_params = {'QueueNamePrefix': prefix} if prefix else {}

        self.add_response(
            'list_queues',
            expected_params=expected_params,
            service_response={'QueueUrls': urls}
        )

    def stub_delete_queue(self, url):
        self.add_response(
            'delete_queue',
            expected_params={'QueueUrl': url},
            service_response={}
        )

    def stub_send_message(self, url, body, attributes, message_id, error_code=None):
        expected_params = {
            'QueueUrl': url,
            'MessageBody': body,
            'MessageAttributes': attributes
        }

        if not error_code:
            self.add_response(
                'send_message',
                expected_params=expected_params,
                service_response={'MessageId': message_id}
            )
        else:
            self.add_client_error(
                'send_message',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_send_message_batch(self, url, messages, error_code=None):
        expected_params = {
            'QueueUrl': url,
            'Entries': [{
                'Id': str(ind),
                'MessageBody': msg['body'],
                'MessageAttributes': msg['attributes']
            } for ind, msg in enumerate(messages)]
        }

        if not error_code:
            self.add_response(
                'send_message_batch',
                expected_params=expected_params,
                service_response={
                    'Successful': [{
                        'Id': str(ind),
                        'MessageId': f'msg-{ind}',
                        'MD5OfMessageBody': 'Test-MD5-Body',
                    } for ind in range(0, len(messages))],
                    'Failed': []
                }
            )
        else:
            self.add_client_error(
                'send_message_batch',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_receive_messages(self, url, messages, receive_count, error_code=None):
        expected_params = {
            'QueueUrl': url,
            'MessageAttributeNames': ['All'],
            'MaxNumberOfMessages': receive_count,
            'WaitTimeSeconds': ANY
        }

        if not error_code:
            self.add_response(
                'receive_message',
                expected_params=expected_params,
                service_response={
                    'Messages': [{
                        'MessageId': f'msg-{ind}',
                        'Body': msg['body'],
                        'MD5OfBody': 'Test-MD5-Body',
                        'ReceiptHandle': f'Receipt-{ind}'
                    } for ind, msg in enumerate(messages) if ind < receive_count]
                }
            )
        else:
            self.add_client_error(
                'receive_message',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_delete_message(self, url, message, error_code=None):
        expected_params = {
            'QueueUrl': url,
            'ReceiptHandle': message.receipt_handle
        }

        if not error_code:
            self.add_response(
                'delete_message',
                expected_params=expected_params,
                service_response={}
            )
        else:
            self.add_client_error(
                'delete_message',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_delete_message_batch(self, url, messages, successes, failures):
        self.add_response(
            'delete_message_batch',
            expected_params={
                'QueueUrl': url,
                'Entries': [{
                    'Id': str(ind),
                    'ReceiptHandle': msg.receipt_handle
                } for ind, msg in enumerate(messages)]
            },
            service_response={
                'Successful': [{
                    'Id': str(ind)
                } for ind in range(0, successes)],
                'Failed': [{
                    'Id': str(ind),
                    'Code': 'ReceiptHandleIsInvalid',
                    'SenderFault': False
                } for ind in range(0, failures)]
            }
        )
