# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Pinpoint Email unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from test_tools.example_stubber import ExampleStubber


class PinpointEmailStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Pinpoint Email unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 Pinpoint Email client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_send_email(
            self, sender, to_addresses, cc_addresses, char_set, subject, html_message,
            text_message, message_id, error_code=None):
        expected_params = {
            'FromEmailAddress': sender,
            'Destination': {'ToAddresses': to_addresses, 'CcAddresses': cc_addresses},
            'Content': {
                'Simple': {
                    'Subject': {'Charset': char_set, 'Data': subject},
                    'Body': {
                        'Html': {'Charset': char_set, 'Data': html_message},
                        'Text': {'Charset': char_set, 'Data': text_message}}}}}
        response = {'MessageId': message_id}
        self._stub_bifurcator(
            'send_email', expected_params, response, error_code=error_code)
