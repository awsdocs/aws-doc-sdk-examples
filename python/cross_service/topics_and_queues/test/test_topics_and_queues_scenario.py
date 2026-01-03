# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Topics and Queues cross-service scenario.
"""

import pytest
from unittest.mock import patch
import logging
import boto3

from topics_and_queues_scenario import TopicsAndQueuesScenario
from sns_wrapper import SnsWrapper
from sqs_wrapper import SqsWrapper


class MockManager:
    """Mock manager for the Topics and Queues scenario tests."""
    
    def __init__(self, stub_runner, scenario_data, input_mocker):
        """Topics and Queues test setup manager."""
        self.scenario_data = scenario_data
        self.stub_runner = stub_runner
        self.input_mocker = input_mocker
        
        # Test data
        self.topic_name = "test-topic"
        self.topic_arn = "arn:aws:sns:us-east-1:123456789012:test-topic"
        self.queue1_name = "test-queue-1"
        self.queue1_url = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-1"
        self.queue1_arn = "arn:aws:sqs:us-east-1:123456789012:test-queue-1"
        self.queue2_name = "test-queue-2"
        self.queue2_url = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-2"
        self.queue2_arn = "arn:aws:sqs:us-east-1:123456789012:test-queue-2"
        self.subscription1_arn = "arn:aws:sns:us-east-1:123456789012:test-topic:subscription-1"
        self.subscription2_arn = "arn:aws:sns:us-east-1:123456789012:test-topic:subscription-2"
        self.message_id = "message-id-123"

    def setup_stubs(self, error, stop_on):
        """Setup stubs for the scenario"""
        # Mock user inputs
        answers = [
            "n",  # Use FIFO topic? (No - standard topic)
            self.topic_name,  # Topic name
            self.queue1_name,  # Queue 1 name
            self.queue2_name,  # Queue 2 name
            "Hello World",  # Message text
            "n",  # Send another message? (No)
            "",  # Press Enter to continue (multiple times)
            "",
            "",
            "",
            "y",  # Delete queue 1?
            "y",  # Delete queue 2?
            "y",  # Delete topic?
        ]
        self.input_mocker.mock_answers(answers)

        with self.stub_runner(error, stop_on) as runner:
            # Setup Phase - Topic creation
            runner.add(
                self.scenario_data.sns_stubber.stub_create_topic,
                self.topic_name,
                self.topic_arn,
                {}  # Empty attributes dict for standard (non-FIFO) topic
            )
            
            # Setup Phase - Queue creation and subscription (Queue 1)
            runner.add(
                self.scenario_data.sqs_stubber.stub_create_queue,
                self.queue1_name,
                {},
                self.queue1_url
            )
            runner.add(
                self.scenario_data.sqs_stubber.stub_get_queue_arn,
                self.queue1_url,
                self.queue1_arn
            )
            runner.add(
                self.scenario_data.sqs_stubber.stub_set_queue_attributes,
                self.queue1_url,
                {"Policy": ""}  # Will be replaced with actual policy
            )
            runner.add(
                self.scenario_data.sns_stubber.stub_subscribe,
                self.topic_arn,
                "sqs",
                self.queue1_arn,
                self.subscription1_arn
            )
            
            # Setup Phase - Queue creation and subscription (Queue 2)
            runner.add(
                self.scenario_data.sqs_stubber.stub_create_queue,
                self.queue2_name,
                {},
                self.queue2_url
            )
            runner.add(
                self.scenario_data.sqs_stubber.stub_get_queue_arn,
                self.queue2_url,
                self.queue2_arn
            )
            runner.add(
                self.scenario_data.sqs_stubber.stub_set_queue_attributes,
                self.queue2_url,
                {"Policy": ""}  # Will be replaced with actual policy
            )
            runner.add(
                self.scenario_data.sns_stubber.stub_subscribe,
                self.topic_arn,
                "sqs", 
                self.queue2_arn,
                self.subscription2_arn
            )
            
            # Publishing Phase
            runner.add(
                self.scenario_data.sns_stubber.stub_publish,
                "Hello World",
                self.message_id,
                topic_arn=self.topic_arn
            )
            
            # Polling Phase - Check queues for messages
            runner.add(
                self.scenario_data.sqs_stubber.stub_receive_messages,
                self.queue1_url,
                [],  # messages (empty list - causes polling to stop)
                10   # receive_count (MaxNumberOfMessages)
            )
            runner.add(
                self.scenario_data.sqs_stubber.stub_receive_messages,
                self.queue2_url, 
                [],  # messages (empty list - causes polling to stop)
                10   # receive_count (MaxNumberOfMessages)
            )
            
            # Cleanup Phase
            runner.add(
                self.scenario_data.sqs_stubber.stub_delete_queue,
                self.queue1_url
            )
            runner.add(
                self.scenario_data.sqs_stubber.stub_delete_queue,
                self.queue2_url
            )
            runner.add(
                self.scenario_data.sns_stubber.stub_unsubscribe,
                self.subscription1_arn
            )
            runner.add(
                self.scenario_data.sns_stubber.stub_unsubscribe,
                self.subscription2_arn
            )
            runner.add(
                self.scenario_data.sns_stubber.stub_delete_topic,
                self.topic_arn
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


class TestTopicsAndQueuesScenario:
    """Integration tests for the Topics and Queues scenario."""

    def test_scenario_standard_topic_no_errors_logged(self, mock_mgr, caplog):
        """
        Verify the scenario runs without logging any errors for standard topic.
        
        Args:
            mock_mgr: Mock manager with stubbed AWS clients
            caplog: Pytest log capture fixture
        """
        mock_mgr.setup_stubs(None, None)

        # Act - Run the scenario
        with caplog.at_level(logging.ERROR):
            mock_mgr.scenario_data.scenario.run_scenario()

        # Assert no errors logged
        error_logs = [record for record in caplog.records if record.levelno >= logging.ERROR]
        assert len(error_logs) == 0, f"Expected no error logs, but found: {error_logs}"


@pytest.mark.integ
def test_run_scenario_integ(input_mocker, capsys):
    """Test the scenario with an integration test using live AWS services."""
    # Mock user inputs for a simple test case
    answers = [
        "n",  # Use FIFO topic? (No - standard topic)
        "test-topic-integ",  # Topic name
        "test-queue-1-integ",  # Queue 1 name
        "test-queue-2-integ",  # Queue 2 name
        "Hello Integration Test",  # Message text
        "n",  # Send another message? (No)
        "",  # Press Enter to continue (multiple times)
        "",
        "",
        "",
        "y",  # Delete queue 1?
        "y",  # Delete queue 2?
        "y",  # Delete topic?
    ]

    input_mocker.mock_answers(answers)
    
    # Create real AWS clients
    sns_client = boto3.client('sns')
    sqs_client = boto3.client('sqs')
    
    # Initialize wrappers and scenario
    sns_wrapper = SnsWrapper(sns_client)
    sqs_wrapper = SqsWrapper(sqs_client)
    scenario = TopicsAndQueuesScenario(sns_wrapper, sqs_wrapper)

    # Run the scenario with live services
    scenario.run_scenario()

    # Verify the scenario completed successfully
    captured = capsys.readouterr()
    
    # Verify the scenario completed successfully
    assert "Messaging with topics and queues scenario is complete." in captured.out
