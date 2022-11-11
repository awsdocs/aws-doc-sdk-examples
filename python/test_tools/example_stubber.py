# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
A base class for stubbers that are used by the Python code example unit tests.
"""

import contextlib
from botocore.stub import Stubber


class ExampleStubber(Stubber):
    """
    A base class that wraps the botocore Stubber and either uses the Stubber to
    intercept requests during tests or pass calls through to AWS.

    All stubbers used in Python unit tests must inherit from this base class.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 service client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        self.use_stubs = use_stubs
        self.region_name = client.meta.region_name
        if self.use_stubs:
            super().__init__(client)
        else:
            self.client = client

    def add_response(self, method, service_response, expected_params=None):
        """When using stubs, add a stubbed response."""
        if self.use_stubs:
            super().add_response(method, service_response, expected_params)

    def add_client_error(self, method, service_error_code='',
                         service_message='', http_status_code=400,
                         service_error_meta=None, expected_params=None,
                         response_meta=None, modeled_fields=None):
        """When using stubs, add a stubbed error response."""
        if self.use_stubs:
            super().add_client_error(method, service_error_code, service_message,
                                     http_status_code, service_error_meta,
                                     expected_params, response_meta)

    def assert_no_pending_responses(self):
        """When using stubs, verify no more responses are waiting in the queue."""
        if self.use_stubs:
            super().assert_no_pending_responses()

    def _stub_bifurcator(
            self, method, expected_params=None, response=None, error_code=None, error_message=''):
        if expected_params is None:
            expected_params = {}
        if response is None:
            response = {}
        if error_code is None:
            self.add_response(
                method, expected_params=expected_params, service_response=response)
        else:
            self.add_client_error(
                method, expected_params=expected_params, service_error_code=error_code, service_message=error_message)
