# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use Amazon CloudWatch to monitor infrastructure metrics and send 
alerts via Amazon SNS when thresholds are breached.

This scenario demonstrates:
1. Creating an SNS topic and email subscription for alarm notifications
2. Creating a CloudWatch alarm that monitors a custom metric
3. Publishing metric data to trigger alarm state changes
4. Retrieving alarm history to view state transitions
5. Cleaning up all created resources
"""

import logging
import sys
import time
from datetime import datetime, timezone

import boto3
from botocore.exceptions import ClientError

sys.path.append("../..")
from demo_tools import question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sns.Scenario_CloudWatchSnsMonitoring]
class CloudWatchSnsMonitoringScenario:
    """Runs an interactive scenario demonstrating CloudWatch monitoring with SNS alerts."""

    def __init__(self, sns_resource, cloudwatch_client):
        """
        :param sns_resource: A Boto3 SNS resource.
        :param cloudwatch_client: A Boto3 CloudWatch client.
        """
        self.sns_resource = sns_resource
        self.cloudwatch_client = cloudwatch_client
        self.topic = None
        self.subscription = None
        self.alarm_name = None
        self.namespace = None
        self.metric_name = "ErrorCount"
        self.dashboard_name = None

    def run_scenario(self):
        """Runs the CloudWatch SNS monitoring scenario."""
        print("-" * 88)
        print("Welcome to the CloudWatch Monitoring with SNS Alerts Scenario.")
        print("-" * 88)
        print(
            "This scenario demonstrates how to:\n"
            "1. Create an SNS topic for alarm notifications\n"
            "2. Create a CloudWatch alarm that monitors a custom metric\n"
            "3. Publish metric data to trigger the alarm\n"
            "4. Receive email notifications when the alarm state changes\n"
        )

        try:
            self._setup_phase()
            self._publish_metrics_phase()
            self._demonstrate_alarm_phase()
        except Exception as e:
            logger.exception("Scenario failed: %s", e)
            print(f"\nThe scenario encountered an error: {e}")
        finally:
            self._cleanup_phase()

    def _setup_phase(self):
        """Setup phase: Create SNS topic, subscription, alarm, and dashboard."""
        # snippet-start:[python.example_code.sns.CreateTopicForAlarm]
        print("\n" + "-" * 88)
        print("Setup Phase")
        print("-" * 88)

        email = q.ask("Enter an email address to receive alarm notifications: ", q.non_empty)

        print("\nCreating SNS topic...")
        self.topic = self.sns_resource.create_topic(Name="cloudwatch-alarms-topic")
        print(f"SNS topic created: {self.topic.arn}")

        print("Creating email subscription...")
        self.subscription = self.topic.subscribe(Protocol="email", Endpoint=email)
        print("Email subscription created. Please check your email and confirm the subscription.")
        # snippet-end:[python.example_code.sns.CreateTopicForAlarm]

        # snippet-start:[python.example_code.cloudwatch.PutMetricAlarmWithSns]
        self.alarm_name = q.ask("\nEnter a name for the CloudWatch alarm: ", q.non_empty)

        namespace_input = input("Enter a name for the custom metric namespace (default: CustomApp/Monitoring): ")
        self.namespace = namespace_input.strip() if namespace_input.strip() else "CustomApp/Monitoring"

        print(f"\nCreating CloudWatch alarm '{self.alarm_name}'...")
        print(f"Alarm will trigger when {self.metric_name} >= 10 for 1 evaluation period (1 minute).")
        
        self.cloudwatch_client.put_metric_alarm(
            AlarmName=self.alarm_name,
            ComparisonOperator="GreaterThanOrEqualToThreshold",
            EvaluationPeriods=1,
            MetricName=self.metric_name,
            Namespace=self.namespace,
            Period=60,
            Statistic="Average",
            Threshold=10.0,
            ActionsEnabled=True,
            AlarmActions=[self.topic.arn],
            AlarmDescription="Alarm when error count exceeds threshold",
        )
        print("CloudWatch alarm created successfully.")
        # snippet-end:[python.example_code.cloudwatch.PutMetricAlarmWithSns]

        # snippet-start:[python.example_code.cloudwatch.PutDashboard]
        self.dashboard_name = "monitoring-dashboard"
        print(f"\nCreating CloudWatch dashboard '{self.dashboard_name}'...")
        
        dashboard_body = {
            "widgets": [
                {
                    "type": "metric",
                    "properties": {
                        "metrics": [[self.namespace, self.metric_name]],
                        "period": 60,
                        "stat": "Average",
                        "region": self.cloudwatch_client.meta.region_name,
                        "title": "Error Count Monitoring"
                    }
                }
            ]
        }
        
        import json
        self.cloudwatch_client.put_dashboard(
            DashboardName=self.dashboard_name,
            DashboardBody=json.dumps(dashboard_body)
        )
        
        region = self.cloudwatch_client.meta.region_name
        print(f"Dashboard '{self.dashboard_name}' created successfully.")
        print(f"View at: https://console.aws.amazon.com/cloudwatch/home?region={region}#dashboards:name={self.dashboard_name}")
        print("-" * 88)
        # snippet-end:[python.example_code.cloudwatch.PutDashboard]

    def _publish_metrics_phase(self):
        """Publish metric data to demonstrate alarm triggering."""
        # snippet-start:[python.example_code.cloudwatch.PutMetricDataForAlarm]
        print("\n" + "-" * 88)
        print("Publishing Metric Data")
        print("-" * 88)

        print("Publishing normal metric data (ErrorCount = 5)...")
        self.cloudwatch_client.put_metric_data(
            Namespace=self.namespace,
            MetricData=[
                {
                    "MetricName": self.metric_name,
                    "Value": 5.0,
                    "Unit": "Count",
                    "Timestamp": datetime.now(timezone.utc)
                }
            ]
        )
        print("Metric data published successfully.")
        # snippet-end:[python.example_code.cloudwatch.PutMetricDataForAlarm]

        # snippet-start:[python.example_code.cloudwatch.DescribeAlarmsState]
        time.sleep(5)
        print("\nChecking alarm state...")
        alarms = self.cloudwatch_client.describe_alarms(AlarmNames=[self.alarm_name])
        if alarms["MetricAlarms"]:
            alarm = alarms["MetricAlarms"][0]
            print(f"Current alarm state: {alarm['StateValue']}")
            print(f"Alarm reason: {alarm['StateReason']}")
        # snippet-end:[python.example_code.cloudwatch.DescribeAlarmsState]

        print("\nPublishing high metric data to trigger alarm (ErrorCount = 15)...")
        self.cloudwatch_client.put_metric_data(
            Namespace=self.namespace,
            MetricData=[
                {
                    "MetricName": self.metric_name,
                    "Value": 15.0,
                    "Unit": "Count",
                    "Timestamp": datetime.now(timezone.utc)
                }
            ]
        )
        print("Metric data published successfully.")

        print("\nWaiting for alarm state to change (this may take up to 1 minute)...")
        for _ in range(12):
            time.sleep(5)
            alarms = self.cloudwatch_client.describe_alarms(AlarmNames=[self.alarm_name])
            if alarms["MetricAlarms"]:
                alarm = alarms["MetricAlarms"][0]
                if alarm["StateValue"] == "ALARM":
                    print(f"Alarm state changed to: {alarm['StateValue']}")
                    print(f"Alarm reason: {alarm['StateReason']}")
                    print(f"\nAn email notification has been sent to your email address.")
                    print("Check your email for the alarm notification.")
                    break
        print("-" * 88)

    def _demonstrate_alarm_phase(self):
        """Retrieve and display alarm history."""
        # snippet-start:[python.example_code.cloudwatch.DescribeAlarmHistory]
        print("\n" + "-" * 88)
        print("Alarm History")
        print("-" * 88)

        print("Retrieving alarm history...")
        history = self.cloudwatch_client.describe_alarm_history(
            AlarmName=self.alarm_name,
            HistoryItemType="StateUpdate",
            MaxRecords=5
        )

        if history["AlarmHistoryItems"]:
            print("\nRecent alarm state changes:")
            for i, item in enumerate(history["AlarmHistoryItems"], 1):
                timestamp = item["Timestamp"].strftime("%Y-%m-%dT%H:%M:%SZ")
                print(f"{i}. {timestamp}: {item['HistorySummary']}")
        else:
            print("No alarm history available yet.")
        print("-" * 88)
        # snippet-end:[python.example_code.cloudwatch.DescribeAlarmHistory]

    def _cleanup_phase(self):
        """Cleanup phase: Delete all created resources."""
        # snippet-start:[python.example_code.cloudwatch.DeleteMonitoringResources]
        if not self.alarm_name:
            return

        print("\n" + "-" * 88)
        print("Cleanup")
        print("-" * 88)

        delete_resources = q.ask(
            "Delete all resources created by this scenario? (y/n) ", q.is_yesno
        )

        if delete_resources:
            try:
                if self.dashboard_name:
                    print("\nDeleting CloudWatch dashboard...")
                    self.cloudwatch_client.delete_dashboards(DashboardNames=[self.dashboard_name])
                    print("Dashboard deleted successfully.")

                print("\nDeleting CloudWatch alarm...")
                self.cloudwatch_client.delete_alarms(AlarmNames=[self.alarm_name])
                print("Alarm deleted successfully.")

                if self.subscription:
                    print("\nUnsubscribing from SNS topic...")
                    self.subscription.delete()
                    print("Subscription removed.")

                if self.topic:
                    print("\nDeleting SNS topic...")
                    self.topic.delete()
                    print("SNS topic deleted successfully.")

                print("\nAll resources cleaned up successfully.")
            except ClientError as e:
                print(f"Error during cleanup: {e}")
        else:
            print("\nResources will remain active.")
            print(f"SNS Topic ARN: {self.topic.arn if self.topic else 'N/A'}")
            print(f"Alarm Name: {self.alarm_name}")

        print("-" * 88)
        print("CloudWatch Monitoring with SNS Alerts scenario completed.")
        # snippet-end:[python.example_code.cloudwatch.DeleteMonitoringResources]


# snippet-end:[python.example_code.sns.Scenario_CloudWatchSnsMonitoring]

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    
    scenario = CloudWatchSnsMonitoringScenario(
        boto3.resource("sns"),
        boto3.client("cloudwatch")
    )
    scenario.run_scenario()
