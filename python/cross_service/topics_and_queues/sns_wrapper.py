# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for AWS SNS operations in the Topics and Queues scenario.
"""

import json
import logging
from typing import Dict, Any, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sns.SnsWrapper]
class SnsWrapper:
    """Wrapper class for managing Amazon SNS operations."""

    def __init__(self, sns_client: Any) -> None:
        """
        Initialize the SnsWrapper.
        
        Args:
            sns_client: A Boto3 Amazon SNS client. This client provides low-level
                       access to AWS SNS services.
        """
        self.sns_client = sns_client

    @classmethod
    def from_client(cls) -> 'SnsWrapper':
        """
        Create an SnsWrapper instance using a default boto3 client.
        
        Returns:
            SnsWrapper: An instance of this class.
        """
        sns_client = boto3.client('sns')
        return cls(sns_client)

    # snippet-start:[python.example_code.sns.CreateTopic]
    def create_topic(
        self, 
        topic_name: str, 
        is_fifo: bool = False, 
        content_based_deduplication: bool = False
    ) -> str:
        """
        Create an SNS topic.

        Args:
            topic_name: The name of the topic to create
            is_fifo: Whether to create a FIFO topic
            content_based_deduplication: Whether to use content-based deduplication for FIFO topics

        Returns:
            str: The ARN of the created topic

        Raises:
            ClientError: If the topic creation fails
        """
        try:
            # Add .fifo suffix for FIFO topics
            if is_fifo and not topic_name.endswith('.fifo'):
                topic_name += '.fifo'

            attributes = {}
            if is_fifo:
                attributes['FifoTopic'] = 'true'
                if content_based_deduplication:
                    attributes['ContentBasedDeduplication'] = 'true'

            response = self.sns_client.create_topic(
                Name=topic_name,
                Attributes=attributes
            )

            topic_arn = response['TopicArn']
            logger.info(f"Created topic: {topic_name} with ARN: {topic_arn}")
            return topic_arn

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error creating topic {topic_name}: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sns.CreateTopic]

    # snippet-start:[python.example_code.sns.Subscribe]
    def subscribe_queue_to_topic(
        self, 
        topic_arn: str, 
        queue_arn: str, 
        filter_policy: Optional[str] = None
    ) -> str:
        """
        Subscribe an SQS queue to an SNS topic.

        Args:
            topic_arn: The ARN of the SNS topic
            queue_arn: The ARN of the SQS queue
            filter_policy: Optional JSON filter policy for message filtering

        Returns:
            str: The ARN of the subscription

        Raises:
            ClientError: If the subscription fails
        """
        try:
            attributes = {}
            if filter_policy:
                attributes['FilterPolicy'] = filter_policy

            response = self.sns_client.subscribe(
                TopicArn=topic_arn,
                Protocol='sqs',
                Endpoint=queue_arn,
                Attributes=attributes
            )

            subscription_arn = response['SubscriptionArn']
            logger.info(f"Subscribed queue {queue_arn} to topic {topic_arn}")
            return subscription_arn

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error subscribing queue to topic: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sns.Subscribe]

    # snippet-start:[python.example_code.sns.Publish]
    def publish_message(
        self,
        topic_arn: str,
        message: str,
        tone_attribute: Optional[str] = None,
        deduplication_id: Optional[str] = None,
        message_group_id: Optional[str] = None
    ) -> str:
        """
        Publish a message to an SNS topic.

        Args:
            topic_arn: The ARN of the SNS topic
            message: The message content to publish
            tone_attribute: Optional tone attribute for message filtering
            deduplication_id: Optional deduplication ID for FIFO topics
            message_group_id: Optional message group ID for FIFO topics

        Returns:
            str: The message ID of the published message

        Raises:
            ClientError: If the message publication fails
        """
        try:
            publish_args = {
                'TopicArn': topic_arn,
                'Message': message
            }

            # Add message attributes if tone is specified
            if tone_attribute:
                publish_args['MessageAttributes'] = {
                    'tone': {
                        'DataType': 'String',
                        'StringValue': tone_attribute
                    }
                }

            # Add FIFO-specific parameters
            if message_group_id:
                publish_args['MessageGroupId'] = message_group_id

            if deduplication_id:
                publish_args['MessageDeduplicationId'] = deduplication_id

            response = self.sns_client.publish(**publish_args)

            message_id = response['MessageId']
            logger.info(f"Published message to topic {topic_arn} with ID: {message_id}")
            return message_id

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error publishing message to topic: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sns.Publish]

    # snippet-start:[python.example_code.sns.Unsubscribe]
    def unsubscribe(self, subscription_arn: str) -> bool:
        """
        Unsubscribe from an SNS topic.

        Args:
            subscription_arn: The ARN of the subscription to remove

        Returns:
            bool: True if successful

        Raises:
            ClientError: If the unsubscribe operation fails
        """
        try:
            self.sns_client.unsubscribe(SubscriptionArn=subscription_arn)
            
            logger.info(f"Unsubscribed: {subscription_arn}")
            return True

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            
            if error_code == 'NotFound':
                logger.warning(f"Subscription not found: {subscription_arn}")
                return True  # Already unsubscribed
            else:
                logger.error(f"Error unsubscribing: {error_code} - {e}")
                raise

    # snippet-end:[python.example_code.sns.Unsubscribe]

    # snippet-start:[python.example_code.sns.DeleteTopic]
    def delete_topic(self, topic_arn: str) -> bool:
        """
        Delete an SNS topic.

        Args:
            topic_arn: The ARN of the topic to delete

        Returns:
            bool: True if successful

        Raises:
            ClientError: If the topic deletion fails
        """
        try:
            self.sns_client.delete_topic(TopicArn=topic_arn)
            
            logger.info(f"Deleted topic: {topic_arn}")
            return True

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            
            if error_code == 'NotFound':
                logger.warning(f"Topic not found: {topic_arn}")
                return True  # Already deleted
            else:
                logger.error(f"Error deleting topic: {error_code} - {e}")
                raise

    # snippet-end:[python.example_code.sns.DeleteTopic]

    def list_topics(self) -> list:
        """
        List all SNS topics in the account using pagination.

        Returns:
            list: List of topic ARNs

        Raises:
            ClientError: If listing topics fails
        """
        try:
            topics = []
            paginator = self.sns_client.get_paginator('list_topics')
            
            for page in paginator.paginate():
                topics.extend([topic['TopicArn'] for topic in page.get('Topics', [])])
            
            logger.info(f"Found {len(topics)} topics")
            return topics

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            if error_code == 'AuthorizationError':
                logger.error("Authorization error listing topics - check IAM permissions")
            else:
                logger.error(f"Error listing topics: {error_code} - {e}")
            raise

# snippet-end:[python.example_code.sns.SnsWrapper]
