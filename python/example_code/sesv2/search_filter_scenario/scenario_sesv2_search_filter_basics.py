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
8. Step 7: Page through filtered configuration sets one result at a time.
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
    "marketing-newsletter-set",
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

# Filter values used by the filter steps.
CONFIG_SET_FILTER = "marketing"
IDENTITY_FILTER = "marketing"
TENANT_FILTER = "customer"

# Server-side filter indexing is eventually consistent. A newly created
# resource can take several seconds to appear in filtered results, even though
# an unfiltered list returns it right away. Setup repeats the filtered calls
# until the new resources appear.
FILTER_WAIT_ATTEMPTS = 12
FILTER_WAIT_DELAY_SECONDS = 5


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
            # Each setup helper adds resources to its list as they're created,
            # so cleanup can delete them even if setup fails partway through.
            print("--- Setup: Creating resources ---")
            self._setup_configuration_sets(created_config_sets)
            self._setup_email_identities(created_identities)
            self._setup_tenants(created_tenants)
            self._wait_for_filtered_results(
                created_config_sets, created_identities, created_tenants
            )
            print()

            # --- Step 1: Unfiltered configuration sets ---
            print("--- Step 1: List ALL configuration sets (unfiltered) ---")
            all_config_sets = self.sesv2_wrapper.list_configuration_sets()
            print(f"Found {len(all_config_sets)} configuration set(s):")
            for name in all_config_sets:
                print(f"  - {name}")
            print()

            # --- Step 2: Filtered configuration sets ---
            print(
                "--- Step 2: Filter configuration sets "
                f'(name contains "{CONFIG_SET_FILTER}") ---'
            )
            filtered_config_sets = self.sesv2_wrapper.list_configuration_sets(
                filter_params={"CONFIGURATION_SET_NAME_CONTAINS": CONFIG_SET_FILTER}
            )
            print(
                f"Found {len(filtered_config_sets)} of {len(all_config_sets)} "
                "configuration set(s):"
            )
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
            print(
                "--- Step 4: Filter email identities "
                f'(name contains "{IDENTITY_FILTER}") ---'
            )
            filtered_identities = self.sesv2_wrapper.list_email_identities(
                filter_params={"IDENTITY_NAME_CONTAINS": IDENTITY_FILTER}
            )
            print(
                f"Found {len(filtered_identities)} of {len(all_identities)} "
                "email identity(ies):"
            )
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
            print(f'--- Step 6: Filter tenants (name contains "{TENANT_FILTER}") ---')
            filtered_tenants = self.sesv2_wrapper.list_tenants(
                filter_params={"TENANT_NAME_CONTAINS": TENANT_FILTER}
            )
            print(f"Found {len(filtered_tenants)} of {len(all_tenants)} tenant(s):")
            for tenant in filtered_tenants:
                print(
                    f"  - {tenant.get('TenantName', 'N/A')} "
                    f"(ID: {tenant.get('TenantId', 'N/A')})"
                )
            print()

            # --- Step 7: Pagination with filters ---
            print("--- Step 7: Page through filtered configuration sets ---")
            print(
                "Listing configuration sets whose name contains "
                f'"{CONFIG_SET_FILTER}" with PageSize=1.\n'
                "Each response holds one result and a NextToken that requests "
                "the next page."
            )
            page_number = 0
            next_token = None
            while True:
                page, next_token = self.sesv2_wrapper.list_configuration_sets_page(
                    {"CONFIGURATION_SET_NAME_CONTAINS": CONFIG_SET_FILTER},
                    page_size=1,
                    next_token=next_token,
                )
                page_number += 1
                print(f"  Page {page_number}: {', '.join(page) or '(empty)'}")
                if not next_token:
                    break
            print()

            print("-" * 88)
            print("Scenario demonstration complete!")
            print("-" * 88)

        finally:
            # --- Cleanup ---
            print()
            print("--- Cleanup: Removing resources created during setup ---")
            self._cleanup(created_config_sets, created_identities, created_tenants)
            print("-" * 88)

    # ------------------------------------------------------------------
    # Setup helpers
    # ------------------------------------------------------------------

    def _setup_configuration_sets(self, created: list) -> None:
        """
        Creates configuration sets for the scenario and adds each new one to
        created. A configuration set that already exists isn't added, so
        cleanup won't delete it.
        """
        for name in CONFIGURATION_SET_NAMES:
            if self.sesv2_wrapper.create_configuration_set(name):
                created.append(name)
                print(f"  Created configuration set: {name}")
            else:
                print(f"  Configuration set {name} already exists. Using it as is.")

    def _setup_email_identities(self, created: list) -> None:
        """Creates email identities and adds each new one to created."""
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

    def _setup_tenants(self, created: list) -> None:
        """Creates tenants and adds each new one to created."""
        for name in TENANT_NAMES:
            try:
                response = self.sesv2_wrapper.create_tenant(name)
                created.append(name)
                tenant_id = response.get("TenantId", "N/A")
                print(f"  Created tenant: {name} (ID: {tenant_id})")
            except ClientError:
                logger.warning("Could not create tenant '%s'. Continuing.", name)

    def _wait_for_filtered_results(
        self, config_set_names: list, identity_names: list, tenant_names: list
    ) -> None:
        """
        Repeats the scenario's filtered list calls until each one includes the
        new resources that match its filter. Each resource type is checked
        with its own filtered call, because one list operation being up to
        date doesn't mean the others are.
        """
        expected_config_sets = {n for n in config_set_names if CONFIG_SET_FILTER in n}
        expected_identities = {i for i in identity_names if IDENTITY_FILTER in i}
        expected_tenants = {n for n in tenant_names if TENANT_FILTER in n}

        print("Waiting for the new resources to appear in filtered results...")
        for attempt in range(1, FILTER_WAIT_ATTEMPTS + 1):
            config_sets = set(
                self.sesv2_wrapper.list_configuration_sets(
                    filter_params={"CONFIGURATION_SET_NAME_CONTAINS": CONFIG_SET_FILTER}
                )
            )
            identities = {
                i["IdentityName"]
                for i in self.sesv2_wrapper.list_email_identities(
                    filter_params={"IDENTITY_NAME_CONTAINS": IDENTITY_FILTER}
                )
            }
            tenants = {
                t["TenantName"]
                for t in self.sesv2_wrapper.list_tenants(
                    filter_params={"TENANT_NAME_CONTAINS": TENANT_FILTER}
                )
            }
            if (
                expected_config_sets <= config_sets
                and expected_identities <= identities
                and expected_tenants <= tenants
            ):
                print("  The new resources are available in filtered results.")
                return
            if attempt < FILTER_WAIT_ATTEMPTS:
                time.sleep(FILTER_WAIT_DELAY_SECONDS)
        print(
            "  Some new resources still aren't in filtered results. "
            "The filter steps may show fewer results than expected."
        )

    # ------------------------------------------------------------------
    # Cleanup
    # ------------------------------------------------------------------

    def _cleanup(
        self,
        config_set_names: list,
        identity_names: list,
        tenant_names: list,
    ) -> None:
        """
        Deletes all resources created during setup. Each delete is attempted
        on its own, so one failure doesn't leave the rest in the account.
        """
        not_deleted = list()
        for kind, names, delete in (
            (
                "configuration set",
                config_set_names,
                self.sesv2_wrapper.delete_configuration_set,
            ),
            (
                "email identity",
                identity_names,
                self.sesv2_wrapper.delete_email_identity,
            ),
            ("tenant", tenant_names, self.sesv2_wrapper.delete_tenant),
        ):
            for name in names:
                try:
                    delete(name)
                    print(f"  Deleted {kind}: {name}")
                except ClientError as err:
                    print(
                        f"  Couldn't delete {kind} {name}: "
                        f"{err.response['Error']['Message']}"
                    )
                    not_deleted.append(f"{kind} {name}")

        if not_deleted:
            print("These resources weren't deleted. Delete them manually:")
            for item in not_deleted:
                print(f"  - {item}")
        else:
            print("Cleanup complete.")


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
