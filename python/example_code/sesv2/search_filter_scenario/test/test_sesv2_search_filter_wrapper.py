# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the Amazon SES v2 search-and-filter wrapper.

These tests use the botocore Stubber (via the make_stubber fixture) to capture
requests before they are sent to AWS and return mocked responses.
"""

import pytest
from botocore.exceptions import ClientError


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_list_configuration_sets(scenario_data, error_code):
    """Test list_configuration_sets with an optional filter."""
    filter_params = {"CONFIGURATION_SET_NAME_CONTAINS": "test"}
    expected = ["test-filter-set", "another-test-set"]

    scenario_data.sesv2_stubber.stub_list_configuration_sets(
        expected, filter_params=filter_params, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.list_configuration_sets(filter_params=filter_params)
        assert got == expected
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_configuration_sets(filter_params=filter_params)
        assert exc_info.value.response["Error"]["Code"] == error_code


def test_list_configuration_sets_paginates(scenario_data):
    """Test that list_configuration_sets follows NextToken across pages."""
    scenario_data.sesv2_stubber.stub_list_configuration_sets(
        ["set-1"], response_next_token="token-1"
    )
    scenario_data.sesv2_stubber.stub_list_configuration_sets(
        ["set-2"], next_token="token-1"
    )

    got = scenario_data.wrapper.list_configuration_sets()
    assert got == ["set-1", "set-2"]


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_list_email_identities(scenario_data, error_code):
    """Test list_email_identities with an optional filter."""
    filter_params = {"IDENTITY_NAME_CONTAINS": "example"}
    expected = [
        {"IdentityName": "marketing@example.com", "IdentityType": "EMAIL_ADDRESS"},
        {"IdentityName": "example.com", "IdentityType": "DOMAIN"},
    ]

    scenario_data.sesv2_stubber.stub_list_email_identities(
        expected, filter_params=filter_params, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.list_email_identities(filter_params=filter_params)
        assert got == expected
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_email_identities(filter_params=filter_params)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TooManyRequestsException"])
def test_list_tenants(scenario_data, error_code):
    """Test list_tenants with an optional filter and page size."""
    filter_params = {"TENANT_NAME_CONTAINS": "customer"}
    expected = [
        {"TenantName": "customer-alpha", "TenantId": "tn-1"},
        {"TenantName": "customer-beta", "TenantId": "tn-2"},
    ]

    scenario_data.sesv2_stubber.stub_list_tenants(
        expected, filter_params=filter_params, page_size=10, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.list_tenants(
            filter_params=filter_params, page_size=10
        )
        assert got == expected
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_tenants(
                filter_params=filter_params, page_size=10
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "AlreadyExistsException"])
def test_create_configuration_set(scenario_data, error_code):
    """Test create_configuration_set. AlreadyExistsException is swallowed."""
    name = "marketing-campaign-set"
    scenario_data.sesv2_stubber.stub_create_configuration_set(
        name, error_code=error_code
    )

    created = scenario_data.wrapper.create_configuration_set(name)

    # The wrapper doesn't raise for AlreadyExistsException. It returns False so
    # callers know they didn't create the configuration set.
    assert created is (error_code is None)


def test_list_configuration_sets_page(scenario_data):
    """Test list_configuration_sets_page returns one page and its NextToken."""
    filter_params = {"CONFIGURATION_SET_NAME_CONTAINS": "marketing"}
    scenario_data.sesv2_stubber.stub_list_configuration_sets(
        ["marketing-campaign-set"],
        filter_params=filter_params,
        page_size=1,
        response_next_token="token-2",
    )
    scenario_data.sesv2_stubber.stub_list_configuration_sets(
        ["marketing-newsletter-set"],
        filter_params=filter_params,
        page_size=1,
        next_token="token-2",
    )

    first = scenario_data.wrapper.list_configuration_sets_page(filter_params, 1)
    second = scenario_data.wrapper.list_configuration_sets_page(
        filter_params, 1, next_token="token-2"
    )

    assert first == (["marketing-campaign-set"], "token-2")
    assert second == (["marketing-newsletter-set"], None)


@pytest.mark.parametrize("error_code", [None, "TooManyRequestsException"])
def test_create_email_identity(scenario_data, error_code):
    """Test create_email_identity."""
    identity = "marketing@example.com"
    scenario_data.sesv2_stubber.stub_create_email_identity(
        identity, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.create_email_identity(identity)
        assert got["IdentityType"] == "EMAIL_ADDRESS"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_email_identity(identity)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_create_tenant(scenario_data, error_code):
    """Test create_tenant."""
    name = "customer-alpha"
    scenario_data.sesv2_stubber.stub_create_tenant(
        name, tenant_id="tn-1", error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.create_tenant(name)
        assert got["TenantName"] == name
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_tenant(name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_delete_configuration_set(scenario_data, error_code):
    """Test delete_configuration_set. NotFoundException is swallowed."""
    name = "marketing-campaign-set"
    scenario_data.sesv2_stubber.stub_delete_configuration_set(
        name, error_code=error_code
    )
    # NotFoundException is logged and swallowed; success returns None.
    scenario_data.wrapper.delete_configuration_set(name)


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_delete_email_identity(scenario_data, error_code):
    """Test delete_email_identity. NotFoundException is swallowed."""
    identity = "marketing@example.com"
    scenario_data.sesv2_stubber.stub_delete_email_identity(
        identity, error_code=error_code
    )
    scenario_data.wrapper.delete_email_identity(identity)


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_delete_tenant(scenario_data, error_code):
    """Test delete_tenant. NotFoundException is swallowed."""
    name = "customer-alpha"
    scenario_data.sesv2_stubber.stub_delete_tenant(name, error_code=error_code)
    scenario_data.wrapper.delete_tenant(name)
