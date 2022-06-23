# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Pinpoint SMS and Voice unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from test_tools.example_stubber import ExampleStubber


class PinpointSmsVoiceStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Pinpoint SMS and Voice unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 Pinpoint SMS and Voice client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_send_voice_message(
            self, origination_number, caller_id, destination_number,
            language_code, voice_id, ssml_message, message_id, error_code=None):
        expected_params = {
            'DestinationPhoneNumber': destination_number,
            'OriginationPhoneNumber': origination_number,
            'CallerId': caller_id,
            'Content': {
                'SSMLMessage': {
                    'LanguageCode': language_code,
                    'VoiceId': voice_id,
                    'Text': ssml_message}}}
        response = {'MessageId': message_id}
        self._stub_bifurcator(
            'send_voice_message', expected_params, response, error_code=error_code)
