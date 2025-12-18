# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Topics and Queues cross-service scenario.
"""

import pytest
from unittest.mock import patch
import logging
import boto3

import sys
import os

# Add parent directory to path to import scenario modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
# Add test_tools to path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "..", "..", "..", "test_tools"))

from topics_and_queues_scenario import TopicsAndQueuesScenario
from sns_wrapper import SnsWrapper
from sqs_wrapper import SqsWrapper


class MockManager:
    """Mock manager for the Topics and Queues scenario tests."""
    
    def __init__(self, sns_client, sns_stubber, sqs_client, sqs_stubber, stub_runner):
        """Topics and Queues test setup manager."""
        self.sns_client = sns_client
        self.sns_stubber = sns_stubber
        self.sqs_client = sqs_client
        self.sqs_stubber = sqs_stubber
        self.stub_runner = stub_runner
        
        # Create wrappers and scenario
        self.sns_wrapper = SnsWrapper(sns_client)
        self.sqs_wrapper = SqsWrapper(sqs_client)
        self.scenario = TopicsAndQueuesScenario(self.sns_wrapper, self.sqs_wrapper)


@pytest.fixture
def mock_mgr(make_stubber, stub_runner):
    """Create a mock manager with AWS service stubbers."""
    sns_client = boto3.client('sns')
    sns_stubber = make_stubber(sns_client)
    sqs_client = boto3.client('sqs')
    sqs_stubber = make_stubber(sqs_client)
    
    return MockManager(sns_client, sns_stubber, sqs_client, sqs_stubber, stub_runner)


class TestTopicsAndQueuesScenario:
    """Integration tests for the Topics and Queues scenario."""

    @patch('demo_tools.question.ask')
    def test_scenario_standard_topic_no_errors_logged(self, mock_ask, mock_mgr, caplog):
        """
        Verify the scenario runs without logging any errors for standard topic.
        
        Args:
            mock_ask: Mock for user input
            mock_mgr: Mock manager with stubbed AWS clients
            caplog: Pytest log capture fixture
        """
        # Arrange user inputs
        mock_ask.side_effect = [
            False,  # Use FIFO topic? (No - standard topic)
            "test-topic",  # Topic name
            "test-queue-1",  # Queue 1 name
            "test-queue-2",  # Queue 2 name
            "Hello World",  # Message text
            False,  # Send another message? (No)
            "",  # Press Enter to continue (multiple times)
            "",
            "",
            "",
            True,  # Delete queue 1?
            True,  # Delete queue 2?
            True,  # Delete topic?
        ]

        # Set up stubs for AWS operations
        with mock_mgr.stub_runner(None, None) as runner:
            # SNS operations
            runner.add(
                mock_mgr.sns_stubber.stub_create_topic,
                "test-topic",
                "arn:aws:sns:us-east-1:123456789012:test-topic",
                {}  # Empty attributes dict for standard (non-FIFO) topic
            )
            runner.add(
                mock_mgr.sns_stubber.stub_subscribe,
                "arn:aws:sns:us-east-1:123456789012:test-topic",
                "sqs",
                "arn:aws:sqs:us-east-1:123456789012:test-queue-1",
                "arn:aws:sns:us-east-1:123456789012:test-topic:subscription-1"
            )
            runner.add(
                mock_mgr.sns_stubber.stub_subscribe,
                "arn:aws:sns:us-east-1:123456789012:test-topic",
                "sqs", 
                "arn:aws:sqs:us-east-1:123456789012:test-queue-2",
                "arn:aws:sns:us-east-1:123456789012:test-topic:subscription-2"
            )
            runner.add(
                mock_mgr.sns_stubber.stub_publish,
                "Hello World",
                "message-id-123",
                topic_arn="arn:aws:sns:us-east-1:123456789012:test-topic"
            )
            runner.add(
                mock_mgr.sns_stubber.stub_unsubscribe,
                "arn:aws:sns:us-east-1:123456789012:test-topic:subscription-1"
            )
            runner.add(
                mock_mgr.sns_stubber.stub_unsubscribe,
                "arn:aws:sns:us-east-1:123456789012:test-topic:subscription-2"
            )
            runner.add(
                mock_mgr.sns_stubber.stub_delete_topic,
                "arn:aws:sns:us-east-1:123456789012:test-topic"
            )
            
            # SQS operations
            runner.add(
                mock_mgr.sqs_stubber.stub_create_queue,
                "test-queue-1",
                {},
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-1"
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_create_queue,
                "test-queue-2",
                {},
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-2"
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_get_queue_attributes,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-1",
                "arn:aws:sqs:us-east-1:123456789012:test-queue-1"
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_get_queue_attributes,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-2",
                "arn:aws:sqs:us-east-1:123456789012:test-queue-2"
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_set_queue_attributes,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-1",
                {"Policy": ""}  # Will be replaced with actual policy
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_set_queue_attributes,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-2",
                {"Policy": ""}  # Will be replaced with actual policy
            )
            # Queue polling - only 1 call per queue since empty results break the polling loop
            runner.add(
                mock_mgr.sqs_stubber.stub_receive_messages,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-1",
                [],  # messages (empty list - causes polling to stop)
                10   # receive_count (MaxNumberOfMessages)
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_receive_messages,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-2", 
                [],  # messages (empty list - causes polling to stop)
                10   # receive_count (MaxNumberOfMessages)
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_delete_queue,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-1"
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_delete_queue,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue-2"
            )

            # Act - Run the scenario
            with caplog.at_level(logging.ERROR):
                mock_mgr.scenario.run_scenario()

            # Assert no errors logged
            error_logs = [record for record in caplog.records if record.levelno >= logging.ERROR]
            assert len(error_logs) == 0, f"Expected no error logs, but found: {error_logs}"

    def test_scenario_initialization(self, mock_mgr):
        """Test that the scenario initializes correctly."""
        scenario = mock_mgr.scenario
        
        assert scenario.sns_wrapper == mock_mgr.sns_wrapper
        assert scenario.sqs_wrapper == mock_mgr.sqs_wrapper
        assert scenario.queue_count == 2
        assert len(scenario.tones) == 4
        assert "cheerful" in scenario.tones
        assert "funny" in scenario.tones
        assert "serious" in scenario.tones
        assert "sincere" in scenario.tones

    def test_create_filter_policy(self, mock_mgr):
        """Test filter policy creation with mocked user input."""
        with patch('demo_tools.question.ask') as mock_ask:
            mock_ask.side_effect = [1, 3, 0]  # Select cheerful, serious, then stop
            
            filter_policy = mock_mgr.scenario._create_filter_policy()
            
            assert filter_policy is not None
            import json
            parsed_policy = json.loads(filter_policy)
            assert "tone" in parsed_policy
            assert "cheerful" in parsed_policy["tone"]
            assert "serious" in parsed_policy["tone"]
            assert len(parsed_policy["tone"]) == 2

    def test_create_filter_policy_no_selections(self, mock_mgr):
        """Test filter policy creation with no selections."""
        with patch('demo_tools.question.ask') as mock_ask:
            mock_ask.side_effect = [0]  # Stop immediately
            
            filter_policy = mock_mgr.scenario._create_filter_policy()
            
            assert filter_policy is None

    @patch('demo_tools.question.ask')
    def test_scenario_handles_aws_errors_gracefully(self, mock_ask, mock_mgr):
        """Test that the scenario handles AWS errors gracefully."""
        # Arrange
        mock_ask.side_effect = [
            False,  # Use FIFO topic? (No)
            "test-topic",  # Topic name
        ]

        # Set up stub to simulate AWS error
        with mock_mgr.stub_runner("TestException", 0) as runner:
            runner.add(
                mock_mgr.sns_stubber.stub_create_topic,
                "test-topic",
                "arn:aws:sns:us-east-1:123456789012:test-topic"
            )

            # Act & Assert - Should handle error gracefully
            mock_mgr.scenario.run_scenario()
            # Verify error was handled appropriately (scenario should continue or cleanup)

    def test_poll_queue_for_messages_empty_queue(self, mock_mgr):
        """Test polling an empty queue."""
        with mock_mgr.stub_runner(None, None) as runner:
            runner.add(
                mock_mgr.sqs_stubber.stub_receive_messages,
                'test-queue-url',
                [],
                10
            )
            
            messages = mock_mgr.scenario._poll_queue_for_messages('test-queue-url')
            
            assert messages == []

    def test_poll_queue_for_messages_with_messages(self, mock_mgr):
        """Test polling a queue with messages."""
        test_messages = [
            {'body': 'Message 1'},
            {'body': 'Message 2'}
        ]
        
        with mock_mgr.stub_runner(None, None) as runner:
            # First call returns messages
            runner.add(
                mock_mgr.sqs_stubber.stub_receive_messages,
                'test-queue-url',
                test_messages,
                10
            )
            # Second call returns empty (polling stops)
            runner.add(
                mock_mgr.sqs_stubber.stub_receive_messages,
                'test-queue-url',
                [],
                10
            )
            
            messages = mock_mgr.scenario._poll_queue_for_messages('test-queue-url')
            
            assert len(messages) == 2
            assert messages[0]['Body'] == 'Message 1'
            assert messages[1]['Body'] == 'Message 2'


@pytest.mark.integ
def test_run_scenario_integ(input_mocker, capsys):
    """Test the scenario with an integration test using live AWS services."""
    # Mock user inputs for a simple test case
    answers = [
        False,  # Use FIFO topic? (No - standard topic)
        "test-topic-integ",  # Topic name
        "test-queue-1-integ",  # Queue 1 name
        "test-queue-2-integ",  # Queue 2 name
        "Hello Integration Test",  # Message text
        False,  # Send another message? (No)
        "",  # Press Enter to continue (multiple times)
        "",
        "",
        "",
        True,  # Delete queue 1?
        True,  # Delete queue 2?
        True,  # Delete topic?
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
    assert "Messaging with topics and queues scenario is complete." in captured.out
