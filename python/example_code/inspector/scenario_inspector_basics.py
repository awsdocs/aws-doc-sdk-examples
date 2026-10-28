# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Inspector to:

Setup
  - Check the Amazon Inspector account status.
  - Enable Inspector for available resource types (if not already enabled).
  - Display an account status summary.
Coverage Assessment
  - List coverage details for scanned resources.
  - Display overall coverage statistics.
  - Review the scan status of resources.
Findings Management
  - List security findings across all resource types.
  - Analyze the details of a finding.
  - Create an example filter that suppresses low-severity findings.
  - List existing filters.
Usage and Costs
  - Check usage totals for Inspector.
  - Review coverage statistics by resource type.
Cleanup
  - Delete the filter.
  - Disable Inspector.

This scenario does not require any additional AWS resources.
"""

import logging
import os
import sys
import time
from collections import Counter
from typing import Optional

from botocore.exceptions import ClientError

from inspector_wrapper import Inspector2Wrapper, RESOURCE_TYPES

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(os.path.dirname(os.path.abspath(__file__)), "../.."))
import demo_tools.question as q  # noqa: E402

logger = logging.getLogger(__name__)

DASHES = "-" * 80

# Maps the resourceState keys returned by BatchGetAccountStatus to the resource
# types accepted by Enable and Disable.
RESOURCE_STATE_KEYS = {
    "ec2": "EC2",
    "ecr": "ECR",
    "lambda": "LAMBDA",
    "lambdaCode": "LAMBDA_CODE",
}


# snippet-start:[python.example_code.inspector2.Inspector2Scenario]
class InspectorScenario:
    """Runs an interactive scenario that shows how to use Amazon Inspector."""

    def __init__(self, inspector_wrapper: Inspector2Wrapper) -> None:
        """
        :param inspector_wrapper: An object that wraps Amazon Inspector actions.
        """
        self.inspector_wrapper = inspector_wrapper
        self.filter_arn: Optional[str] = None
        # The resource types this scenario enabled, so cleanup can disable them.
        self.enabled_types: list[str] = list()

    @staticmethod
    def _wait_for_input() -> None:
        q.ask("\nPress Enter to continue...")
        print(DASHES)

    # ------------------------------------------------------------------
    # Setup
    # ------------------------------------------------------------------
    def setup(self) -> None:
        """
        Checks the account status, enables Inspector for any resource types
        that are not already enabled, and displays an account status summary.
        """
        print(DASHES)
        print("Setup: Checking Inspector account status...")
        accounts = self.inspector_wrapper.get_account_status()
        to_enable = list()
        for account in accounts:
            print(f"  Account ID: {account.get('accountId', 'Unknown')}")
            print(
                "  Overall State: "
                f"{account.get('state', dict()).get('status', 'Unknown')}"
            )
            resource_state = account.get("resourceState", dict())
            for key, resource_type in RESOURCE_STATE_KEYS.items():
                status = resource_state.get(key, dict()).get("status", "DISABLED")
                if status != "ENABLED" and resource_type not in to_enable:
                    to_enable.append(resource_type)

        if to_enable:
            print(f"\nEnabling Inspector for: {', '.join(to_enable)}...")
            results = self.inspector_wrapper.enable(to_enable)
            for result in results:
                print(
                    f"  Account: {result.get('accountId', 'Unknown')} -> "
                    f"Status: {result.get('status', 'Unknown')}"
                )
            if results:
                self.enabled_types = to_enable
        else:
            print("\nInspector is already enabled for all resource types.")

        print("\nAccount status summary:")
        for account in self.inspector_wrapper.get_account_status():
            print(f"  Account ID: {account.get('accountId', 'Unknown')}")
            resource_state = account.get("resourceState", dict())
            for key, resource_type in RESOURCE_STATE_KEYS.items():
                status = resource_state.get(key, dict()).get("status", "UNKNOWN")
                print(f"    {resource_type:12s}: {status}")

    # ------------------------------------------------------------------
    # Coverage Assessment
    # ------------------------------------------------------------------
    def assess_coverage(self) -> None:
        """
        Lists coverage details, displays overall coverage statistics, and
        reviews the scan status of covered resources.
        """
        print("Coverage Assessment: Listing coverage details...")
        resources = self.inspector_wrapper.list_coverage(max_results=5)
        if not resources:
            print("  No coverage information found.")
        else:
            by_type = Counter(r.get("resourceType") for r in resources)
            for resource_type, count in by_type.items():
                print(f"  {resource_type}: {count} resource(s)")
            print("\n  Sample resources:")
            for resource in resources[:3]:
                print(
                    f"  - {resource.get('resourceType')}: {resource.get('resourceId')}"
                )
                print(f"    Scan Type : {resource.get('scanType')}")
                scan_status = resource.get("scanStatus", dict())
                if scan_status:
                    print(f"    Status    : {scan_status.get('statusCode')}")
                print(f"    Account ID: {resource.get('accountId')}")

        print("\nOverall coverage statistics:")
        stats = self.inspector_wrapper.list_coverage_statistics()
        print(f"  Total covered resources: {stats['totalCounts']}")

        print("\nScan status of resources:")
        if resources:
            by_status = Counter(
                r.get("scanStatus", dict()).get("statusCode", "UNKNOWN")
                for r in resources
            )
            for status_code, count in by_status.items():
                print(f"  {status_code}: {count} resource(s)")
        else:
            print("  No resources to review.")

    # ------------------------------------------------------------------
    # Findings Management
    # ------------------------------------------------------------------
    def manage_findings(self) -> None:
        """
        Lists findings across all resource types, analyzes one finding, creates
        a filter that suppresses low-severity findings, and lists filters.
        """
        print("Findings Management: Listing security findings...")
        findings = self.inspector_wrapper.list_findings(max_results=10)
        if not findings:
            print("  No findings found. This is common for new accounts.")
        else:
            for finding in findings:
                print(
                    f"  - [{finding.get('severity')}] {finding.get('title', 'Untitled')}"
                )
                print(f"    Type  : {finding.get('type')}")
                print(f"    Status: {finding.get('status')}")
                print(f"    ARN   : {finding.get('findingArn')}")

            finding_arn = findings[0]["findingArn"]
            print(f"\nAnalyzing finding details for: {finding_arn}")
            details = self.inspector_wrapper.batch_get_finding_details([finding_arn])
            if not details:
                print(f"  No details found for {finding_arn}.")
            for detail in details:
                print(f"  Risk Score: {detail.get('riskScore', 'N/A')}")
                exploit = detail.get("exploitObserved")
                if exploit:
                    print(f"  Exploit First Seen: {exploit.get('firstSeen')}")
                    print(f"  Exploit Last Seen : {exploit.get('lastSeen')}")
                for label, key in (
                    ("CWEs", "cwes"),
                    ("TTPs", "ttps"),
                    ("Tools", "tools"),
                    ("Reference URLs", "referenceUrls"),
                ):
                    values = detail.get(key, list())
                    if values:
                        print(f"  {label}: {', '.join(values)}")

        filter_name = f"suppress-low-{int(time.time())}"
        print(f"\nCreating filter '{filter_name}' to suppress LOW severity findings...")
        self.filter_arn = self.inspector_wrapper.create_low_severity_filter(
            filter_name, "Suppress low severity findings"
        )
        if self.filter_arn:
            print(f"  Created filter: {self.filter_arn}")
        else:
            print("  The filter was not created.")

        print("\nListing filters...")
        filters = self.inspector_wrapper.list_filters(max_results=10)
        if not filters:
            print("  No filters found.")
        for inspector_filter in filters:
            print(
                f"  - {inspector_filter.get('name')}: {inspector_filter.get('arn')} "
                f"({inspector_filter.get('action')})"
            )

    # ------------------------------------------------------------------
    # Usage and Costs
    # ------------------------------------------------------------------
    def check_usage_and_costs(self) -> None:
        """Lists usage totals and reviews coverage statistics by resource type."""
        print("Usage and Costs: Checking usage totals...")
        totals = self.inspector_wrapper.list_usage_totals(max_results=10)
        if not totals:
            print("  No usage totals found.")
        for total in totals:
            print(f"  Account ID: {total.get('accountId')}")
            for usage in total.get("usage", list()):
                cost = usage.get("estimatedMonthlyCost")
                cost_text = (
                    f" (est. {cost} {usage.get('currency', '')})" if cost else ""
                )
                print(f"    {usage.get('type')}: {usage.get('total')}{cost_text}")

        print("\nCoverage statistics by resource type:")
        stats = self.inspector_wrapper.list_coverage_statistics(
            group_by="RESOURCE_TYPE"
        )
        if not stats["countsByGroup"]:
            print("  No coverage statistics available.")
        for group in stats["countsByGroup"]:
            print(f"  {group.get('groupKey')}: {group.get('count')}")

    # ------------------------------------------------------------------
    # Cleanup
    # ------------------------------------------------------------------
    def cleanup(self) -> None:
        """Deletes the filter and disables Inspector, if the user agrees."""
        print("Cleanup")
        if self.filter_arn:
            print(f"  Filter ARN: {self.filter_arn}")
        if not q.ask("Delete the filter and disable Inspector? (y/n) ", q.is_yesno):
            print("  Leaving the filter and Inspector settings in place.")
            return

        if self.filter_arn:
            self.inspector_wrapper.delete_filter(self.filter_arn)
            print("  Filter deleted.")
            self.filter_arn = None

        disable_types = self.enabled_types
        if not disable_types and q.ask(
            "Inspector was already enabled before this scenario ran. "
            "Disable it anyway? (y/n) ",
            q.is_yesno,
        ):
            disable_types = RESOURCE_TYPES
        if disable_types:
            results = self.inspector_wrapper.disable(disable_types)
            for result in results:
                print(
                    f"  Account: {result.get('accountId', 'Unknown')} -> "
                    f"Status: {result.get('status', 'Unknown')}"
                )
            print(f"  Inspector disabled for: {', '.join(disable_types)}")
            self.enabled_types = list()
        else:
            print("  Inspector was left enabled.")

    def _cleanup_after_error(self) -> None:
        """Removes resources this scenario created when a step fails."""
        if self.filter_arn:
            try:
                self.inspector_wrapper.delete_filter(self.filter_arn)
                print("Cleanup: Filter deleted.")
            except ClientError as err:
                logger.warning("Failed to delete filter during cleanup: %s", err)
        if self.enabled_types:
            try:
                self.inspector_wrapper.disable(self.enabled_types)
                print("Cleanup: Inspector disabled.")
            except ClientError as err:
                logger.warning("Failed to disable Inspector during cleanup: %s", err)

    # ------------------------------------------------------------------
    # Run the scenario
    # ------------------------------------------------------------------
    def run_scenario(self) -> None:
        """Runs the Amazon Inspector basics scenario."""
        print(DASHES)
        print("Amazon Inspector Basics Scenario")
        print(DASHES)
        print(
            "Amazon Inspector is a security assessment service that helps improve\n"
            "the security and compliance of applications deployed on AWS. It\n"
            "automatically assesses workloads for software vulnerabilities and\n"
            "unintended network exposure."
        )
        self._wait_for_input()

        try:
            self.setup()
            self._wait_for_input()
            self.assess_coverage()
            self._wait_for_input()
            self.manage_findings()
            self._wait_for_input()
            self.check_usage_and_costs()
            self._wait_for_input()
            self.cleanup()
        except ClientError:
            logger.exception("The scenario encountered an error.")
            self._cleanup_after_error()
            raise

        print(DASHES)
        print("Scenario completed successfully!")
        print("What you learned:")
        print("  - How to check Inspector account status")
        print("  - How to enable Inspector")
        print("  - How to list and analyze findings")
        print("  - How to check coverage information")
        print("  - How to create and manage filters")
        print("  - How to track usage and costs")
        print("  - How to clean up resources")
        print(DASHES)


# snippet-end:[python.example_code.inspector2.Inspector2Scenario]


def main() -> None:
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    scenario = InspectorScenario(Inspector2Wrapper.from_client())
    try:
        scenario.run_scenario()
    except ClientError:
        sys.exit(1)


if __name__ == "__main__":
    main()
