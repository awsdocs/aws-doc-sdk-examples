# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon SES v2 Search and Filter Basics Scenario
================================================

This scenario demonstrates the new filter support for list operations in
Amazon SES v2:

1. Setup: Create multiple configuration sets, email identities, and tenants.
2. Step 1: List all configuration sets (unfiltered).
3. Step 2: Filter configuration sets by name substring.
4. Step 3: List all email identities (unfiltered).
5. Step 4: Filter email identities by name substring.
6. Step 5: List all tenants (unfiltered).
7. Step 6: Filter tenants by name substring.
8. Step 7: Demonstrate pagination with filters.
9. Cleanup: Delete all resources created during setup.
"""

import logging
import time

from botocore.exceptions import ClientError

from sesv2_wrapper import SearchFilterWrapper

logger = logging.getLogger(__name__)

# Resource names used in the scenario.
CONFIGURATION_SET_NAMES = [
    "marketing-campaign-set",
    "transactional-email-set",
    "test-filter-set",
]
EMAIL_IDENTITIES = [
    "marketing@example.com",
    "support@example.com",
    "example.com",
]
TENANT_NAMES = [
    "customer-alpha",
    "customer-beta",
    "internal-testing",
]

# Server-side filter indexing is eventually consistent. A newly created
# resource can take several seconds to become visible to filtered list calls,
# even though it is returned immediately by an unfiltered list. Wait briefly
# after setup so the filter steps below return meaningful results.
FILTER_INDEX_SETTLE_SECONDS = 15


# snippet-start:[python.example_code.sesv2.SearchFilterBasicsScenario]
class SearchFilterBasicsScenario:
    """Runs the SES v2 Search and Filter Basics scenario."""

    def __init__(self, sesv2_wrapper: SearchFilterWrapper):
        """
        :param sesv2_wrapper: An instance of SearchFilterWrapper that provides
            the Amazon SES v2 operations used in this scenario.
        """
        self.sesv2_wrapper = sesv2_wrapper

    def run_scenario(self) -> None:
        """Orchestrates the full scenario."""
        print("-" * 88)
        print("Welcome to the Amazon SES v2 Search and Filter Basics scenario.")
        print("-" * 88)
        print(
            "This demo shows how to use the new filter parameters on\n"
            "ListConfigurationSets, ListEmailIdentities, and ListTenants to\n"
            "narrow results server-side."
        )
        print()

        created_config_sets = list()
        created_identities = list()
        created_tenants = list()

        try:
            # --- Setup ---
            print("--- Setup: Creating resources ---")
            created_config_sets = self._setup_configuration_sets()
            created_identities = self._setup_email_identities()
            created_tenants = self._setup_tenants()
            print()
            print(
                f"Waiting {FILTER_INDEX_SETTLE_SECONDS}s for the filter index to "
                "catch up with the newly created resources..."
            )
            time.sleep(FILTER_INDEX_SETTLE_SECONDS)
            print()

            # --- Step 1: Unfiltered configuration sets ---
            print("--- Step 1: List ALL configuration sets (unfiltered) ---")
            all_config_sets = self.sesv2_wrapper.list_configuration_sets()
            print(f"Found {len(all_config_sets)} configuration set(s):")
            for name in all_config_sets:
                print(f"  - {name}")
            print()

            # --- Step 2: Filtered configuration sets ---
            filter_value = "test"
            print(
                f'--- Step 2: Filter configuration sets (name contains "{filter_value}") ---'
            )
            filtered_config_sets = self.sesv2_wrapper.list_configuration_sets(
                filter_params={"CONFIGURATION_SET_NAME_CONTAINS": filter_value}
            )
            print(f"Found {len(filtered_config_sets)} matching configuration set(s):")
            for name in filtered_config_sets:
                print(f"  - {name}")
            print()

            # --- Step 3: Unfiltered email identities ---
            print("--- Step 3: List ALL email identities (unfiltered) ---")
            all_identities = self.sesv2_wrapper.list_email_identities()
            print(f"Found {len(all_identities)} email identity(ies):")
            for identity in all_identities:
                print(
                    f"  - {identity.get('IdentityName', 'N/A')} "
                    f"({identity.get('IdentityType', 'N/A')}, "
                    f"verification={identity.get('VerificationStatus', 'N/A')})"
                )
            print()

            # --- Step 4: Filtered email identities ---
            identity_filter_value = "example"
            print(
                f'--- Step 4: Filter email identities (name contains "{identity_filter_value}") ---'
            )
            filtered_identities = self.sesv2_wrapper.list_email_identities(
                filter_params={"IDENTITY_NAME_CONTAINS": identity_filter_value}
            )
            print(f"Found {len(filtered_identities)} matching identity(ies):")
            for identity in filtered_identities:
                print(
                    f"  - {identity.get('IdentityName', 'N/A')} "
                    f"({identity.get('IdentityType', 'N/A')})"
                )
            print()

            # --- Step 5: Unfiltered tenants ---
            print("--- Step 5: List ALL tenants (unfiltered) ---")
            all_tenants = self.sesv2_wrapper.list_tenants()
            print(f"Found {len(all_tenants)} tenant(s):")
            for tenant in all_tenants:
                print(
                    f"  - {tenant.get('TenantName', 'N/A')} "
                    f"(ID: {tenant.get('TenantId', 'N/A')})"
                )
            print()

            # --- Step 6: Filtered tenants ---
            tenant_filter_value = "customer"
            print(
                f'--- Step 6: Filter tenants (name contains "{tenant_filter_value}") ---'
            )
            filtered_tenants = self.sesv2_wrapper.list_tenants(
                filter_params={"TENANT_NAME_CONTAINS": tenant_filter_value}
            )
            print(f"Found {len(filtered_tenants)} matching tenant(s):")
            for tenant in filtered_tenants:
                print(
                    f"  - {tenant.get('TenantName', 'N/A')} "
                    f"(ID: {tenant.get('TenantId', 'N/A')})"
                )
            print()

            # --- Step 7: Pagination with filters ---
            print("--- Step 7: Demonstrate pagination with filters ---")
            print(
                "Listing configuration sets with filter and PageSize=1 to "
                "demonstrate paginated filtering..."
            )
            paginated_results = self.sesv2_wrapper.list_configuration_sets(
                filter_params={"CONFIGURATION_SET_NAME_CONTAINS": "set"},
                page_size=1,
            )
            print(
                f"Retrieved {len(paginated_results)} configuration set(s) "
                "across multiple pages:"
            )
            for name in paginated_results:
                print(f"  - {name}")
            print()

            print("-" * 88)
            print("Scenario demonstration complete!")
            print("-" * 88)

        finally:
            # --- Cleanup ---
            print()
            print("--- Cleanup: Removing resources created during setup ---")
            self._cleanup(created_config_sets, created_identities, created_tenants)
            print("Cleanup complete.")
            print("-" * 88)

    # ------------------------------------------------------------------
    # Setup helpers
    # ------------------------------------------------------------------

    def _setup_configuration_sets(self) -> list:
        """Creates configuration sets for the scenario and returns names."""
        created = list()
        for name in CONFIGURATION_SET_NAMES:
            self.sesv2_wrapper.create_configuration_set(name)
            created.append(name)
            print(f"  Created configuration set: {name}")
        return created

    def _setup_email_identities(self) -> list:
        """Creates email identities for the scenario and returns names."""
        created = list()
        for identity in EMAIL_IDENTITIES:
            try:
                response = self.sesv2_wrapper.create_email_identity(identity)
                created.append(identity)
                identity_type = response.get("IdentityType", "UNKNOWN")
                verified = response.get("VerifiedForSendingStatus", False)
                print(
                    f"  Created email identity: {identity} "
                    f"(type={identity_type}, verified={verified})"
                )
            except ClientError:
                logger.warning("Could not create identity '%s'. Continuing.", identity)
        return created

    def _setup_tenants(self) -> list:
        """Creates tenants for the scenario and returns names."""
        created = list()
        for name in TENANT_NAMES:
            try:
                response = self.sesv2_wrapper.create_tenant(name)
                created.append(name)
                tenant_id = response.get("TenantId", "N/A")
                print(f"  Created tenant: {name} (ID: {tenant_id})")
            except ClientError:
                logger.warning("Could not create tenant '%s'. Continuing.", name)
        return created

    # ------------------------------------------------------------------
    # Cleanup
    # ------------------------------------------------------------------

    def _cleanup(
        self,
        config_set_names: list,
        identity_names: list,
        tenant_names: list,
    ) -> None:
        """Deletes all resources created during setup."""
        for name in config_set_names:
            self.sesv2_wrapper.delete_configuration_set(name)
            print(f"  Deleted configuration set: {name}")

        for identity in identity_names:
            self.sesv2_wrapper.delete_email_identity(identity)
            print(f"  Deleted email identity: {identity}")

        for name in tenant_names:
            self.sesv2_wrapper.delete_tenant(name)
            print(f"  Deleted tenant: {name}")


# snippet-end:[python.example_code.sesv2.SearchFilterBasicsScenario]


def main():
    """Runs the SES v2 Search and Filter Basics scenario."""
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")
    try:
        sesv2_wrapper = SearchFilterWrapper.from_client()
        scenario = SearchFilterBasicsScenario(sesv2_wrapper)
        scenario.run_scenario()
    except Exception as e:
        logger.error("Failed to run scenario: %s", e)
        print(f"Failed to run the scenario: {e}")


if __name__ == "__main__":
    main()
