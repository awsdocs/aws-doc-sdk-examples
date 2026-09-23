# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the AWS STS Basics scenario.

These tests exercise the real AWS STS service — no mocking is used for the
main STS or CloudFormation clients. Resources are created and cleaned up
within the tests.

Run with:  pytest test_sts_basics.py -v
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import json
import time
import uuid

import boto3
import pytest
from botocore.exceptions import ClientError

from sts_wrapper import STSWrapper
from scenarios.sts_basics_scenario import STSScenario


@pytest.mark.integ
class TestSTSHello:
    """Integration tests for the Hello STS example."""

    def test_hello_sts_get_caller_identity(self):
        """GetCallerIdentity returns valid Account, Arn, and UserId."""
        sts_client = boto3.client("sts")
        response = sts_client.get_caller_identity()

        assert "Account" in response
        assert "Arn" in response
        assert "UserId" in response
        assert len(response["Account"]) == 12


@pytest.mark.integ
class TestSTSWrapper:
    """Integration tests for individual STSWrapper operations."""

    @pytest.fixture(autouse=True)
    def setup_wrapper(self):
        """Create an STSWrapper instance for each test."""
        self.sts_client = boto3.client("sts")
        self.wrapper = STSWrapper(self.sts_client)

    def test_get_caller_identity(self):
        """GetCallerIdentity returns Account, Arn, and UserId."""
        identity = self.wrapper.get_caller_identity()

        assert "Account" in identity
        assert "Arn" in identity
        assert "UserId" in identity
        assert len(identity["Account"]) == 12

    def test_get_access_key_info(self):
        """GetAccessKeyInfo returns the account that owns the access key."""
        session = boto3.Session()
        credentials = session.get_credentials()
        if credentials is None:
            pytest.skip("No credentials available to test GetAccessKeyInfo.")

        frozen = credentials.get_frozen_credentials()
        access_key_id = frozen.access_key

        account = self.wrapper.get_access_key_info(access_key_id)

        assert len(account) == 12
        # Account should match our current caller's account.
        identity = self.wrapper.get_caller_identity()
        assert account == identity["Account"]

    def test_get_session_token(self):
        """GetSessionToken returns temporary credentials."""
        # NOTE: GetSessionToken cannot be called with temporary credentials
        # from AssumeRole. If running under assumed-role credentials (e.g.
        # in CI), this test may be skipped.
        try:
            result = self.wrapper.get_session_token(duration_seconds=900)
        except ClientError as error:
            if error.response["Error"]["Code"] == "AccessDenied":
                pytest.skip(
                    "GetSessionToken not available with current credential type."
                )
            raise

        creds = result["Credentials"]
        assert "AccessKeyId" in creds
        assert "SecretAccessKey" in creds
        assert "SessionToken" in creds
        assert "Expiration" in creds
        assert creds["AccessKeyId"].startswith("ASIA")

    def test_decode_authorization_message_invalid(self):
        """DecodeAuthorizationMessage raises InvalidAuthorizationMessageException
        when given an invalid encoded message."""
        with pytest.raises(ClientError) as exc_info:
            self.wrapper.decode_authorization_message(
                encoded_message="this-is-not-valid"
            )
        assert (
            exc_info.value.response["Error"]["Code"]
            == "InvalidAuthorizationMessageException"
        )


@pytest.mark.integ
class TestSTSScenario:
    """Integration test that runs the full STS Basics scenario end-to-end."""

    def test_run_full_scenario(self):
        """
        Runs the full scenario: setup, all 10 steps, and cleanup.
        Resources are cleaned up in a finally block even if assertions fail.
        """
        sts_wrapper = STSWrapper.from_client()
        cf_client = boto3.client("cloudformation")
        scenario = STSScenario(sts_wrapper, cf_client)

        try:
            # Setup — deploy CloudFormation stack.
            scenario.setup()
            assert scenario.role_arn is not None
            assert scenario.stack_name is not None

            # Step 1: Verify current identity.
            scenario.step_1_verify_identity()
            assert scenario.original_identity is not None
            assert "Account" in scenario.original_identity

            # Step 2: Assume the role.
            scenario.step_2_assume_role()
            assert scenario.assumed_role_credentials is not None
            assert "AccessKeyId" in scenario.assumed_role_credentials
            assert scenario.assumed_role_credentials["AccessKeyId"].startswith("ASIA")

            # Step 3: Verify assumed-role identity.
            scenario.step_3_verify_assumed_identity()

            # Step 4: Get access key info.
            scenario.step_4_get_access_key_info()

            # Step 5: Get session token.
            try:
                scenario.step_5_get_session_token()
            except ClientError as error:
                if error.response["Error"]["Code"] == "AccessDenied":
                    print(
                        "Skipping Step 5 — GetSessionToken not available "
                        "with current credential type."
                    )
                else:
                    raise

            # Step 6: Get federation token.
            try:
                fed_result = scenario.step_6_get_federation_token()
                assert "Credentials" in fed_result
                assert "FederatedUser" in fed_result

                # Step 7: Verify federated identity.
                scenario.step_7_verify_federated_identity(fed_result["Credentials"])
            except ClientError as error:
                if error.response["Error"]["Code"] == "AccessDenied":
                    print(
                        "Skipping Steps 6-7 — GetFederationToken not available "
                        "with current credential type."
                    )
                else:
                    raise

            # Step 8: Decode authorization message (error path).
            scenario.step_8_decode_authorization_message()

            # Step 9: Get access key info for original key.
            scenario.step_9_get_access_key_info_original()

            # Step 10: Final identity check.
            scenario.step_10_final_identity_check()

        finally:
            # Always clean up regardless of test outcome.
            scenario.cleanup()
