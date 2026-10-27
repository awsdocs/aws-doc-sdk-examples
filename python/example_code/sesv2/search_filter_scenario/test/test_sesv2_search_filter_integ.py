# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon SES v2 Search and Filter scenario.

These tests call the real AWS SES v2 service - do NOT mock the main service
client. Resources created during setup are cleaned up in a ``finally`` block.

Run with: pytest -m integ test_sesv2_search_filter_integ.py
"""

import logging
import os
import sys
import time
import uuid
from unittest.mock import patch

import pytest
from botocore.exceptions import ClientError

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from sesv2_wrapper import SearchFilterWrapper
from scenario_sesv2_search_filter_basics import SearchFilterBasicsScenario

logger = logging.getLogger(__name__)

# Unique suffix to avoid collisions across parallel test runs.
_UNIQUE = uuid.uuid4().hex[:8]

CONFIG_SET_NAMES = [
    f"integ-marketing-{_UNIQUE}",
    f"integ-transactional-{_UNIQUE}",
    f"integ-test-filter-{_UNIQUE}",
]

EMAIL_IDENTITIES = [
    f"integ-marketing-{_UNIQUE}@example.com",
    f"integ-support-{_UNIQUE}@example.com",
]

TENANT_NAMES = [
    f"integ-customer-alpha-{_UNIQUE}",
    f"integ-customer-beta-{_UNIQUE}",
]

# Server-side filter indexing is eventually consistent: a newly created
# resource can take several seconds to appear in filtered results even though
# it is already returned by an unfiltered list. Poll to absorb that delay.
_FILTER_POLL_ATTEMPTS = 10
_FILTER_POLL_DELAY_SECONDS = 5


def _poll_until(predicate):
    """
    Calls ``predicate`` until it returns a truthy value or attempts run out.

    :param predicate: A no-argument callable returning the filtered result.
    :return: The first truthy result, or the last result if none was truthy.
    """
    result = None
    for _ in range(_FILTER_POLL_ATTEMPTS):
        result = predicate()
        if result:
            return result
        time.sleep(_FILTER_POLL_DELAY_SECONDS)
    return result


@pytest.fixture(scope="module")
def sesv2_wrapper():
    """Provides a real SearchFilterWrapper for integration tests."""
    return SearchFilterWrapper.from_client()


@pytest.fixture(scope="module")
def resources(sesv2_wrapper):
    """
    Creates SES v2 resources for the test module and yields them. On teardown,
    deletes all resources created during setup.
    """
    created_config_sets = list()
    created_identities = list()
    created_tenants = list()

    try:
        for name in CONFIG_SET_NAMES:
            try:
                sesv2_wrapper.create_configuration_set(name)
                created_config_sets.append(name)
            except ClientError:
                logger.warning("Could not create config set '%s'.", name)

        for identity in EMAIL_IDENTITIES:
            try:
                sesv2_wrapper.create_email_identity(identity)
                created_identities.append(identity)
            except ClientError:
                logger.warning("Could not create identity '%s'.", identity)

        for name in TENANT_NAMES:
            try:
                sesv2_wrapper.create_tenant(name)
                created_tenants.append(name)
            except ClientError:
                logger.warning("Could not create tenant '%s'.", name)

        # Brief pause so resources are fully available.
        time.sleep(2)

        yield {
            "config_sets": created_config_sets,
            "identities": created_identities,
            "tenants": created_tenants,
        }
    finally:
        for name in created_config_sets:
            try:
                sesv2_wrapper.delete_configuration_set(name)
            except ClientError:
                logger.warning("Cleanup: could not delete config set '%s'.", name)
        for identity in created_identities:
            try:
                sesv2_wrapper.delete_email_identity(identity)
            except ClientError:
                logger.warning("Cleanup: could not delete identity '%s'.", identity)
        for name in created_tenants:
            try:
                sesv2_wrapper.delete_tenant(name)
            except ClientError:
                logger.warning("Cleanup: could not delete tenant '%s'.", name)


@pytest.mark.integ
class TestSeSv2SearchFilter:
    """Integration tests for SES v2 search-and-filter operations."""

    def test_list_configuration_sets_unfiltered(self, sesv2_wrapper, resources):
        """List all configuration sets (unfiltered) and verify created ones appear."""
        all_sets = sesv2_wrapper.list_configuration_sets()
        for name in resources["config_sets"]:
            assert (
                name in all_sets
            ), f"Expected config set '{name}' in unfiltered results."

    def test_list_configuration_sets_filtered(self, sesv2_wrapper, resources):
        """Filter configuration sets by name substring."""
        target = f"integ-test-filter-{_UNIQUE}"
        filtered = _poll_until(
            lambda: [
                name
                for name in sesv2_wrapper.list_configuration_sets(
                    filter_params={"CONFIGURATION_SET_NAME_CONTAINS": target}
                )
                if target in name
            ]
        )
        assert filtered, "Filtered results should include the test-filter config set."

    def test_list_email_identities_unfiltered(self, sesv2_wrapper, resources):
        """List all email identities (unfiltered) and verify created ones appear."""
        all_identities = sesv2_wrapper.list_email_identities()
        identity_names = [i.get("IdentityName") for i in all_identities]
        for identity in resources["identities"]:
            assert (
                identity in identity_names
            ), f"Expected identity '{identity}' in unfiltered results."

    def test_list_email_identities_filtered(self, sesv2_wrapper, resources):
        """Filter email identities by name substring."""
        target = f"integ-marketing-{_UNIQUE}"
        filtered = _poll_until(
            lambda: [
                name
                for name in (
                    i.get("IdentityName", "")
                    for i in sesv2_wrapper.list_email_identities(
                        filter_params={"IDENTITY_NAME_CONTAINS": target}
                    )
                )
                if target in name
            ]
        )
        assert filtered, "Filtered results should include the marketing identity."

    def test_list_tenants_unfiltered(self, sesv2_wrapper, resources):
        """List all tenants (unfiltered) and verify created ones appear."""
        all_tenants = sesv2_wrapper.list_tenants()
        tenant_names = [t.get("TenantName") for t in all_tenants]
        for name in resources["tenants"]:
            assert (
                name in tenant_names
            ), f"Expected tenant '{name}' in unfiltered results."

    def test_list_tenants_filtered(self, sesv2_wrapper, resources):
        """Filter tenants by name substring."""
        target = f"integ-customer-alpha-{_UNIQUE}"
        filtered = _poll_until(
            lambda: [
                name
                for name in (
                    t.get("TenantName", "")
                    for t in sesv2_wrapper.list_tenants(
                        filter_params={"TENANT_NAME_CONTAINS": target}
                    )
                )
                if target in name
            ]
        )
        assert filtered, "Filtered results should include customer-alpha tenant."

    def test_pagination_with_filter(self, sesv2_wrapper, resources):
        """Demonstrate pagination works with filters (PageSize=1)."""
        # All created config sets share the unique run suffix, so filtering on
        # it returns exactly this run's resources across multiple pages.
        expected = len(resources["config_sets"])

        def _fetch():
            found = sesv2_wrapper.list_configuration_sets(
                filter_params={"CONFIGURATION_SET_NAME_CONTAINS": _UNIQUE},
                page_size=1,
            )
            return found if len(found) >= expected else None

        integ_sets = _poll_until(_fetch)
        assert (
            integ_sets is not None and len(integ_sets) >= expected
        ), "Paginated + filtered results should include all integ config sets."


@pytest.mark.integ
def test_scenario_complete_integration():
    """
    Run the entire scenario end-to-end against real AWS services. The scenario
    creates its own resources and cleans them up in a finally block. time.sleep
    is patched out to keep the test fast.
    """
    sesv2_wrapper = SearchFilterWrapper.from_client()
    scenario = SearchFilterBasicsScenario(sesv2_wrapper)

    with patch("scenario_sesv2_search_filter_basics.time.sleep", return_value=None):
        # Should complete without raising; cleanup runs in the scenario's
        # finally block regardless of outcome.
        scenario.run_scenario()
