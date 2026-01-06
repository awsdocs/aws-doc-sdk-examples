# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Topics and Queues Cross-Service Scenario

This scenario demonstrates messaging with topics and queues using Amazon Simple Notification
Service (Amazon SNS) and Amazon Simple Queue Service (Amazon SQS). The scenario includes:

1. Create an SNS topic, either FIFO (First-In-First-Out) or non-FIFO
2. Create SQS queues to subscribe to the topic
3. Set up message filtering and queue policies
4. Subscribe the SQS queues to the SNS topic
5. Publish messages to the SNS topic with various attributes
6. Poll SQS queues for messages and display results
7. Clean up resources (delete queues, unsubscribe, delete topic)

The scenario uses both SNS and SQS services to demonstrate cross-service messaging patterns.
"""

import json
import sys
import time
from typing import List, Dict, Any, Optional
import logging

import boto3
from botocore.exceptions import ClientError

from sns_wrapper import SnsWrapper
from sqs_wrapper import SqsWrapper

# Add relative path to include demo_tools
sys.path.append("../..")
import demo_tools.question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cross_service.topics_and_queues.TopicsAndQueuesScenario]
class TopicsAndQueuesScenario:
    """Manages the Topics and Queues cross-service scenario."""

    DASHES = "-" * 80

    def __init__(self, sns_wrapper: SnsWrapper, sqs_wrapper: SqsWrapper) -> None:
        """
        Initialize the Topics and Queues scenario.

        :param sns_wrapper: SnsWrapper instance for SNS operations.
        :param sqs_wrapper: SqsWrapper instance for SQS operations.
        """
        self.sns_wrapper = sns_wrapper
        self.sqs_wrapper = sqs_wrapper
        
        # Scenario state
        self.use_fifo_topic = False
        self.use_content_based_deduplication = False
        self.topic_name = None
        self.topic_arn = None
        self.queue_count = 2
        self.queue_urls = []
        self.subscription_arns = []
        self.tones = ["cheerful", "funny", "serious", "sincere"]

    def run_scenario(self) -> None:
        """Run the Topics and Queues cross-service scenario."""
        print(self.DASHES)
        print("Welcome to messaging with topics and queues.")
        print(self.DASHES)
        print(f"""
    In this scenario, you will create an SNS topic and subscribe {self.queue_count} SQS queues to the topic.
    You can select from several options for configuring the topic and the subscriptions for the queues.
    You can then post to the topic and see the results in the queues.
        """)

        try:
            # Setup Phase
            print(self.DASHES)
            self._setup_topic()
            print(self.DASHES)

            self._setup_queues()
            print(self.DASHES)

            # Demonstration Phase
            self._publish_messages()
            print(self.DASHES)

            # Examination Phase
            self._poll_queues_for_messages()
            print(self.DASHES)

            # Cleanup Phase
            self._cleanup_resources()
            print(self.DASHES)

        except Exception as e:
            logger.error(f"Scenario failed: {e}")
            print(f"There was a problem with the scenario: {e}")
            print("\nInitiating cleanup...")
            try:
                self._cleanup_resources()
            except Exception as cleanup_error:
                logger.error(f"Error during cleanup: {cleanup_error}")

        print("Messaging with topics and queues scenario is complete.")
        print(self.DASHES)

    def _setup_topic(self) -> None:
        """Set up the SNS topic to be used with the queues."""
        print("SNS topics can be configured as FIFO (First-In-First-Out).")
        print("FIFO topics deliver messages in order and support deduplication and message filtering.")
        print()

        self.use_fifo_topic = q.ask("Would you like to work with FIFO topics? (y/n): ", q.is_yesno)

        if self.use_fifo_topic:
            print(self.DASHES)
            self.topic_name = q.ask("Enter a name for your SNS topic: ", q.non_empty)
            print("Because you have selected a FIFO topic, '.fifo' must be appended to the topic name.")
            print()

            print(self.DASHES)
            print("""
    Because you have chosen a FIFO topic, deduplication is supported.
    Deduplication IDs are either set in the message or automatically generated 
    from content using a hash function.
    
    If a message is successfully published to an SNS FIFO topic, any message 
    published and determined to have the same deduplication ID, 
    within the five-minute deduplication interval, is accepted but not delivered.
    
    For more information about deduplication, 
    see https://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html.
            """)

            self.use_content_based_deduplication = q.ask(
                "Use content-based deduplication instead of entering a deduplication ID? (y/n): ", 
                q.is_yesno
            )
        else:
            self.topic_name = q.ask("Enter a name for your SNS topic: ", q.non_empty)

        print(self.DASHES)

        # Create the topic
        self.topic_arn = self.sns_wrapper.create_topic(
            self.topic_name, 
            self.use_fifo_topic, 
            self.use_content_based_deduplication
        )

        print(f"Your new topic with the name {self.topic_name}")
        print(f"  and Amazon Resource Name (ARN) {self.topic_arn}")
        print(f"  has been created.")
        print()

    def _setup_queues(self) -> None:
        """Set up the SQS queues and subscribe them to the topic."""
        print(f"Now you will create {self.queue_count} Amazon Simple Queue Service (Amazon SQS) queues to subscribe to the topic.")

        for i in range(self.queue_count):
            queue_name = q.ask(f"Enter a name for SQS queue #{i+1}: ", q.non_empty)
            
            if self.use_fifo_topic and i == 0:
                print("Because you have selected a FIFO topic, '.fifo' must be appended to the queue name.")

            # Create the queue
            queue_url = self.sqs_wrapper.create_queue(queue_name, self.use_fifo_topic)
            self.queue_urls.append(queue_url)

            print(f"Your new queue with the name {queue_name}")
            print(f"  and queue URL {queue_url}")
            print(f"  has been created.")
            print()

            if i == 0:
                print("The queue URL is used to retrieve the queue ARN,")
                print("which is used to create a subscription.")
                print(self.DASHES)

            # Get queue ARN
            queue_arn = self.sqs_wrapper.get_queue_arn(queue_url)

            if i == 0:
                print("An AWS Identity and Access Management (IAM) policy must be attached to an SQS queue,")
                print("enabling it to receive messages from an SNS topic.")

            # Set queue policy to allow SNS to send messages
            self.sqs_wrapper.set_queue_policy_for_topic(queue_arn, self.topic_arn, queue_url)

            # Set up message filtering if using FIFO
            subscription_arn = self._setup_subscription_with_filter(i, queue_arn, queue_name)
            self.subscription_arns.append(subscription_arn)

    def _setup_subscription_with_filter(self, queue_index: int, queue_arn: str, queue_name: str) -> str:
        """Set up subscription with optional message filtering."""
        filter_policy = None
        
        if self.use_fifo_topic:
            print(self.DASHES)
            if queue_index == 0:
                print("Subscriptions to a FIFO topic can have filters.")
                print("If you add a filter to this subscription, then only the filtered messages")
                print("will be received in the queue.")
                print()
                print("For information about message filtering,")
                print("see https://docs.aws.amazon.com/sns/latest/dg/sns-message-filtering.html")
                print()
                print("For this example, you can filter messages by a TONE attribute.")

            use_filter = q.ask(f"Filter messages for {queue_name}'s subscription to the topic? (y/n): ", q.is_yesno)
            
            if use_filter:
                filter_policy = self._create_filter_policy()

        subscription_arn = self.sns_wrapper.subscribe_queue_to_topic(
            self.topic_arn, queue_arn, filter_policy
        )

        print(f"The queue {queue_name} has been subscribed to the topic {self.topic_name}")
        print(f"  with the subscription ARN {subscription_arn}")

        return subscription_arn

    def _create_filter_policy(self) -> str:
        """Create a message filter policy based on user selections."""
        print(self.DASHES)
        print("You can filter messages by one or more of the following TONE attributes.")

        filter_selections = []
        selection_number = 0

        while True:
            print("Enter a number to add a TONE filter, or enter 0 to stop adding filters.")
            for i, tone in enumerate(self.tones, 1):
                print(f"  {i}. {tone}")

            selection = q.ask("Your choice: ", q.is_int, q.in_range(0, len(self.tones)))
            
            if selection == 0:
                break
            elif selection > 0 and self.tones[selection - 1] not in filter_selections:
                filter_selections.append(self.tones[selection - 1])
                print(f"Added '{self.tones[selection - 1]}' to filter list.")

        if filter_selections:
            filters = {"tone": filter_selections}
            return json.dumps(filters)
        return None

    def _publish_messages(self) -> None:
        """Publish messages to the topic with various options."""
        print("Now we can publish messages.")

        keep_sending = True
        while keep_sending:
            print()
            message = q.ask("Enter a message to publish: ", q.non_empty)

            message_group_id = None
            deduplication_id = None
            tone_attribute = None

            if self.use_fifo_topic:
                print("Because you are using a FIFO topic, you must set a message group ID.")
                print("All messages within the same group will be received in the order they were published.")
                print()
                message_group_id = q.ask("Enter a message group ID for this message: ", q.non_empty)

                if not self.use_content_based_deduplication:
                    print("Because you are not using content-based deduplication,")
                    print("you must enter a deduplication ID.")
                    deduplication_id = q.ask("Enter a deduplication ID for this message: ", q.non_empty)

                # Ask about tone attribute
                add_attribute = q.ask("Add an attribute to this message? (y/n): ", q.is_yesno)
                if add_attribute:
                    print("Enter a number for an attribute:")
                    for i, tone in enumerate(self.tones, 1):
                        print(f"  {i}. {tone}")
                    
                    selection = q.ask("Your choice: ", q.is_int, q.in_range(1, len(self.tones)))
                    if 1 <= selection <= len(self.tones):
                        tone_attribute = self.tones[selection - 1]

            # Publish the message
            message_id = self.sns_wrapper.publish_message(
                self.topic_arn,
                message,
                tone_attribute,
                deduplication_id,
                message_group_id
            )

            print(f"Message published with ID: {message_id}")

            keep_sending = q.ask("Send another message? (y/n): ", q.is_yesno)

    def _poll_queues_for_messages(self) -> None:
        """Poll all queues for messages and display results."""
        for i, queue_url in enumerate(self.queue_urls):
            print(f"Polling queue #{i+1} at {queue_url} for messages...")
            
            q.ask("Press Enter to continue...")

            messages = self._poll_queue_for_messages(queue_url)
            
            if messages:
                print(f"{len(messages)} message(s) were received by queue #{i+1}")
                for j, message in enumerate(messages, 1):
                    print(f"  Message {j}:")
                    # Parse the SNS message body to get the actual message
                    try:
                        sns_message = json.loads(message['Body'])
                        actual_message = sns_message.get('Message', message['Body'])
                        print(f"    {actual_message}")
                    except (json.JSONDecodeError, KeyError):
                        print(f"    {message['Body']}")

                # Delete the messages
                self.sqs_wrapper.delete_messages(queue_url, messages)
                print(f"Messages deleted from queue #{i+1}")
            else:
                print(f"No messages received by queue #{i+1}")
            
            print(self.DASHES)

    def _poll_queue_for_messages(self, queue_url: str) -> List[Dict[str, Any]]:
        """Poll a single queue for messages."""
        all_messages = []
        max_polls = 3  # Limit polling to avoid infinite loops
        
        for poll_count in range(max_polls):
            messages = self.sqs_wrapper.receive_messages(queue_url, 10)
            
            if messages:
                all_messages.extend(messages)
                print(f"  Received {len(messages)} messages in poll {poll_count + 1}")
                # Small delay between polls
                time.sleep(1)
            else:
                print(f"  No messages in poll {poll_count + 1}")
                break
                
        return all_messages

    def _cleanup_resources(self) -> None:
        """Clean up all resources created during the scenario."""
        print("Cleaning up resources...")

        # Delete queues
        for i, queue_url in enumerate(self.queue_urls):
            if queue_url:
                delete_queue = q.ask(f"Delete queue #{i+1} with URL {queue_url}? (y/n): ", q.is_yesno)
                if delete_queue:
                    try:
                        self.sqs_wrapper.delete_queue(queue_url)
                        print(f"Deleted queue #{i+1}")
                    except Exception as e:
                        print(f"Error deleting queue #{i+1}: {e}")

        # Unsubscribe from topic
        for i, subscription_arn in enumerate(self.subscription_arns):
            if subscription_arn:
                try:
                    self.sns_wrapper.unsubscribe(subscription_arn)
                    print(f"Unsubscribed subscription #{i+1}")
                except Exception as e:
                    print(f"Error unsubscribing #{i+1}: {e}")

        # Delete topic
        if self.topic_arn:
            delete_topic = q.ask(f"Delete topic {self.topic_name}? (y/n): ", q.is_yesno)
            if delete_topic:
                try:
                    self.sns_wrapper.delete_topic(self.topic_arn)
                    print(f"Deleted topic {self.topic_name}")
                except Exception as e:
                    print(f"Error deleting topic: {e}")

        print("Resource cleanup complete.")

# snippet-end:[python.example_code.cross_service.topics_and_queues.TopicsAndQueuesScenario]


def main() -> None:
    """
    Main function to run the Topics and Queues cross-service scenario.
    
    This example uses the default settings specified in your shared credentials
    and config files.
    """
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")
    
    try:
        # Initialize AWS clients
        sns_client = boto3.client('sns')
        sqs_client = boto3.client('sqs')
        
        # Initialize wrappers
        sns_wrapper = SnsWrapper(sns_client)
        sqs_wrapper = SqsWrapper(sqs_client)
        
        # Run the scenario
        scenario = TopicsAndQueuesScenario(sns_wrapper, sqs_wrapper)
        scenario.run_scenario()
        
    except Exception as e:
        logger.error(f"Failed to run scenario: {e}")
        print(f"Failed to run the scenario: {e}")


if __name__ == "__main__":
    main()
