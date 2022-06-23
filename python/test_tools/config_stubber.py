# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Config unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class ConfigStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS Config unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 AWS Config client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_put_config_rule(self, rule, error_code=None):
        expected_params = {'ConfigRule': rule}
        response = {}
        self._stub_bifurcator(
            'put_config_rule', expected_params, response, error_code=error_code)

    def stub_describe_config_rules(self, rule_names, source_ids=None, error_code=None):
        expected_params = {'ConfigRuleNames': rule_names}
        response = {'ConfigRules': [{
            'ConfigRuleName': name,
            'Source': {'Owner': 'Test', 'SourceIdentifier': 'TestID'}}
            for name in rule_names]}
        if source_ids is not None:
            for rule, source_id in zip(response['ConfigRules'], source_ids):
                rule['Source']['SourceIdentifier'] = source_id
        self._stub_bifurcator(
            'describe_config_rules', expected_params, response, error_code=error_code)

    def stub_delete_config_rule(self, rule_name, error_code=None):
        expected_params = {'ConfigRuleName': rule_name}
        response = {}
        self._stub_bifurcator(
            'delete_config_rule', expected_params, response, error_code=error_code)

    def stub_describe_conformance_packs(self, packs, error_code=None):
        expected_params = {}
        response = {'ConformancePackDetails': [{
            'ConformancePackName': pack,
            'ConformancePackArn': f'arn:{pack}',
            'ConformancePackId': f'{pack}-id'
        } for pack in packs]}
        self._stub_bifurcator(
            'describe_conformance_packs', expected_params, response, error_code=error_code)

    def stub_describe_conformance_pack_compliance(self, pack_name, rule_names, error_code=None):
        expected_params = {'ConformancePackName': pack_name}
        response = {
            'ConformancePackName': pack_name,
            'ConformancePackRuleComplianceList': [{
                'ConfigRuleName': rule_name
            } for rule_name in rule_names]}
        self._stub_bifurcator(
            'describe_conformance_pack_compliance', expected_params, response, error_code=error_code)
