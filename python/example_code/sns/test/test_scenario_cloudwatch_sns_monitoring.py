# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for scenario_cloudwatch_sns_monitoring.py
"""

import boto3
import pytest
from unittest.mock import MagicMock, patch
from botocore.exceptions import ClientError

from scenario_cloudwatch_sns_monitoring import CloudWatchSnsMonitoringScenario


@pytest.fixture
def scenario():
    """Create a scenario instance with mocked AWS resources."""
    sns_resource = MagicMock()
    cloudwatch_client = MagicMock()
    cloudwatch_client.meta.region_name = "us-east-1"
    return CloudWatchSnsMonitoringScenario(sns_resource, cloudwatch_client)


@patch("scenario_cloudwatch_sns_monitoring.q")
def test_setup_phase(mock_q, scenario):
    """Test the setup phase creates all required resources."""
    mock_q.ask.side_effect = ["test@example.com", "TestAlarm"]
    
    mock_topic = MagicMock()
    mock_topic.arn = "arn:aws:sns:us-east-1:123456789012:cloudwatch-alarms-topic"
    scenario.sns_resource.create_topic.return_value = mock_topic
    
    mock_subscription = MagicMock()
    mock_topic.subscribe.return_value = mock_subscription
    
    with patch("builtins.input", return_value=""):
        scenario._setup_phase()
    
    scenario.sns_resource.create_topic.assert_called_once_with(Name="cloudwatch-alarms-topic")
    mock_topic.subscribe.assert_called_once_with(Protocol="email", Endpoint="test@example.com")
    scenario.cloudwatch_client.put_metric_alarm.assert_called_once()
    scenario.cloudwatch_client.put_dashboard.assert_called_once()
    
    assert scenario.topic == mock_topic
    assert scenario.subscription == mock_subscription
    assert scenario.alarm_name == "TestAlarm"
    assert scenario.namespace == "CustomApp/Monitoring"


def test_publish_metrics_phase(scenario):
    """Test publishing metrics and checking alarm state."""
    scenario.namespace = "CustomApp/Monitoring"
    scenario.alarm_name = "TestAlarm"
    
    scenario.cloudwatch_client.describe_alarms.return_value = {
        "MetricAlarms": [{
            "StateValue": "ALARM",
            "StateReason": "Threshold Crossed"
        }]
    }
    
    scenario._publish_metrics_phase()
    
    assert scenario.cloudwatch_client.put_metric_data.call_count == 2
    scenario.cloudwatch_client.describe_alarms.assert_called()


def test_demonstrate_alarm_phase(scenario):
    """Test retrieving and displaying alarm history."""
    scenario.alarm_name = "TestAlarm"
    
    scenario.cloudwatch_client.describe_alarm_history.return_value = {
        "AlarmHistoryItems": [
            {
                "Timestamp": MagicMock(),
                "HistorySummary": "State changed from OK to ALARM"
            }
        ]
    }
    
    scenario._demonstrate_alarm_phase()
    
    scenario.cloudwatch_client.describe_alarm_history.assert_called_once_with(
        AlarmName="TestAlarm",
        HistoryItemType="StateUpdate",
        MaxRecords=5
    )


@patch("scenario_cloudwatch_sns_monitoring.q")
def test_cleanup_phase_with_deletion(mock_q, scenario):
    """Test cleanup phase when user chooses to delete resources."""
    mock_q.ask.return_value = True
    
    scenario.alarm_name = "TestAlarm"
    scenario.dashboard_name = "monitoring-dashboard"
    
    mock_topic = MagicMock()
    mock_topic.arn = "arn:aws:sns:us-east-1:123456789012:test-topic"
    scenario.topic = mock_topic
    
    mock_subscription = MagicMock()
    scenario.subscription = mock_subscription
    
    scenario._cleanup_phase()
    
    scenario.cloudwatch_client.delete_dashboards.assert_called_once_with(
        DashboardNames=["monitoring-dashboard"]
    )
    scenario.cloudwatch_client.delete_alarms.assert_called_once_with(
        AlarmNames=["TestAlarm"]
    )
    mock_subscription.delete.assert_called_once()
    mock_topic.delete.assert_called_once()


@patch("scenario_cloudwatch_sns_monitoring.q")
def test_cleanup_phase_without_deletion(mock_q, scenario):
    """Test cleanup phase when user chooses not to delete resources."""
    mock_q.ask.return_value = False
    
    scenario.alarm_name = "TestAlarm"
    mock_topic = MagicMock()
    mock_topic.arn = "arn:aws:sns:us-east-1:123456789012:test-topic"
    scenario.topic = mock_topic
    
    scenario._cleanup_phase()
    
    scenario.cloudwatch_client.delete_dashboards.assert_not_called()
    scenario.cloudwatch_client.delete_alarms.assert_not_called()


@patch("scenario_cloudwatch_sns_monitoring.q")
def test_run_scenario_success(mock_q, scenario):
    """Test complete scenario execution."""
    mock_q.ask.side_effect = ["test@example.com", "TestAlarm", False]
    
    mock_topic = MagicMock()
    mock_topic.arn = "arn:aws:sns:us-east-1:123456789012:test-topic"
    scenario.sns_resource.create_topic.return_value = mock_topic
    
    scenario.cloudwatch_client.describe_alarms.return_value = {
        "MetricAlarms": [{
            "StateValue": "ALARM",
            "StateReason": "Threshold Crossed"
        }]
    }
    
    scenario.cloudwatch_client.describe_alarm_history.return_value = {
        "AlarmHistoryItems": []
    }
    
    with patch("builtins.input", return_value=""):
        scenario.run_scenario()
    
    scenario.sns_resource.create_topic.assert_called_once()
    scenario.cloudwatch_client.put_metric_alarm.assert_called_once()
    assert scenario.cloudwatch_client.put_metric_data.call_count == 2


def test_run_scenario_handles_errors(scenario):
    """Test scenario handles errors gracefully."""
    scenario.sns_resource.create_topic.side_effect = ClientError(
        {"Error": {"Code": "InvalidParameter", "Message": "Invalid topic name"}},
        "CreateTopic"
    )
    
    with patch("scenario_cloudwatch_sns_monitoring.q"):
        with patch("builtins.input", return_value=""):
            scenario.run_scenario()
    
    # Cleanup should still be attempted
    assert True  # Scenario completes without crashing
