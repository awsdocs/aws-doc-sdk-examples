// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.wkflw.topicsandqueues.messages]
export const MESSAGES = {
  headerWelcome: "Welcome to messaging with topics and queues",
  headerFifo: "FIFO",
  headerDedup: "Deduplication",
  headerCreateTopic: "Create SNS topic",
  headerCreateQueues: "Create SQS queues",
  headerAttachPolicy: "Attach IAM policies to SQS queues",
  headerSubscribeQueues: "Subscribe SQS queues to an SNS topic",
  headerPublishMessage: "Publish messages to an SNS topic",
  headerReceiveMessages: "Receive and delete messages",
  description:
    "In this scenario, you will create an SNS topic and subscribe 2 SQS queues to the topic. " +
    "You can select from several options for configuring the topic and the subscriptions " +
    "for the 2 queues. You can then post to the topic and see the results in the queues.",
  snsFifoDescription:
    "SNS topics can be configured as FIFO (First-In-First-Out). " +
    "FIFO topics deliver messages in order and support deduplication and message filtering.",
  snsFifoPrompt: "Would you like to work with FIFO topics?",
  deduplicationNotice:
    "Because you have chosen a FIFO topic, deduplication is supported.",
  deduplicationDescription:
    "Deduplication IDs are either set in the message or automatically generated from " +
    "content using a hash function. If a message is successfully published to an SNS " +
    "FIFO topic, any message published and determined to have the same deduplication ID, " +
    "within the five-minute deduplication interval, is accepted but not delivered. For " +
    "more information about deduplication, see https://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html.",
  deduplicationPrompt:
    "Would you like to use content-based deduplication instead of entering a deduplication ID?",
  creatingTopics:
    "Create a new SNS topic in your account. Messages will be published to this topic.",
  headerFifoNaming: "FIFO naming schemes",
  topicNamePrompt:
    "Enter a name for your SNS topic. For example, 'breaking-news'.",
  appendFifoNotice:
    "Because you have selected a FIFO topic/queue, '.fifo' must be appended to the name.",
  topicCreatedNotice:
    "Your new topic with the name '${TOPIC_NAME}' and the topic " +
    "Amazon Resource Name (ARN) '${TOPIC_ARN}' has been created.",
  createQueuesNotice: "Now you will create 2 SQS queues.",
  queueNamePrompt:
    "Enter a name for an SQS queue. For example, '${EXAMPLE_NAME}'.",
  queueCreatedNotice:
    "Your new SQS queue with the name '${QUEUE_NAME}' and the queue URL " +
    "'${QUEUE_URL}' and the ARN '${QUEUE_ARN}' has been created. ",
  queueCount: "Let's create queue ${COUNT}.",
  attachPolicyNotice:
    "To allow an SQS queue to receive messages from an SNS topic, you must attach a policy.",
  addPolicyConfirmation:
    "Do you want to attach this policy to the ${QUEUE_NAME}?",
  policyNotAttachedNotice:
    "No policy was attached to ${QUEUE_NAME}. It will not be able to receive messages from the topic.",
  fifoFilterNotice:
    "Subscriptions to a FIFO topic can have filters. If you add a filter to " +
    "this subscription, then only the filtered messages will be received in the queue. " +
    "For information about message filtering, see https://docs.aws.amazon.com/sns/latest/dg/sns-message-filtering.html. " +
    "For this example, you can filter messages by a 'tone' attribute.",
  fifoFilterSelect:
    "Select any of the following tones. ${QUEUE_NAME} will only receive messages with the chosen tones.",
  queueSubscribedNotice:
    "The queue '${QUEUE_NAME}' has been subscribed to the topic '${TOPIC_NAME}'. " +
    "Only messages that match the filter policy will be sent to the queue. " +
    "The filter policy for this queue matches the following tones: ${TONES}",
  publishMessagePrompt: "Enter a message to publish to the topic",
  publishAnother: "Would you like to publish another message?",
  groupIdNotice:
    "Because you are using a FIFO topic, you must provide a group ID. All messages " +
    "with the same group ID will be received in the order they were published.",
  groupIdPrompt: "Enter a group ID for this message",
  deduplicationIdNotice:
    "Because you are not using content-based deduplication, you must enter a deduplication ID.",
  deduplicationIdPrompt: "Enter a deduplication ID for this message",
  messageAttributesPrompt:
    "Select any number of attributes to add to the message",
  deleteAndPollConfirmation:
    "Any above messages have been deleted. Would you like to poll for messages again?",
  messagesReceivedNotice:
    "The following messages were received by the SQS queue '${QUEUE_NAME}'.",
  noMessagesReceivedNotice:
    "No messages were received by the SQS queue '${QUEUE_NAME}'.",
};
// snippet-end:[javascript.v3.wkflw.topicsandqueues.messages]
