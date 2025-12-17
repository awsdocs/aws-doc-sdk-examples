# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Topics and Queues cross-service scenario.
"""

import pytest
from unittest.mock import Mock, patch
import logging

import sys
import os

# Add parent directory to path to import scenario modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from topics_and_queues_scenario import TopicsAndQueuesScenario
from sns_wrapper import SnsWrapper
from sqs_wrapper import SqsWrapper


class TestTopicsAndQueuesScenario:
    """Integration tests for the Topics and Queues scenario."""

    @pytest.fixture
    def mock_clients(self):
        """Create mock AWS clients."""
        mock_sns_client = Mock()
        mock_sqs_client = Mock()
        return mock_sns_client, mock_sqs_client

    @pytest.fixture
    def wrappers(self, mock_clients):
        """Create wrapper instances with mocked dependencies."""
        mock_sns_client, mock_sqs_client = mock_clients
        
        sns_wrapper = SnsWrapper(mock_sns_client)
        sqs_wrapper = SqsWrapper(mock_sqs_client)
        
        return sns_wrapper, sqs_wrapper

    @pytest.fixture
    def scenario(self, wrappers):
        """Create a scenario instance with mocked dependencies."""
        sns_wrapper, sqs_wrapper = wrappers
        return TopicsAndQueuesScenario(sns_wrapper, sqs_wrapper)

    @patch('demo_tools.question.ask')
    def test_scenario_standard_topic_no_errors_logged(self, mock_ask, scenario, caplog):
        """
        Verify the scenario runs without logging any errors for standard topic.
        
        Args:
            mock_ask: Mock for user input
            scenario: The scenario instance
            caplog: Pytest log capture fixture
        """
        # Arrange
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

        # Mock successful AWS operations
        scenario.sns_wrapper.sns_client.create_topic.return_value = {
            'TopicArn': 'arn:aws:sns:us-east-1:123456789012:test-topic'
        }
        
        scenario.sqs_wrapper.sqs_client.create_queue.return_value = {
            'QueueUrl': 'https://sqs.us-east-1.amazonaws.com/123456789012/test-queue'
        }
        
        scenario.sqs_wrapper.sqs_client.get_queue_attributes.return_value = {
            'Attributes': {
                'QueueArn': 'arn:aws:sqs:us-east-1:123456789012:test-queue'
            }
        }
        
        scenario.sns_wrapper.sns_client.subscribe.return_value = {
            'SubscriptionArn': 'arn:aws:sns:us-east-1:123456789012:test-topic:subscription-id'
        }
        
        scenario.sns_wrapper.sns_client.publish.return_value = {
            'MessageId': 'message-id-123'
        }
        
        scenario.sqs_wrapper.sqs_client.receive_message.return_value = {
            'Messages': []
        }

        # Act
        with caplog.at_level(logging.ERROR):
            scenario.run_scenario()

        # Assert no errors logged
        error_logs = [record for record in caplog.records if record.levelno >= logging.ERROR]
        assert len(error_logs) == 0, f"Expected no error logs, but found: {error_logs}"

    @patch('demo_tools.question.ask')
    def test_scenario_fifo_topic_with_filtering_no_errors_logged(self, mock_ask, scenario, caplog):
        """
        Verify the scenario runs without logging any errors for FIFO topic with filtering.
        
        Args:
            mock_ask: Mock for user input
            scenario: The scenario instance
            caplog: Pytest log capture fixture
        """
        # Arrange
        mock_ask.side_effect = [
            True,  # Use FIFO topic? (Yes)
            "test-fifo-topic",  # Topic name
            True,  # Use content-based deduplication?
            "test-fifo-queue-1",  # Queue 1 name
            "test-fifo-queue-2",  # Queue 2 name
            True,  # Filter messages for queue 1?
            1,  # Select tone filter (cheerful)
            0,  # Stop adding filters
            False,  # Filter messages for queue 2? (No)
            "Hello FIFO World",  # Message text
            "group-1",  # Message group ID
            True,  # Add attribute?
            2,  # Select tone (funny)
            False,  # Send another message? (No)
            "",  # Press Enter to continue (multiple times)
            "",
            "",
            "",
            True,  # Delete queue 1?
            True,  # Delete queue 2?
            True,  # Delete topic?
        ]

        # Mock successful AWS operations for FIFO
        scenario.sns_wrapper.sns_client.create_topic.return_value = {
            'TopicArn': 'arn:aws:sns:us-east-1:123456789012:test-fifo-topic.fifo'
        }
        
        scenario.sqs_wrapper.sqs_client.create_queue.return_value = {
            'QueueUrl': 'https://sqs.us-east-1.amazonaws.com/123456789012/test-fifo-queue.fifo'
        }
        
        scenario.sqs_wrapper.sqs_client.get_queue_attributes.return_value = {
            'Attributes': {
                'QueueArn': 'arn:aws:sqs:us-east-1:123456789012:test-fifo-queue.fifo'
            }
        }
        
        scenario.sns_wrapper.sns_client.subscribe.return_value = {
            'SubscriptionArn': 'arn:aws:sns:us-east-1:123456789012:test-fifo-topic.fifo:subscription-id'
        }
        
        scenario.sns_wrapper.sns_client.publish.return_value = {
            'MessageId': 'fifo-message-id-123'
        }
        
        scenario.sqs_wrapper.sqs_client.receive_message.return_value = {
            'Messages': [
                {
                    'Body': '{"Message": "Hello FIFO World"}',
                    'ReceiptHandle': 'receipt-handle-123'
                }
            ]
        }

        # Act
        with caplog.at_level(logging.ERROR):
            scenario.run_scenario()

        # Assert no errors logged
        error_logs = [record for record in caplog.records if record.levelno >= logging.ERROR]
        assert len(error_logs) == 0, f"Expected no error logs, but found: {error_logs}"

    def test_scenario_initialization(self, wrappers):
        """Test that the scenario initializes correctly."""
        sns_wrapper, sqs_wrapper = wrappers
        scenario = TopicsAndQueuesScenario(sns_wrapper, sqs_wrapper)
        
        assert scenario.sns_wrapper == sns_wrapper
        assert scenario.sqs_wrapper == sqs_wrapper
        assert scenario.queue_count == 2
        assert len(scenario.tones) == 4
        assert "cheerful" in scenario.tones
        assert "funny" in scenario.tones
        assert "serious" in scenario.tones
        assert "sincere" in scenario.tones

    def test_create_filter_policy(self, scenario):
        """Test filter policy creation with mocked user input."""
        with patch('demo_tools.question.ask') as mock_ask:
            mock_ask.side_effect = [1, 3, 0]  # Select cheerful, serious, then stop
            
            filter_policy = scenario._create_filter_policy()
            
            assert filter_policy is not None
            import json
            parsed_policy = json.loads(filter_policy)
            assert "tone" in parsed_policy
            assert "cheerful" in parsed_policy["tone"]
            assert "serious" in parsed_policy["tone"]
            assert len(parsed_policy["tone"]) == 2

    def test_create_filter_policy_no_selections(self, scenario):
        """Test filter policy creation with no selections."""
        with patch('demo_tools.question.ask') as mock_ask:
            mock_ask.side_effect = [0]  # Stop immediately
            
            filter_policy = scenario._create_filter_policy()
            
            assert filter_policy is None

    @patch('demo_tools.question.ask')
    def test_scenario_handles_aws_errors_gracefully(self, mock_ask, scenario, caplog):
        """Test that the scenario handles AWS errors gracefully."""
        # Arrange
        mock_ask.side_effect = [
            False,  # Use FIFO topic? (No)
            "test-topic",  # Topic name
        ]

        # Mock AWS error
        from botocore.exceptions import ClientError
        error_response = {'Error': {'Code': 'AccessDenied', 'Message': 'Access denied'}}
        scenario.sns_wrapper.sns_client.create_topic.side_effect = ClientError(error_response, 'CreateTopic')

        # Act & Assert
        with pytest.raises(ClientError):
            scenario.run_scenario()

    def test_poll_queue_for_messages_empty_queue(self, scenario):
        """Test polling an empty queue."""
        scenario.sqs_wrapper.sqs_client.receive_message.return_value = {'Messages': []}
        
        messages = scenario._poll_queue_for_messages('test-queue-url')
        
        assert messages == []
        # Verify receive_message was called
        scenario.sqs_wrapper.sqs_client.receive_message.assert_called()

    def test_poll_queue_for_messages_with_messages(self, scenario):
        """Test polling a queue with messages."""
        test_messages = [
            {'Body': 'Message 1', 'ReceiptHandle': 'handle-1'},
            {'Body': 'Message 2', 'ReceiptHandle': 'handle-2'}
        ]
        
        # Mock first call returns messages, second call returns empty
        scenario.sqs_wrapper.sqs_client.receive_message.side_effect = [
            {'Messages': test_messages},
            {'Messages': []}
        ]
        
        messages = scenario._poll_queue_for_messages('test-queue-url')
        
        assert len(messages) == 2
        assert messages[0]['Body'] == 'Message 1'
        assert messages[1]['Body'] == 'Message 2'
