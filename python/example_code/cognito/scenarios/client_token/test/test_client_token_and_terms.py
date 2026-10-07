# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration test for the Amazon Cognito Identity Provider Client Token and
Terms scenario.

This test runs the full scenario end to end against real AWS: it deploys the
CloudFormation prerequisite stack, retrieves the app client secret, obtains M2M
access tokens with GetClientToken, discovers Terms documents with
DescribeTermsByClient, and deletes the stack. Interactive prompts are supplied
through the shared input-mocking fixture; AWS calls are not mocked.

Ensure you have appropriate AWS credentials and permissions before running.
"""

import boto3
import pytest

from cognito_idp_wrapper import CognitoIdentityProviderWrapper
from client_token_and_terms_scenario import ClientTokenAndTermsScenario


@pytest.mark.integ
def test_run_client_token_and_terms_scenario(input_mocker, capsys):
    wrapper = CognitoIdentityProviderWrapper(boto3.client("cognito-idp"))
    cfn_client = boto3.client("cloudformation")
    scenario = ClientTokenAndTermsScenario(wrapper, cfn_client)

    input_mocker.mock_answers(
        [
            "",  # Stack name prompt -> use the default.
            "",  # Press Enter after the all-scopes token.
            "",  # Press Enter after the terms-of-use lookup.
            "y",  # Confirm cleanup (delete the stack).
        ]
    )

    scenario.run()

    captured = capsys.readouterr()
    assert "Amazon Cognito Client Token and Terms scenario completed." in captured.out
    # The scenario reached the token and terms phases and cleaned up.
    assert "Token type: Bearer" in captured.out
    assert "All Cognito resources have been removed." in captured.out
