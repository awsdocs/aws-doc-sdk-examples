# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use Amazon Inspector to manage vulnerability assessments and findings.
This scenario demonstrates:
1. Enabling Amazon Inspector for your account
2. Checking account status and enabled scan types
3. Listing coverage statistics for resources
4. Managing security findings and vulnerability analysis
5. Optionally disabling Inspector scanning

Amazon Inspector is a vulnerability management service that continuously scans
AWS workloads for software vulnerabilities and unintended network exposure.
"""

import logging
import os
import sys
from typing import Optional

import boto3
from botocore.exceptions import ClientError

from inspector_wrapper import InspectorWrapper

# Add relative path to include demo_tools
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "../.."))
from demo_tools import question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.inspector.InspectorScenario]
class InspectorScenario:
    """Runs an interactive scenario that shows how to use Amazon Inspector."""

    def __init__(self, inspector_wrapper: InspectorWrapper):
        """
        :param inspector_wrapper: An instance of the InspectorWrapper class.
        """
        self.inspector_wrapper = inspector_wrapper

    def run_scenario(self):
        """Runs the Amazon Inspector basics scenario."""
        print("-" * 88)
        print("Welcome to the Amazon Inspector basics scenario!")
        print("-" * 88)

        print(
            "Amazon Inspector is a vulnerability management service that continuously scans\n"
            "AWS workloads for software vulnerabilities and unintended network exposure.\n"
            "This scenario will walk you through the basic operations of Amazon Inspector.\n"
        )

        try:
            self._setup_phase()
            self._coverage_assessment_phase()
            self._findings_management_phase()
            self._vulnerability_analysis_phase()
        except Exception as e:
            logger.error(f"Scenario failed: {e}")
        finally:
            self._cleanup_phase()

    def _setup_phase(self):
        """Setup phase: Enable Inspector and verify activation."""
        print("-" * 88)
        print("Setup Phase: Enabling Amazon Inspector")
        print("-" * 88)

        # Check current account status first
        print("Checking current Amazon Inspector status...")
        try:
            status_response = self.inspector_wrapper.get_account_status()
            if status_response.get("accounts"):
                account = status_response["accounts"][0]
                current_status = account.get("state", {}).get("status", "UNKNOWN")
                print(f"Current Inspector status: {current_status}")

                if current_status == "ENABLED":
                    print("Amazon Inspector is already enabled for your account.")
                    self._display_account_status(account)
                    return
        except ClientError as e:
            print(f"Could not check current status: {e}")

        # Enable Inspector if not already enabled
        enable_inspector = q.ask(
            "Would you like to enable Amazon Inspector for your account? (y/n): ",
            q.is_yesno,
        )

        if enable_inspector:
            print("Enabling Amazon Inspector...")
            try:
                # Enable for all resource types by default
                resource_types = ["EC2", "ECR", "LAMBDA"]
                response = self.inspector_wrapper.enable_inspector(
                    resource_types=resource_types
                )

                print("Amazon Inspector has been enabled successfully!")

                # Display the results
                if "accounts" in response:
                    for account in response["accounts"]:
                        account_id = account.get("accountId", "Unknown")
                        status = account.get("status", "Unknown")
                        print(f"Account {account_id}: {status}")

                        if "resourceStatus" in account:
                            print("Resource scanning status:")
                            for resource_type, resource_status in account[
                                "resourceStatus"
                            ].items():
                                print(f"  {resource_type}: {resource_status}")

            except ClientError as e:
                error_code = e.response["Error"]["Code"]
                if error_code == "ValidationException":
                    print(
                        "Validation error: Please check your account permissions and resource types."
                    )
                elif error_code == "AccessDeniedException":
                    print(
                        "Access denied: You don't have permission to enable Amazon Inspector."
                    )
                else:
                    print(f"Error enabling Inspector: {e}")
                raise
        else:
            print(
                "Skipping Inspector enablement. Some operations may not work without enabling Inspector."
            )

        # Verify Inspector is activated
        print("\nVerifying Amazon Inspector activation...")
        try:
            status_response = self.inspector_wrapper.get_account_status()
            if status_response.get("accounts"):
                account = status_response["accounts"][0]
                self._display_account_status(account)
        except ClientError as e:
            print(f"Error verifying Inspector status: {e}")

    def _coverage_assessment_phase(self):
        """Coverage assessment phase: List coverage statistics."""
        print("-" * 88)
        print("Coverage Assessment Phase")
        print("-" * 88)

        print("Listing coverage statistics for your AWS resources...")

        try:
            # List coverage for all resource types
            coverage_response = self.inspector_wrapper.list_coverage(max_results=10)

            covered_resources = coverage_response.get("coveredResources", [])
            if covered_resources:
                print(f"Found {len(covered_resources)} covered resources:")

                # Group resources by type
                resource_types = {}
                for resource in covered_resources:
                    resource_type = resource.get("resourceType", "Unknown")
                    if resource_type not in resource_types:
                        resource_types[resource_type] = []
                    resource_types[resource_type].append(resource)

                # Display coverage by resource type
                for resource_type, resources in resource_types.items():
                    print(f"\n{resource_type} Resources ({len(resources)}):")
                    for resource in resources[
                        :3
                    ]:  # Show first 3 resources of each type
                        resource_id = resource.get("resourceId", "Unknown")
                        scan_status = resource.get("scanStatus", {}).get(
                            "statusCode", "Unknown"
                        )
                        last_scanned = resource.get("lastScannedAt", "Never")
                        print(f"  Resource: {resource_id}")
                        print(f"    Status: {scan_status}")
                        print(f"    Last Scanned: {last_scanned}")

                    if len(resources) > 3:
                        print(f"    ... and {len(resources) - 3} more resources")
            else:
                print("No covered resources found. This might be because:")
                print(
                    "- Inspector was recently enabled and hasn't scanned resources yet"
                )
                print(
                    "- No supported resources (EC2, ECR, Lambda) exist in your account"
                )
                print("- Resources are still being discovered")

        except ClientError as e:
            print(f"Error listing coverage: {e}")

    def _findings_management_phase(self):
        """Findings management phase: List and filter security findings."""
        print("-" * 88)
        print("Findings Management Phase")
        print("-" * 88)

        print("Listing security findings from Amazon Inspector...")

        try:
            # List findings with basic filtering
            findings_response = self.inspector_wrapper.list_findings(max_results=10)

            findings = findings_response.get("findings", [])
            if findings:
                print(f"Found {len(findings)} security findings:")

                # Group findings by severity
                severity_groups = {}
                for finding in findings:
                    severity = finding.get("severity", "UNKNOWN")
                    if severity not in severity_groups:
                        severity_groups[severity] = []
                    severity_groups[severity].append(finding)

                # Display findings by severity
                severity_order = [
                    "CRITICAL",
                    "HIGH",
                    "MEDIUM",
                    "LOW",
                    "INFORMATIONAL",
                    "UNKNOWN",
                ]
                for severity in severity_order:
                    if severity in severity_groups:
                        findings_list = severity_groups[severity]
                        print(f"\n{severity} Severity Findings ({len(findings_list)}):")

                        for finding in findings_list[
                            :2
                        ]:  # Show first 2 findings per severity
                            title = finding.get("title", "No title")
                            finding_type = finding.get("type", "Unknown")
                            inspector_score = finding.get("inspectorScore", "N/A")
                            print(f"  • {title}")
                            print(f"    Type: {finding_type}")
                            print(f"    Inspector Score: {inspector_score}")

                            # Show affected resources
                            resources = finding.get("resources", [])
                            if resources:
                                resource = resources[0]  # Show first resource
                                resource_id = resource.get("id", "Unknown")
                                resource_type = resource.get("type", "Unknown")
                                print(
                                    f"    Affected Resource: {resource_id} ({resource_type})"
                                )

                        if len(findings_list) > 2:
                            print(
                                f"    ... and {len(findings_list) - 2} more {severity.lower()} findings"
                            )

                # Ask if user wants to see detailed information for a finding
                show_details = q.ask(
                    "Would you like to see detailed information for a finding? (y/n): ",
                    q.is_yesno,
                )

                if show_details and findings:
                    self._show_finding_details(
                        findings[0]
                    )  # Show details for first finding

            else:
                print("No security findings found. This might be because:")
                print(
                    "- Inspector was recently enabled and hasn't completed scanning yet"
                )
                print("- No vulnerabilities were detected in your resources")
                print("- Resources are still being scanned")

        except ClientError as e:
            print(f"Error listing findings: {e}")

    def _vulnerability_analysis_phase(self):
        """Vulnerability analysis phase: Display vulnerability details and remediation guidance."""
        print("-" * 88)
        print("Vulnerability Analysis Phase")
        print("-" * 88)

        print("Analyzing vulnerabilities by resource type...")

        # Filter findings by resource type
        resource_types = [
            "AWS_EC2_INSTANCE",
            "AWS_ECR_CONTAINER_IMAGE",
            "AWS_LAMBDA_FUNCTION",
        ]

        for resource_type in resource_types:
            print(f"\nAnalyzing {resource_type} vulnerabilities...")

            try:
                # Create filter for specific resource type
                filter_criteria = {
                    "resourceType": [{"comparison": "EQUALS", "value": resource_type}]
                }

                findings_response = self.inspector_wrapper.list_findings(
                    filter_criteria=filter_criteria, max_results=5
                )

                findings = findings_response.get("findings", [])
                if findings:
                    print(f"  Found {len(findings)} findings for {resource_type}")

                    for finding in findings[:2]:  # Show first 2 findings
                        title = finding.get("title", "No title")
                        severity = finding.get("severity", "Unknown")
                        print(f"    • {title} ({severity})")

                        # Show vulnerability details if available
                        vuln_details = finding.get("packageVulnerabilityDetails")
                        if vuln_details:
                            vuln_id = vuln_details.get("vulnerabilityId", "Unknown")
                            source = vuln_details.get("source", "Unknown")
                            print(f"      Vulnerability ID: {vuln_id}")
                            print(f"      Source: {source}")

                            # Show vulnerable packages
                            vulnerable_packages = vuln_details.get(
                                "vulnerablePackages", []
                            )
                            if vulnerable_packages:
                                package = vulnerable_packages[0]
                                package_name = package.get("name", "Unknown")
                                current_version = package.get("version", "Unknown")
                                fixed_version = package.get(
                                    "fixedInVersion", "Not available"
                                )
                                print(
                                    f"      Package: {package_name} (v{current_version})"
                                )
                                print(f"      Fixed in: {fixed_version}")
                else:
                    print(f"  No findings for {resource_type}")

            except ClientError as e:
                print(f"  Error analyzing {resource_type}: {e}")

    def _show_finding_details(self, finding):
        """Show detailed information for a specific finding."""
        finding_arn = finding.get("findingArn")
        if not finding_arn:
            print("Cannot show details: Finding ARN not available")
            return

        print(f"\nDetailed information for finding:")
        print(f"ARN: {finding_arn}")

        try:
            details_response = self.inspector_wrapper.get_finding_details([finding_arn])
            finding_details = details_response.get("findingDetails", [])

            if finding_details:
                details = finding_details[0]

                # Show CISA data if available
                cisa_data = details.get("cisaData")
                if cisa_data:
                    print("CISA Information:")
                    print(f"  Action: {cisa_data.get('action', 'N/A')}")
                    print(f"  Date Added: {cisa_data.get('dateAdded', 'N/A')}")

                # Show CWE information
                cwes = details.get("cwes", [])
                if cwes:
                    print(f"CWE IDs: {', '.join(cwes)}")

                # Show EPSS score
                epss_score = details.get("epssScore")
                if epss_score:
                    print(f"EPSS Score: {epss_score}")

                # Show reference URLs
                reference_urls = details.get("referenceUrls", [])
                if reference_urls:
                    print("Reference URLs:")
                    for url in reference_urls[:3]:  # Show first 3 URLs
                        print(f"  • {url}")

        except ClientError as e:
            print(f"Error getting finding details: {e}")

    def _display_account_status(self, account):
        """Display account status information."""
        account_id = account.get("accountId", "Unknown")
        print(f"\nAccount ID: {account_id}")

        # Display overall status
        if "state" in account:
            status = account["state"].get("status", "Unknown")
            print(f"Inspector Status: {status}")

        # Display resource-specific status
        if "resourceState" in account:
            resource_state = account["resourceState"]
            print("Resource Scanning Status:")

            for resource_type, state in resource_state.items():
                resource_status = state.get("status", "Unknown")
                print(f"  {resource_type.upper()}: {resource_status}")

    def _cleanup_phase(self):
        """Cleanup phase: Optionally disable Inspector scanning."""
        print("-" * 88)
        print("Cleanup Phase")
        print("-" * 88)

        disable_inspector = q.ask(
            "Would you like to disable Amazon Inspector for your account? (y/n): ",
            q.is_yesno,
        )

        if disable_inspector:
            print(
                "WARNING: Disabling Amazon Inspector will stop vulnerability scanning for your resources."
            )
            confirm_disable = q.ask(
                "Are you sure you want to disable Inspector? (y/n): ", q.is_yesno
            )

            if confirm_disable:
                try:
                    # Disable all resource types
                    resource_types = ["EC2", "ECR", "LAMBDA"]
                    response = self.inspector_wrapper.disable_inspector(
                        resource_types=resource_types
                    )

                    print("Amazon Inspector has been disabled.")

                    # Display the results
                    if "accounts" in response:
                        for account in response["accounts"]:
                            account_id = account.get("accountId", "Unknown")
                            status = account.get("status", "Unknown")
                            print(f"Account {account_id}: {status}")

                except ClientError as e:
                    error_code = e.response["Error"]["Code"]
                    if error_code == "ConflictException":
                        print(
                            "Cannot disable Inspector: There may be active scans or other conflicts."
                        )
                    else:
                        print(f"Error disabling Inspector: {e}")
            else:
                print("Inspector remains enabled.")
        else:
            print("Inspector remains enabled.")

        # Show final status
        print("\nFinal Amazon Inspector status:")
        try:
            status_response = self.inspector_wrapper.get_account_status()
            if status_response.get("accounts"):
                account = status_response["accounts"][0]
                self._display_account_status(account)
        except ClientError as e:
            print(f"Error getting final status: {e}")

        print("-" * 88)
        print("Thanks for using the Amazon Inspector basics scenario!")
        print("-" * 88)


# snippet-end:[python.example_code.inspector.InspectorScenario]


def main():
    """Runs the Amazon Inspector basics scenario."""
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")

    try:
        inspector_wrapper = InspectorWrapper.from_client()
        scenario = InspectorScenario(inspector_wrapper)
        scenario.run_scenario()
    except Exception as e:
        logger.error(f"Failed to run scenario: {e}")


if __name__ == "__main__":
    main()
