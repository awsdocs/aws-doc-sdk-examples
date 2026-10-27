# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon SES v2 search-and-filter unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from boto3 import client

from test_tools.example_stubber import ExampleStubber


class Sesv2Stubber(ExampleStubber):
    """
    A class that implements stub functions used by the Amazon SES v2
    search-and-filter unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests and raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, sesv2_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param sesv2_client: A Boto 3 Amazon SES v2 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(sesv2_client, use_stubs)

    def stub_list_configuration_sets(
        self,
        configuration_sets,
        filter_params=None,
        page_size=None,
        next_token=None,
        response_next_token=None,
        error_code=None,
    ) -> None:
        """Stub the list_configuration_sets function."""
        expected_params = {}
        if filter_params is not None:
            expected_params["Filter"] = filter_params
        if page_size is not None:
            expected_params["PageSize"] = page_size
        if next_token is not None:
            expected_params["NextToken"] = next_token
        response = {"ConfigurationSets": configuration_sets}
        if response_next_token is not None:
            response["NextToken"] = response_next_token
        self._stub_bifurcator(
            "list_configuration_sets",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_list_email_identities(
        self,
        email_identities,
        filter_params=None,
        page_size=None,
        next_token=None,
        response_next_token=None,
        error_code=None,
    ) -> None:
        """Stub the list_email_identities function."""
        expected_params = {}
        if filter_params is not None:
            expected_params["Filter"] = filter_params
        if page_size is not None:
            expected_params["PageSize"] = page_size
        if next_token is not None:
            expected_params["NextToken"] = next_token
        response = {"EmailIdentities": email_identities}
        if response_next_token is not None:
            response["NextToken"] = response_next_token
        self._stub_bifurcator(
            "list_email_identities",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_list_tenants(
        self,
        tenants,
        filter_params=None,
        page_size=None,
        next_token=None,
        response_next_token=None,
        error_code=None,
    ) -> None:
        """Stub the list_tenants function."""
        expected_params = {}
        if filter_params is not None:
            expected_params["Filter"] = filter_params
        if page_size is not None:
            expected_params["PageSize"] = page_size
        if next_token is not None:
            expected_params["NextToken"] = next_token
        response = {"Tenants": tenants}
        if response_next_token is not None:
            response["NextToken"] = response_next_token
        self._stub_bifurcator(
            "list_tenants", expected_params, response, error_code=error_code
        )

    def stub_create_configuration_set(
        self, configuration_set_name, error_code=None
    ) -> None:
        """Stub the create_configuration_set function."""
        expected_params = {"ConfigurationSetName": configuration_set_name}
        response = {}
        self._stub_bifurcator(
            "create_configuration_set",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_create_email_identity(self, email_identity, error_code=None) -> None:
        """Stub the create_email_identity function."""
        expected_params = {"EmailIdentity": email_identity}
        response = {"IdentityType": "EMAIL_ADDRESS", "VerifiedForSendingStatus": False}
        self._stub_bifurcator(
            "create_email_identity",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_create_tenant(
        self, tenant_name, tenant_id="tn-123", error_code=None
    ) -> None:
        """Stub the create_tenant function."""
        expected_params = {"TenantName": tenant_name}
        response = {"TenantName": tenant_name, "TenantId": tenant_id}
        self._stub_bifurcator(
            "create_tenant", expected_params, response, error_code=error_code
        )

    def stub_delete_configuration_set(
        self, configuration_set_name, error_code=None
    ) -> None:
        """Stub the delete_configuration_set function."""
        expected_params = {"ConfigurationSetName": configuration_set_name}
        response = {}
        self._stub_bifurcator(
            "delete_configuration_set",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_delete_email_identity(self, email_identity, error_code=None) -> None:
        """Stub the delete_email_identity function."""
        expected_params = {"EmailIdentity": email_identity}
        response = {}
        self._stub_bifurcator(
            "delete_email_identity",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_delete_tenant(self, tenant_name, error_code=None) -> None:
        """Stub the delete_tenant function."""
        expected_params = {"TenantName": tenant_name}
        response = {}
        self._stub_bifurcator(
            "delete_tenant", expected_params, response, error_code=error_code
        )
