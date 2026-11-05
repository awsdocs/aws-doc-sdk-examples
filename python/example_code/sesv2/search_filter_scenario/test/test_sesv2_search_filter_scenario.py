# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the Amazon SES v2 Search and Filter scenario.

These tests use stubbed responses, so they make no calls to AWS.
"""

import pytest
from botocore.exceptions import ClientError

import scenario_sesv2_search_filter_basics as scenario_module
from scenario_sesv2_search_filter_basics import (
    CONFIG_SET_FILTER,
    CONFIGURATION_SET_NAMES,
    EMAIL_IDENTITIES,
    IDENTITY_FILTER,
    TENANT_FILTER,
    TENANT_NAMES,
    SearchFilterBasicsScenario,
)

CONFIG_SET_FILTER_PARAMS = {"CONFIGURATION_SET_NAME_CONTAINS": CONFIG_SET_FILTER}
IDENTITY_FILTER_PARAMS = {"IDENTITY_NAME_CONTAINS": IDENTITY_FILTER}
TENANT_FILTER_PARAMS = {"TENANT_NAME_CONTAINS": TENANT_FILTER}

MATCHING_CONFIG_SETS = [n for n in CONFIGURATION_SET_NAMES if CONFIG_SET_FILTER in n]


def _identities(names):
    return [{"IdentityName": n, "IdentityType": "EMAIL_ADDRESS"} for n in names]


def _tenants(names):
    return [{"TenantName": n, "TenantId": f"tn-{i}"} for i, n in enumerate(names)]


ALL_IDENTITIES = _identities(EMAIL_IDENTITIES)
MATCHING_IDENTITIES = _identities([i for i in EMAIL_IDENTITIES if IDENTITY_FILTER in i])
ALL_TENANTS = _tenants(TENANT_NAMES)
MATCHING_TENANTS = _tenants([n for n in TENANT_NAMES if TENANT_FILTER in n])


@pytest.fixture
def sleeps(monkeypatch):
    """Replaces time.sleep in the scenario and records each call."""
    calls = []
    monkeypatch.setattr(scenario_module.time, "sleep", calls.append)
    return calls


def _stub_setup(stubber):
    for name in CONFIGURATION_SET_NAMES:
        stubber.stub_create_configuration_set(name)
    for identity in EMAIL_IDENTITIES:
        stubber.stub_create_email_identity(identity)
    for name in TENANT_NAMES:
        stubber.stub_create_tenant(name)


def _stub_wait_attempt(stubber, ready):
    """Stubs one round of the setup wait's filtered list calls."""
    stubber.stub_list_configuration_sets(
        MATCHING_CONFIG_SETS if ready else [], filter_params=CONFIG_SET_FILTER_PARAMS
    )
    stubber.stub_list_email_identities(
        MATCHING_IDENTITIES if ready else [], filter_params=IDENTITY_FILTER_PARAMS
    )
    stubber.stub_list_tenants(
        MATCHING_TENANTS if ready else [], filter_params=TENANT_FILTER_PARAMS
    )


def _stub_steps(stubber):
    stubber.stub_list_configuration_sets(CONFIGURATION_SET_NAMES)
    stubber.stub_list_configuration_sets(
        MATCHING_CONFIG_SETS, filter_params=CONFIG_SET_FILTER_PARAMS
    )
    stubber.stub_list_email_identities(ALL_IDENTITIES)
    stubber.stub_list_email_identities(
        MATCHING_IDENTITIES, filter_params=IDENTITY_FILTER_PARAMS
    )
    stubber.stub_list_tenants(ALL_TENANTS)
    stubber.stub_list_tenants(MATCHING_TENANTS, filter_params=TENANT_FILTER_PARAMS)
    # Step 7 pages through the filtered configuration sets one at a time.
    stubber.stub_list_configuration_sets(
        MATCHING_CONFIG_SETS[:1],
        filter_params=CONFIG_SET_FILTER_PARAMS,
        page_size=1,
        response_next_token="page-2",
    )
    stubber.stub_list_configuration_sets(
        MATCHING_CONFIG_SETS[1:2],
        filter_params=CONFIG_SET_FILTER_PARAMS,
        page_size=1,
        next_token="page-2",
    )


def _stub_cleanup(stubber, failing_config_set=None):
    for name in CONFIGURATION_SET_NAMES:
        error = "TooManyRequestsException" if name == failing_config_set else None
        stubber.stub_delete_configuration_set(name, error_code=error)
    for identity in EMAIL_IDENTITIES:
        stubber.stub_delete_email_identity(identity)
    for name in TENANT_NAMES:
        stubber.stub_delete_tenant(name)


