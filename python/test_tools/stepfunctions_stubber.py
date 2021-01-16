# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Step Functions unit tests.
"""

from datetime import datetime
import json
from test_tools.example_stubber import ExampleStubber


class StepFunctionsStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Step Functions unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Step Functions client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_state_machine(
            self, name, definition, role_arn, state_machine_arn, error_code=None):
        expected_params = {
            'name': name, 'definition': json.dumps(definition), 'roleArn': role_arn}
        response = {
            'stateMachineArn': state_machine_arn, 'creationDate': datetime.now()}
        self._stub_bifurcator(
            'create_state_machine', expected_params, response, error_code=error_code)

    def stub_update_state_machine(
            self, state_machine_arn, definition, role_arn=None, error_code=None):
        expected_params = {
            'stateMachineArn': state_machine_arn, 'definition': json.dumps(definition)}
        if role_arn is not None:
            expected_params['roleArn'] = role_arn
        response = {'updateDate': datetime.now()}
        self._stub_bifurcator(
            'update_state_machine', expected_params, response, error_code=error_code)

    def stub_delete_state_machine(self, state_machine_arn, error_code=None):
        expected_params = {'stateMachineArn': state_machine_arn}
        response = {}
        self._stub_bifurcator(
            'delete_state_machine', expected_params, response, error_code=error_code)

    def stub_list_state_machines(self, state_machines, error_code=None):
        expected_params = {}
        response = {'stateMachines': [
            {**sm, 'type': 'STANDARD', 'creationDate': datetime.now()}
            for sm in state_machines]}
        self._stub_bifurcator(
            'list_state_machines', expected_params, response, error_code=error_code)

    def stub_describe_state_machine(
            self, state_machine_arn, name, definition, role_arn, error_code=None):
        expected_params = {'stateMachineArn': state_machine_arn}
        response = {
            'name': name, 'definition': definition, 'roleArn': role_arn,
            'stateMachineArn': state_machine_arn, 'type': 'STANDARD',
            'creationDate': datetime.now()}
        self._stub_bifurcator(
            'describe_state_machine', expected_params, response, error_code=error_code)

    def stub_start_execution(
            self, state_machine_arn, run_name, run_arn, run_input=None,
            error_code=None):
        expected_params = {'stateMachineArn': state_machine_arn, 'name': run_name}
        if run_input is not None:
            expected_params['input'] = json.dumps(run_input)
        response = {'executionArn': run_arn, 'startDate': datetime.now()}
        self._stub_bifurcator(
            'start_execution', expected_params, response, error_code=error_code)

    def stub_list_executions(
            self, state_machine_arn, runs, run_status=None, error_code=None):
        expected_params = {'stateMachineArn': state_machine_arn}
        if run_status is not None:
            expected_params['statusFilter'] = run_status
        response = {'executions': [
            {**run, 'stateMachineArn': state_machine_arn,
             'status': run_status if run_status is not None else 'RUNNING',
             'startDate': datetime.now()} for run in runs]}
        self._stub_bifurcator(
            'list_executions', expected_params, response, error_code=error_code)

    def stub_stop_execution(self, run_arn, cause, error_code=None):
        expected_params = {'executionArn': run_arn, 'cause': cause}
        response = {'stopDate': datetime.now()}
        self._stub_bifurcator(
            'stop_execution', expected_params, response, error_code=error_code)
