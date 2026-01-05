# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for AWS SQS operations in the Topics and Queues scenario.
"""

import json
import logging
from typing import Dict, Any, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sqs.SqsWrapper]
class SqsWrapper:
    """Wrapper class for managing Amazon SQS operations."""

    def __init__(self, sqs_client: Any) -> None:
        """
        Initialize the SqsWrapper.
        
        Args:
            sqs_client: A Boto3 Amazon SQS client. This client provides low-level
                       access to AWS SQS services.
        """
        self.sqs_client = sqs_client

    @classmethod
    def from_client(cls) -> 'SqsWrapper':
        """
        Create an SqsWrapper instance using a default boto3 client.
        
        Returns:
            SqsWrapper: An instance of this class.
        """
        sqs_client = boto3.client('sqs')
        return cls(sqs_client)

    # snippet-start:[python.example_code.sqs.CreateQueue]
    def create_queue(self, queue_name: str, is_fifo: bool = False) -> str:
        """
        Create an SQS queue.

        Args:
            queue_name: The name of the queue to create
            is_fifo: Whether to create a FIFO queue

        Returns:
            str: The URL of the created queue

        Raises:
            ClientError: If the queue creation fails
        """
        try:
            # Add .fifo suffix for FIFO queues
            if is_fifo and not queue_name.endswith('.fifo'):
                queue_name += '.fifo'

            attributes = {}
            if is_fifo:
                attributes['FifoQueue'] = 'true'

            response = self.sqs_client.create_queue(
                QueueName=queue_name,
                Attributes=attributes
            )

            queue_url = response['QueueUrl']
            logger.info(f"Created queue: {queue_name} with URL: {queue_url}")
            return queue_url

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error creating queue {queue_name}: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sqs.CreateQueue]

    # snippet-start:[python.example_code.sqs.GetQueueAttributes]
    def get_queue_arn(self, queue_url: str) -> str:
        """
        Get the ARN of an SQS queue.

        Args:
            queue_url: The URL of the queue

        Returns:
            str: The ARN of the queue

        Raises:
            ClientError: If getting queue attributes fails
        """
        try:
            response = self.sqs_client.get_queue_attributes(
                QueueUrl=queue_url,
                AttributeNames=['QueueArn']
            )

            queue_arn = response['Attributes']['QueueArn']
            logger.info(f"Queue ARN for {queue_url}: {queue_arn}")
            return queue_arn

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error getting queue ARN: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sqs.GetQueueAttributes]

    # snippet-start:[python.example_code.sqs.SetQueueAttributes]
    def set_queue_policy_for_topic(self, queue_arn: str, topic_arn: str, queue_url: str) -> bool:
        """
        Set the queue policy to allow SNS to send messages to the queue.

        Args:
            queue_arn: The ARN of the SQS queue
            topic_arn: The ARN of the SNS topic
            queue_url: The URL of the SQS queue

        Returns:
            bool: True if successful

        Raises:
            ClientError: If setting the queue policy fails
        """
        try:
            # Create policy that allows SNS to send messages to the queue
            policy = {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {
                            "Service": "sns.amazonaws.com"
                        },
                        "Action": "sqs:SendMessage",
                        "Resource": queue_arn,
                        "Condition": {
                            "ArnEquals": {
                                "aws:SourceArn": topic_arn
                            }
                        }
                    }
                ]
            }

            self.sqs_client.set_queue_attributes(
                QueueUrl=queue_url,
                Attributes={
                    'Policy': json.dumps(policy)
                }
            )

            logger.info(f"Set queue policy for {queue_url} to allow messages from {topic_arn}")
            return True

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error setting queue policy: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sqs.SetQueueAttributes]

    # snippet-start:[python.example_code.sqs.ReceiveMessage]
    def receive_messages(self, queue_url: str, max_messages: int = 10) -> List[Dict[str, Any]]:
        """
        Receive messages from an SQS queue.

        Args:
            queue_url: The URL of the queue to receive messages from
            max_messages: Maximum number of messages to receive (1-10)

        Returns:
            List[Dict[str, Any]]: List of received messages

        Raises:
            ClientError: If receiving messages fails
        """
        try:
            # Ensure max_messages is within valid range
            max_messages = max(1, min(10, max_messages))

            response = self.sqs_client.receive_message(
                QueueUrl=queue_url,
                MaxNumberOfMessages=max_messages,
                WaitTimeSeconds=2,  # Short polling
                MessageAttributeNames=['All']
            )

            messages = response.get('Messages', [])
            logger.info(f"Received {len(messages)} messages from {queue_url}")
            return messages

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error receiving messages: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sqs.ReceiveMessage]

    # snippet-start:[python.example_code.sqs.DeleteMessageBatch]
    def delete_messages(self, queue_url: str, messages: List[Dict[str, Any]]) -> bool:
        """
        Delete messages from an SQS queue in batches.

        Args:
            queue_url: The URL of the queue
            messages: List of messages to delete

        Returns:
            bool: True if successful

        Raises:
            ClientError: If deleting messages fails
        """
        try:
            if not messages:
                return True

            # Build delete entries for batch delete
            delete_entries = []
            for i, message in enumerate(messages):
                delete_entries.append({
                    'Id': str(i),
                    'ReceiptHandle': message['ReceiptHandle']
                })

            # Delete messages in batches of 10 (SQS limit)
            batch_size = 10
            for i in range(0, len(delete_entries), batch_size):
                batch = delete_entries[i:i + batch_size]
                
                response = self.sqs_client.delete_message_batch(
                    QueueUrl=queue_url,
                    Entries=batch
                )

                # Check for failures
                if 'Failed' in response and response['Failed']:
                    for failed in response['Failed']:
                        logger.warning(f"Failed to delete message: {failed}")

            logger.info(f"Deleted {len(messages)} messages from {queue_url}")
            return True

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error deleting messages: {error_code} - {e}")
            raise

    # snippet-end:[python.example_code.sqs.DeleteMessageBatch]

    # snippet-start:[python.example_code.sqs.DeleteQueue]
    def delete_queue(self, queue_url: str) -> bool:
        """
        Delete an SQS queue.

        Args:
            queue_url: The URL of the queue to delete

        Returns:
            bool: True if successful

        Raises:
            ClientError: If the queue deletion fails
        """
        try:
            self.sqs_client.delete_queue(QueueUrl=queue_url)
            
            logger.info(f"Deleted queue: {queue_url}")
            return True

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            
            if error_code == 'AWS.SimpleQueueService.NonExistentQueue':
                logger.warning(f"Queue not found: {queue_url}")
                return True  # Already deleted
            else:
                logger.error(f"Error deleting queue: {error_code} - {e}")
                raise

    # snippet-end:[python.example_code.sqs.DeleteQueue]

    def list_queues(self, queue_name_prefix: Optional[str] = None) -> List[str]:
        """
        List all SQS queues in the account using pagination.

        Args:
            queue_name_prefix: Optional prefix to filter queue names

        Returns:
            List[str]: List of queue URLs

        Raises:
            ClientError: If listing queues fails
        """
        try:
            queue_urls = []
            paginator = self.sqs_client.get_paginator('list_queues')
            
            page_params = {}
            if queue_name_prefix:
                page_params['QueueNamePrefix'] = queue_name_prefix

            for page in paginator.paginate(**page_params):
                queue_urls.extend(page.get('QueueUrls', []))
            
            logger.info(f"Found {len(queue_urls)} queues")
            return queue_urls

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            if error_code == 'AccessDenied':
                logger.error("Access denied listing queues - check IAM permissions")
            else:
                logger.error(f"Error listing queues: {error_code} - {e}")
            raise

    def send_message(self, queue_url: str, message_body: str, **kwargs) -> str:
        """
        Send a message to an SQS queue.

        Args:
            queue_url: The URL of the queue
            message_body: The message content
            **kwargs: Additional message parameters (DelaySeconds, MessageAttributes, etc.)

        Returns:
            str: The message ID

        Raises:
            ClientError: If sending the message fails
        """
        try:
            send_params = {
                'QueueUrl': queue_url,
                'MessageBody': message_body,
                **kwargs
            }

            response = self.sqs_client.send_message(**send_params)
            
            message_id = response['MessageId']
            logger.info(f"Sent message to {queue_url} with ID: {message_id}")
            return message_id

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"Error sending message: {error_code} - {e}")
            raise

# snippet-end:[python.example_code.sqs.SqsWrapper]
