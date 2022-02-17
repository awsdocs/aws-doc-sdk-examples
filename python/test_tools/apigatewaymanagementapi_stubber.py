# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon API Gateway Management API unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class ApiGatewayManagementApiStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon API Gateway Management API unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 API Gateway Management API client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_post_to_connection(self, message, connection_id, error_code=None):
        expected_params = {'Data': message, 'ConnectionId': connection_id}
        response = {}
        self._stub_bifurcator(
            'post_to_connection', expected_params, response, error_code=error_code)