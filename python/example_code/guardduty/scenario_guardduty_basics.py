# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage Amazon GuardDuty
detectors and findings. This scenario demonstrates:

1. Creating a GuardDuty detector to enable threat detection
2. Generating sample findings for demonstration
3. Listing and examining findings
4. Cleaning up resources

This example runs interactively and uses the demo_tools module for user input.
"""

import logging
import os
import sys
import time
from typing import Optional

import boto3
from botocore.exceptions import ClientError

from guardduty_wrapper import GuardDutyWrapper

# Add relative path to include demo_tools
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "../.."))
from demo_tools import question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.guardduty.GuardDutyScenario]
class GuardDutyScenario:
    """Runs an interactive scenario that shows how to use Amazon GuardDuty."""

    def __init__(self, guardduty_wrapper: GuardDutyWrapper):
        """
        :param guardduty_wrapper: An instance of the GuardDutyWrapper class.
        """
        self.guardduty_wrapper = guardduty_wrapper
        self.detector_id = None

    def run_scenario(self):
        """Runs the GuardDuty basics scenario."""
        print("-" * 88)
        print("Welcome to the Amazon GuardDuty basics scenario!")
        print("-" * 88)

        print(
            "Amazon GuardDuty is a threat detection service that continuously monitors "
            "for malicious activity and unauthorized behavior to protect your AWS accounts "
            "and workloads."
        )
        print()

        try:
            self._setup_phase()
            self._demonstration_phase()
            self._examination_phase()
        except Exception as e:
            logger.error(f"Scenario failed: {e}")
            print(f"The scenario encountered an error: {e}")
        finally:
            self._cleanup_phase()

    def _setup_phase(self):
        """Setup phase: Create or use existing GuardDuty detector."""
        print("=" * 60)
        print("Setup: Creating GuardDuty detector")
        print("=" * 60)

        # Check for existing detectors
        existing_detectors = self.guardduty_wrapper.list_detectors()

        if existing_detectors:
            print(f"Found {len(existing_detectors)} existing detector(s):")
            for detector_id in existing_detectors:
                detector_info = self.guardduty_wrapper.get_detector(detector_id)
                status = detector_info.get("Status", "Unknown")
                print(f"  - {detector_id} (Status: {status})")

            if q.ask("Do you want to use an existing detector? (y/n): ", q.is_yesno):
                self.detector_id = existing_detectors[0]
                print(f"Using existing detector: {self.detector_id}")
            else:
                self.detector_id = self._create_new_detector()
        else:
            print("No existing detectors found. Creating a new one...")
            self.detector_id = self._create_new_detector()

        # Display detector information
        detector_info = self.guardduty_wrapper.get_detector(self.detector_id)
        print(f"\nDetector Details:")
        print(f"  ID: {self.detector_id}")
        print(f"  Status: {detector_info.get('Status', 'Unknown')}")
        print(f"  Service Role: {detector_info.get('ServiceRole', 'Not specified')}")
        print(
            f"  Finding Publishing Frequency: {detector_info.get('FindingPublishingFrequency', 'Unknown')}"
        )

    def _create_new_detector(self) -> str:
        """Create a new GuardDuty detector."""
        print("Creating a new GuardDuty detector...")

        # Ask user for finding publishing frequency
        frequencies = {"1": "FIFTEEN_MINUTES", "2": "ONE_HOUR", "3": "SIX_HOURS"}

        print("Choose finding publishing frequency:")
        print("1. Every 15 minutes")
        print("2. Every hour")
        print("3. Every 6 hours")

        choice = q.ask("Enter your choice (1-3): ", q.is_int, q.in_range(1, 3))
        frequency = frequencies[str(choice)]

        detector_id = self.guardduty_wrapper.create_detector(
            enable=True, finding_publishing_frequency=frequency
        )

        print(f"Successfully created detector: {detector_id}")
        return detector_id

    def _demonstration_phase(self):
        """Demonstration phase: Generate and explore sample findings."""
        print("\n" + "=" * 60)
        print("Demonstration: Working with GuardDuty findings")
        print("=" * 60)

        print(
            "GuardDuty generates findings when it detects potential security threats. "
            "Let's create some sample findings to explore GuardDuty's capabilities."
        )

        if q.ask("Create sample findings for demonstration? (y/n): ", q.is_yesno):
            print("Creating sample findings...")
            self.guardduty_wrapper.create_sample_findings(self.detector_id)

            print(
                "Sample findings created! Waiting a moment for them to be processed..."
            )
            time.sleep(5)  # Give time for findings to be created

            # List findings
            finding_ids = self.guardduty_wrapper.list_findings(self.detector_id)

            if finding_ids:
                print(f"Found {len(finding_ids)} findings.")

                if q.ask(
                    "Would you like to examine the findings in detail? (y/n): ",
                    q.is_yesno,
                ):
                    self._examine_findings(finding_ids[:5])  # Examine first 5 findings
            else:
                print("No findings found yet. They may take a few moments to appear.")

    def _examination_phase(self):
        """Examination phase: Explore findings in detail."""
        print("\n" + "=" * 60)
        print("Examination: Detailed finding analysis")
        print("=" * 60)

        finding_ids = self.guardduty_wrapper.list_findings(self.detector_id)

        if not finding_ids:
            print("No findings available for examination.")
            return

        print(f"Total findings available: {len(finding_ids)}")

        if q.ask("Would you like to examine findings by severity? (y/n): ", q.is_yesno):
            self._examine_findings_by_severity(finding_ids)

    def _examine_findings(self, finding_ids):
        """Examine specific findings in detail."""
        if not finding_ids:
            print("No findings to examine.")
            return

        findings = self.guardduty_wrapper.get_findings(self.detector_id, finding_ids)

        for i, finding in enumerate(findings, 1):
            print(f"\n--- Finding {i} ---")
            print(f"ID: {finding.get('Id', 'Unknown')}")
            print(f"Type: {finding.get('Type', 'Unknown')}")
            print(f"Severity: {finding.get('Severity', 'Unknown')}")
            print(f"Title: {finding.get('Title', 'No title')}")
            print(f"Description: {finding.get('Description', 'No description')}")

            # Show service information if available
            service_info = finding.get("Service", {})
            if service_info:
                print(f"Service: {service_info.get('ServiceName', 'Unknown')}")
                print(f"Detector ID: {service_info.get('DetectorId', 'Unknown')}")

            if i < len(findings) and q.ask(
                "Continue to next finding? (y/n): ", q.is_yesno
            ):
                continue
            else:
                break

    def _examine_findings_by_severity(self, finding_ids):
        """Group and examine findings by severity level."""
        findings = self.guardduty_wrapper.get_findings(self.detector_id, finding_ids)

        # Group findings by severity
        severity_groups = {}
        for finding in findings:
            severity = finding.get("Severity", 0)
            if severity >= 7.0:
                level = "HIGH"
            elif severity >= 4.0:
                level = "MEDIUM"
            else:
                level = "LOW"

            if level not in severity_groups:
                severity_groups[level] = []
            severity_groups[level].append(finding)

        # Display findings by severity
        for level in ["HIGH", "MEDIUM", "LOW"]:
            if level in severity_groups:
                findings_in_level = severity_groups[level]
                print(f"\n{level} Severity Findings ({len(findings_in_level)}):")

                for finding in findings_in_level:
                    print(
                        f"  - {finding.get('Type', 'Unknown')} (Score: {finding.get('Severity', 'Unknown')})"
                    )
                    print(f"    {finding.get('Title', 'No title')}")

    def _cleanup_phase(self):
        """Cleanup phase: Optionally delete the detector."""
        print("\n" + "=" * 60)
        print("Cleanup: Managing GuardDuty resources")
        print("=" * 60)

        if not self.detector_id:
            print("No detector to clean up.")
            return

        print(
            "GuardDuty detectors continue to monitor your account and may incur charges. "
            "You can disable the detector to stop monitoring, or delete it entirely."
        )

        cleanup_choice = q.ask(
            "What would you like to do?\n"
            "1. Keep the detector running\n"
            "2. Delete the detector\n"
            "Enter your choice (1-2): ",
            q.is_int,
            q.in_range(1, 2),
        )

        if cleanup_choice == 2:
            if q.ask(
                f"Are you sure you want to delete detector {self.detector_id}? (y/n): ",
                q.is_yesno,
            ):
                try:
                    self.guardduty_wrapper.delete_detector(self.detector_id)
                    print(f"Detector {self.detector_id} has been deleted.")
                except Exception as e:
                    print(f"Error deleting detector: {e}")
            else:
                print("Detector deletion cancelled.")
        else:
            print(f"Detector {self.detector_id} will continue running.")
            print("You can manage it through the AWS Console or CLI.")


# snippet-end:[python.example_code.guardduty.GuardDutyScenario]


def main():
    """Runs the GuardDuty basics scenario."""
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")

    try:
        guardduty_wrapper = GuardDutyWrapper.from_client()
        scenario = GuardDutyScenario(guardduty_wrapper)
        scenario.run_scenario()
    except Exception as e:
        logger.error(f"Failed to run scenario: {e}")
        print(f"Failed to run the scenario: {e}")


if __name__ == "__main__":
    main()
