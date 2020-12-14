# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Route 53 unit tests.
"""

import datetime
from test_tools.example_stubber import ExampleStubber


class Route53Stubber(ExampleStubber):
    """
    A class that implements stub functions used by Route 53 unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Route 53 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_list_hosted_zones(self, zones, max_items, error_code=None):
        expected_params = {'MaxItems': max_items}
        response = {
            'HostedZones': zones,
            'Marker': 'test-marker',
            'IsTruncated': False,
            'MaxItems': max_items
        }
        self._stub_bifurcator(
            'list_hosted_zones', expected_params, response, error_code=error_code)

    def stub_list_resource_record_sets(
            self, zone_id, max_items, record_sets, error_code=None):
        expected_params = {'HostedZoneId': zone_id, 'MaxItems': max_items}
        response = {
            'ResourceRecordSets': record_sets,
            'IsTruncated': False,
            'MaxItems': max_items}
        self._stub_bifurcator(
            'list_resource_record_sets', expected_params, response,
            error_code=error_code)

    def stub_change_resource_record_sets(self, zone_id, changes, error_code=None):
        expected_params = {
            'HostedZoneId': zone_id, 'ChangeBatch': {'Changes': changes}}
        response = {'ChangeInfo': {
            'Id': '/change/123456789012',
            'Status': 'PENDING',
            'SubmittedAt': datetime.datetime.now()}}
        self._stub_bifurcator(
            'change_resource_record_sets', expected_params, response,
            error_code=error_code)
