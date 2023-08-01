/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[javascript.v3.wkflw.sns.messages]
export const MESSAGES = {
  welcome: "Welcome to messaging with topics and queues.",
  description:
    "In this workflow, you will create an SNS topic and subscribe 2 SQS queues to the topic. " +
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
};
// snippet-end:[javascript.v3.wkflw.sns.messages]
