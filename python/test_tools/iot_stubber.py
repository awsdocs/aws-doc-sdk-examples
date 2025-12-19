# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS IoT unit tests.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class IoTStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS IoT unit tests.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 IoT client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_thing(self, thing_name, thing_arn, error_code=None):
        expected_params = {"thingName": thing_name}
        response = {"thingName": thing_name, "thingArn": thing_arn}
        self._stub_bifurcator("create_thing", expected_params, response, error_code)

    def stub_list_things(self, things, error_code=None):
        expected_params = {}
        response = {"things": things}
        self._stub_bifurcator("list_things", expected_params, response, error_code)

    def stub_create_keys_and_certificate(
        self, certificate_arn, certificate_id, error_code=None
    ):
        expected_params = {"setAsActive": True}
        response = {
            "certificateArn": certificate_arn,
            "certificateId": certificate_id,
            "certificatePem": "test-pem",
            "keyPair": {"PublicKey": "test-public", "PrivateKey": "test-private"},
        }
        self._stub_bifurcator(
            "create_keys_and_certificate", expected_params, response, error_code
        )

    def stub_attach_thing_principal(self, thing_name, principal, error_code=None):
        expected_params = {"thingName": thing_name, "principal": principal}
        response = {}
        self._stub_bifurcator(
            "attach_thing_principal", expected_params, response, error_code
        )

    def stub_describe_endpoint(self, endpoint_address, error_code=None):
        expected_params = {"endpointType": "iot:Data-ATS"}
        response = {"endpointAddress": endpoint_address}
        self._stub_bifurcator(
            "describe_endpoint", expected_params, response, error_code
        )

    def stub_list_certificates(self, certificates, error_code=None):
        expected_params = {}
        response = {"certificates": certificates}
        self._stub_bifurcator(
            "list_certificates", expected_params, response, error_code
        )

    def stub_detach_thing_principal(self, thing_name, principal, error_code=None):
        expected_params = {"thingName": thing_name, "principal": principal}
        response = {}
        self._stub_bifurcator(
            "detach_thing_principal", expected_params, response, error_code
        )

    def stub_update_certificate(self, certificate_id, status, error_code=None):
        expected_params = {"certificateId": certificate_id, "newStatus": status}
        response = {}
        self._stub_bifurcator(
            "update_certificate", expected_params, response, error_code
        )

    def stub_delete_certificate(self, certificate_id, error_code=None):
        expected_params = {"certificateId": certificate_id}
        response = {}
        self._stub_bifurcator(
            "delete_certificate", expected_params, response, error_code
        )

    def stub_create_topic_rule(self, rule_name, error_code=None):
        expected_params = {"ruleName": rule_name, "topicRulePayload": ANY}
        response = {}
        self._stub_bifurcator(
            "create_topic_rule", expected_params, response, error_code
        )

    def stub_list_topic_rules(self, rules, error_code=None):
        expected_params = {}
        response = {"rules": rules}
        self._stub_bifurcator("list_topic_rules", expected_params, response, error_code)

    def stub_search_index(self, query, things, error_code=None):
        expected_params = {"queryString": query}
        response = {"things": things}
        self._stub_bifurcator("search_index", expected_params, response, error_code)

    def stub_update_indexing_configuration(self, error_code=None):
        expected_params = {"thingIndexingConfiguration": ANY}
        response = {}
        self._stub_bifurcator(
            "update_indexing_configuration", expected_params, response, error_code
        )

    def stub_delete_thing(self, thing_name, error_code=None):
        expected_params = {"thingName": thing_name}
        response = {}
        self._stub_bifurcator("delete_thing", expected_params, response, error_code)

    def stub_delete_topic_rule(self, rule_name, error_code=None):
        expected_params = {"ruleName": rule_name}
        response = {}
        self._stub_bifurcator(
            "delete_topic_rule", expected_params, response, error_code
        )
