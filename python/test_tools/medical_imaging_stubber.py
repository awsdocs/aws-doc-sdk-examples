# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Secrets Manager unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class MedicalImagingStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS HealthImaging unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 medical imaging client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_datastore(self, datastore_name, datastore_id, error_code=None):
        expected_params = {'datastoreName': datastore_name}
        response = {
            'datastoreId': datastore_id,
            'datastoreStatus': 'CREATING',
        }
        self._stub_bifurcator(
            'create_datastore', expected_params, response, error_code=error_code)

    def stub_get_datastore_properties(self, datastore_id, error_code=None):
        expected_params = {'datastoreId': datastore_id}
        response = {
            'datastoreProperties': {
                'datastoreId': datastore_id,
                'datastoreStatus': 'ACTIVE',
                'datastoreName': 'MyDataStore'
            }
        }

        self._stub_bifurcator(
            'get_datastore', expected_params, response, error_code=error_code)

    def stub_list_datastores(self, datastore_id, error_code=None):
        expected_params = {}
        response = {
            'datastoreSummaries': [
                {
                    'datastoreId': datastore_id,
                    'datastoreStatus': 'ACTIVE',
                    'datastoreName': 'MyDataStore1'
                }
            ]
        }

        self._stub_bifurcator(
            'list_datastores', expected_params, response, error_code=error_code)

    def stub_delete_data_store(self,  datastore_id, error_code=None):
        expected_params = {'datastoreId': datastore_id}
        response = {
            'datastoreId': datastore_id,
            'datastoreStatus': 'DELETING'
        }
        self._stub_bifurcator(
            'delete_datastore', expected_params, response, error_code=error_code)