def test_run_scenario(scenario_data, sleeps, capsys):
    stubber = scenario_data.sesv2_stubber
    _stub_setup(stubber)
    _stub_wait_attempt(stubber, ready=True)
    _stub_steps(stubber)
    _stub_cleanup(stubber)

    SearchFilterBasicsScenario(scenario_data.wrapper).run_scenario()

    out = capsys.readouterr().out
    # Each filter returns a subset of the unfiltered results.
    assert "Found 2 of 4 configuration set(s)" in out
    assert "Found 1 of 3 email identity(ies)" in out
    assert "Found 2 of 3 tenant(s)" in out
    # Pagination shows each page separately.
    assert "Page 1: marketing-campaign-set" in out
    assert "Page 2: marketing-newsletter-set" in out
    assert "Cleanup complete." in out
    # The filtered results were ready on the first check, so there was no wait.
    assert sleeps == []


def test_run_scenario_waits_for_filtered_results(scenario_data, sleeps, capsys):
    stubber = scenario_data.sesv2_stubber
    _stub_setup(stubber)
    _stub_wait_attempt(stubber, ready=False)
    _stub_wait_attempt(stubber, ready=True)
    _stub_steps(stubber)
    _stub_cleanup(stubber)

    SearchFilterBasicsScenario(scenario_data.wrapper).run_scenario()

    out = capsys.readouterr().out
    assert sleeps == [scenario_module.FILTER_WAIT_DELAY_SECONDS]
    assert "The new resources are available in filtered results." in out


def test_wait_gives_up_after_last_attempt(scenario_data, sleeps, capsys, monkeypatch):
    monkeypatch.setattr(scenario_module, "FILTER_WAIT_ATTEMPTS", 2)
    stubber = scenario_data.sesv2_stubber
    _stub_wait_attempt(stubber, ready=False)
    _stub_wait_attempt(stubber, ready=False)

    SearchFilterBasicsScenario(scenario_data.wrapper)._wait_for_filtered_results(
        CONFIGURATION_SET_NAMES, EMAIL_IDENTITIES, TENANT_NAMES
    )

    out = capsys.readouterr().out
    assert "Some new resources still aren't in filtered results." in out
    # It sleeps between attempts but not after the last one.
    assert len(sleeps) == 1


def test_cleanup_continues_after_failed_delete(scenario_data, capsys):
    stubber = scenario_data.sesv2_stubber
    failing = CONFIGURATION_SET_NAMES[0]
    _stub_cleanup(stubber, failing_config_set=failing)

    # Every stubbed delete must be used, or the stubber fails the test, so this
    # also confirms that cleanup kept going after the failure.
    SearchFilterBasicsScenario(scenario_data.wrapper)._cleanup(
        CONFIGURATION_SET_NAMES, EMAIL_IDENTITIES, TENANT_NAMES
    )

    out = capsys.readouterr().out
    assert f"Couldn't delete configuration set {failing}" in out
    assert "These resources weren't deleted." in out
    assert f"  - configuration set {failing}" in out
    assert "Cleanup complete." not in out


def test_setup_leaves_existing_configuration_set(scenario_data, capsys):
    """A configuration set that already existed isn't marked for cleanup."""
    stubber = scenario_data.sesv2_stubber
    existing = CONFIGURATION_SET_NAMES[1]
    for name in CONFIGURATION_SET_NAMES:
        error = "AlreadyExistsException" if name == existing else None
        stubber.stub_create_configuration_set(name, error_code=error)

    created = []
    SearchFilterBasicsScenario(scenario_data.wrapper)._setup_configuration_sets(created)

    assert existing not in created
    assert created == [n for n in CONFIGURATION_SET_NAMES if n != existing]
    assert f"Configuration set {existing} already exists." in capsys.readouterr().out


def test_failed_setup_still_cleans_up_created_resources(scenario_data, capsys):
    """Resources created before a setup failure are still deleted."""
    stubber = scenario_data.sesv2_stubber
    first, failing = CONFIGURATION_SET_NAMES[0], CONFIGURATION_SET_NAMES[1]
    stubber.stub_create_configuration_set(first)
    stubber.stub_create_configuration_set(failing, error_code="BadRequestException")
    # Only the configuration set created before the failure is deleted. The
    # stubber fails the test if this delete isn't made.
    stubber.stub_delete_configuration_set(first)

    with pytest.raises(ClientError):
        SearchFilterBasicsScenario(scenario_data.wrapper).run_scenario()

    out = capsys.readouterr().out
    assert f"Deleted configuration set: {first}" in out
    assert "Cleanup complete." in out
