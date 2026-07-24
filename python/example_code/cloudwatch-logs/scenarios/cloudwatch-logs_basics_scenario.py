# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon CloudWatch Logs Basics Scenario

This scenario demonstrates a complete CloudWatch Logs management workflow:
1. Create a log group and log stream.
2. Ingest sample log events.
3. Retrieve log events from the stream.
4. Filter log events using patterns.
5. Create and verify a metric filter.
6. Configure and verify a retention policy.
7. Clean up all resources.

This file is fully self-contained and does NOT depend on demo_tools or any
external modules not available via pip install.
"""

import logging
import time
from datetime import datetime, timezone

import boto3

from cloudwatch_logs_wrapper import CloudWatchLogsWrapper

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cloudwatch-logs.CloudWatchLogsScenario]
class CloudWatchLogsScenario:
    """Runs an interactive scenario demonstrating CloudWatch Logs basics."""

    def __init__(self, wrapper: CloudWatchLogsWrapper):
        """
        Initializes the scenario with a CloudWatchLogsWrapper instance.

        :param wrapper: A CloudWatchLogsWrapper instance to use for operations.
        """
        self.wrapper = wrapper
        timestamp = int(time.time())
        self.log_group_name = f"sdk-example-logs-{timestamp}"
        self.log_stream_name = "application-stream-1"
        self.filter_name = "ErrorCount"
        self.metric_name = "ApplicationErrors"
        self.metric_namespace = "SDKExampleApp"

    def run_scenario(self) -> None:
        """
        Runs the full CloudWatch Logs basics scenario.
        """
        print("\n" + "=" * 80)
        print("Welcome to the Amazon CloudWatch Logs Basics Scenario!")
        print("=" * 80)

        try:
            self._setup()
            self._ingest_log_events()
            self._retrieve_log_events()
            self._filter_log_events()
            self._create_metric_filter()
            self._configure_retention_policy()
        finally:
            self._cleanup()

        print("\n" + "=" * 80)
        print("CloudWatch Logs Basics Scenario complete!")
        print("=" * 80)

    def _setup(self) -> None:
        """Sets up CloudWatch Logs resources."""
        print("\n" + "-" * 80)
        print("Setting up CloudWatch Logs resources...")
        print("-" * 80)

        self.wrapper.create_log_group(
            self.log_group_name,
            tags={"Environment": "SDK-Example", "Project": "CloudWatchLogs-Basics"},
        )
        print(f"Created log group: {self.log_group_name}")

        self.wrapper.create_log_stream(self.log_group_name, self.log_stream_name)
        print(f"Created log stream: {self.log_stream_name}")

        print("Setup complete.")
        print("-" * 80)

    def _ingest_log_events(self) -> None:
        """Generates and uploads sample log events."""
        print("\n" + "-" * 80)
        print("Ingesting log events...")
        print("-" * 80)

        now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)
        log_events = [
            {"timestamp": now_ms, "message": "INFO: Application started successfully"},
            {"timestamp": now_ms + 3000, "message": "INFO: Database connection pool initialized (size=10)"},
            {"timestamp": now_ms + 6000, "message": "INFO: Request processed in 45ms"},
            {"timestamp": now_ms + 9000, "message": "WARN: High memory usage detected: 85%"},
            {"timestamp": now_ms + 12000, "message": "ERROR: Connection timeout to database server"},
            {"timestamp": now_ms + 15000, "message": "INFO: Retrying database connection (attempt 2/3)"},
            {"timestamp": now_ms + 18000, "message": "INFO: Database connection re-established"},
            {"timestamp": now_ms + 21000, "message": "INFO: Request processed in 120ms"},
            {"timestamp": now_ms + 24000, "message": "ERROR: Failed to process request - NullPointerException"},
            {"timestamp": now_ms + 27000, "message": "INFO: Health check passed - all systems operational"},
        ]

        print(f"Uploading {len(log_events)} log events to stream '{self.log_stream_name}'...")
        self.wrapper.put_log_events(
            self.log_group_name, self.log_stream_name, log_events
        )
        print(f"Successfully uploaded {len(log_events)} log events.")

        print("Waiting for log ingestion to complete...")
        time.sleep(3)
        print("-" * 80)

    def _retrieve_log_events(self) -> None:
        """Retrieves and displays log events from the stream."""
        print("\n" + "-" * 80)
        print("Retrieving log events from stream...")
        print("-" * 80)

        events = self.wrapper.get_log_events(
            self.log_group_name, self.log_stream_name, start_from_head=True
        )
        print(f"Retrieved {len(events)} log events:")
        for event in events:
            ts = datetime.fromtimestamp(
                event["timestamp"] / 1000, tz=timezone.utc
            ).strftime("%Y-%m-%d %H:%M:%S")
            print(f"  [{ts}] {event['message']}")
        print(f"Total events retrieved: {len(events)}")
        print("-" * 80)

    def _filter_log_events(self) -> None:
        """Filters log events using patterns."""
        print("\n" + "-" * 80)
        print("Filtering log events...")
        print("-" * 80)

        # Filter for ERROR events
        error_pattern = "ERROR"
        print(f'Searching for ERROR events with pattern: "{error_pattern}"')
        error_events = self.wrapper.filter_log_events(
            self.log_group_name, error_pattern
        )
        print(f"Found {len(error_events)} matching events:")
        for event in error_events:
            ts = datetime.fromtimestamp(
                event["timestamp"] / 1000, tz=timezone.utc
            ).strftime("%Y-%m-%d %H:%M:%S")
            print(f"  [{ts}] {event['message']}")

        # Filter for ERROR or WARN events
        warn_error_pattern = "?ERROR ?WARN"
        print(f'\nSearching for ERROR or WARN events with pattern: "{warn_error_pattern}"')
        warn_error_events = self.wrapper.filter_log_events(
            self.log_group_name, warn_error_pattern
        )
        print(f"Found {len(warn_error_events)} matching events:")
        for event in warn_error_events:
            ts = datetime.fromtimestamp(
                event["timestamp"] / 1000, tz=timezone.utc
            ).strftime("%Y-%m-%d %H:%M:%S")
            print(f"  [{ts}] {event['message']}")
        print("-" * 80)

    def _create_metric_filter(self) -> None:
        """Creates and verifies a metric filter."""
        print("\n" + "-" * 80)
        print("Creating and verifying metric filter...")
        print("-" * 80)

        self.wrapper.put_metric_filter(
            log_group_name=self.log_group_name,
            filter_name=self.filter_name,
            filter_pattern="ERROR",
            metric_name=self.metric_name,
            metric_namespace=self.metric_namespace,
            metric_value="1",
            default_value=0,
        )
        print(f"Created metric filter '{self.filter_name}' on log group '{self.log_group_name}'")
        print(f"  Filter pattern: ERROR")
        print(f"  Metric namespace: {self.metric_namespace}")
        print(f"  Metric name: {self.metric_name}")

        print("\nVerifying metric filter configuration...")
        filters = self.wrapper.describe_metric_filters(
            self.log_group_name, filter_name_prefix=self.filter_name
        )
        print(f"Found {len(filters)} metric filter(s):")
        for f in filters:
            print(f"  Name: {f['filterName']}")
            print(f"  Pattern: {f['filterPattern']}")
            transformations = f.get("metricTransformations", list())
            for t in transformations:
                print(f"  Metric: {t['metricNamespace']}/{t['metricName']}")
                print(f"  Default Value: {t.get('defaultValue', 'N/A')}")
        print("Metric filter verified successfully.")
        print("-" * 80)

    def _configure_retention_policy(self) -> None:
        """Configures and verifies a retention policy."""
        print("\n" + "-" * 80)
        print("Configuring retention policy...")
        print("-" * 80)

        retention_days = 7
        self.wrapper.put_retention_policy(self.log_group_name, retention_days)
        print(f"Set retention policy to {retention_days} days for log group '{self.log_group_name}'")

        print("\nVerifying log group configuration...")
        log_groups = self.wrapper.describe_log_groups(
            log_group_name_prefix=self.log_group_name
        )
        for group in log_groups:
            if group.get("logGroupName") == self.log_group_name:
                print("Log Group Details:")
                print(f"  Name: {group['logGroupName']}")
                print(f"  ARN: {group.get('arn', 'N/A')}")
                retention = group.get("retentionInDays", None)
                retention_display = f"{retention} days" if retention else "Never expire"
                print(f"  Retention: {retention_display}")
                print(f"  Stored Bytes: {group.get('storedBytes', 0)}")
                creation_time = group.get("creationTime", None)
                if creation_time:
                    ct = datetime.fromtimestamp(
                        creation_time / 1000, tz=timezone.utc
                    ).strftime("%Y-%m-%d %H:%M:%S")
                    print(f"  Creation Time: {ct}")
        print("Retention policy verified successfully.")
        print("-" * 80)

    def _cleanup(self) -> None:
        """Cleans up all resources created during the scenario."""
        print("\n" + "-" * 80)
        print("Cleaning up resources...")
        print("-" * 80)

        print(f"Deleting log group '{self.log_group_name}'...")
        self.wrapper.delete_log_group(self.log_group_name)
        print("Log group deleted successfully.")
        print("All resources cleaned up.")
        print("-" * 80)


# snippet-end:[python.example_code.cloudwatch-logs.CloudWatchLogsScenario]


def main():
    """Entry point for running the CloudWatch Logs basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    wrapper = CloudWatchLogsWrapper.from_client()
    scenario = CloudWatchLogsScenario(wrapper)
    scenario.run_scenario()


if __name__ == "__main__":
    main()
