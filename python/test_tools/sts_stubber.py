# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS STS unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import io
import json
from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


class StsStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS STS Control unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 STS client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_get_caller_identity(self, account_id, error_code=None):
        if not error_code:
            self.add_response(
                'get_caller_identity',
                expected_params={},
                service_response={'Account': account_id}
            )
        else:
            self.add_client_error(
                'get_caller_identity',
                expected_params={},
                service_error_code=error_code
            )
