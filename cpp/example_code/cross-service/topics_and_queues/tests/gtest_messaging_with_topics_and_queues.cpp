// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include "topics_and_queues_samples.h"
#include "topics_and_queues_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(TopicsAndQueues_GTests, getting_startedV1_2_) {

        AddCommandLineResponses({
                                        "n", // Would you like to work with FIFO topics? (y/n)
                                        "tests_v1", // Enter a name for your SNS topic.
                                        "tests_v1_1", // Enter a name for an SQS queue.
                                        "tests_v1_2", // Enter a name for the next SQS queue.
                                        "test message v1 1", // Enter a message text to publish.
                                        "y", // Post another message? (y/n)
                                        "test message v1 2", // Enter a message text to publish.
                                        "n", // Post another message? (y/n)
                                        "", // Press any key to continue...
                                        "y", // Delete the SQS queues? (y/n)
                                        "y" // Delete the SNS topic? (y/n)
                                });

        auto result = AwsDoc::TopicsAndQueues::messagingWithTopicsAndQueues(
                *s_clientConfig);
        ASSERT_TRUE(result);
    }

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(TopicsAndQueues_GTests, getting_startedV2_2_) {

        AddCommandLineResponses({
                                        "y", // Would you like to work with FIFO topics? (y/n)
                                        "y", // Use content-based deduplication instead of entering a deduplication ID? (y/n)
                                        "tests_v2", // Enter a name for your SNS topic.
                                        "tests_v2_1", // Enter a name for an SQS queue.
                                        "n", // Filter messages for ""tests_v2_1".fifo"'s subscription to the topic "tests_v2.fifo"?  (y/n)
                                        "tests_v2_2", // Enter a name for the next SQS queue.
                                        "n", // Filter messages for ""tests_v2_1".fifo"'s subscription to the topic "tests_v2.fifo"?  (y/n)
                                        "test message v2 1", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "y", // Post another message? (y/n)
                                        "test message v2 2", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "n", // Post another message? (y/n)
                                        "", // Press any key to continue...
                                        "y", // Delete the SQS queues? (y/n)
                                        "y" // Delete the SNS topic? (y/n)
                                });

        auto result = AwsDoc::TopicsAndQueues::messagingWithTopicsAndQueues(
                *s_clientConfig);
        ASSERT_TRUE(result);
    }

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(TopicsAndQueues_GTests, getting_startedV3_2_) {

        AddCommandLineResponses({
                                        "y", // Would you like to work with FIFO topics? (y/n)
                                        "n", // Use content-based deduplication instead of entering a deduplication ID? (y/n)
                                        "tests_v3", // Enter a name for your SNS topic.
                                        "tests_v3_1", // Enter a name for an SQS queue.
                                        "n", // Filter messages for ""tests_v2_1".fifo"'s subscription to the topic "tests_v2.fifo"?  (y/n)
                                        "tests_v3_2", // Enter a name for the next SQS queue.
                                        "n", // Filter messages for ""tests_v2_1".fifo"'s subscription to the topic "tests_v2.fifo"?  (y/n)
                                        "test message v3 1", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "1", // Enter a deduplication ID for this message.
                                        "y", // Post another message? (y/n)
                                        "test message v3 2", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "2", // Enter a deduplication ID for this message.
                                        "n", // Post another message? (y/n)
                                        "", // Press any key to continue...
                                        "y", // Delete the SQS queues? (y/n)
                                        "y" // Delete the SNS topic? (y/n)
                                });

        auto result = AwsDoc::TopicsAndQueues::messagingWithTopicsAndQueues(
                *s_clientConfig);
        ASSERT_TRUE(result);
    }

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(TopicsAndQueues_GTests, getting_startedV4_2_) {

        AddCommandLineResponses({
                                        "y", // Would you like to work with FIFO topics? (y/n)
                                        "y", // Use content-based deduplication instead of entering a deduplication ID? (y/n)
                                        "tests_v4", // Enter a name for your SNS topic.
                                        "tests_v4_1", // Enter a name for an SQS queue.
                                        "y", // Filter messages for ""tests_v4_1".fifo"'s subscription to the topic "tests_v4.fifo"?  (y/n)
                                        "1", // Enter a number (or enter zero to stop adding more).
                                        "2", // Enter a number (or enter zero to stop adding more).
                                        "0", // Enter a number (or enter zero to stop adding more).
                                        "tests_v4_2", // Enter a name for the next SQS queue.
                                        "n", // Filter messages for ""tests_v4_1".fifo"'s subscription to the topic "tests_v4.fifo"?  (y/n)
                                        "test message v4 1", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "1", // Enter a deduplication ID for this message.
                                        "y", // Add an attribute to this message? (y/n)
                                        "1", // Enter a number for an attribute.
                                        "y", // Post another message? (y/n)
                                        "test message v4 2", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "2", // Enter a deduplication ID for this message.
                                        "n", // Add an attribute to this message? (y/n)
                                        "n", // Post another message? (y/n)
                                        "", // Press any key to continue...
                                        "y", // Delete the SQS queues? (y/n)
                                        "y" // Delete the SNS topic? (y/n)
                                });

        auto result = AwsDoc::TopicsAndQueues::messagingWithTopicsAndQueues(
                *s_clientConfig);
        ASSERT_TRUE(result);
    }


    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(TopicsAndQueues_GTests, getting_startedV5_2_) {

        AddCommandLineResponses({
                                        "y", // Would you like to work with FIFO topics? (y/n)
                                        "n", // Use content-based deduplication instead of entering a deduplication ID? (y/n)
                                        "tests_v5", // Enter a name for your SNS topic.
                                        "tests_v5_1", // Enter a name for an SQS queue.
                                        "y", // Filter messages for ""tests_v5_1".fifo"'s subscription to the topic "tests_v5.fifo"?  (y/n)
                                        "1", // Enter a number (or enter zero to stop adding more).
                                        "2", // Enter a number (or enter zero to stop adding more).
                                        "0", // Enter a number (or enter zero to stop adding more).
                                        "tests_v5_2", // Enter a name for the next SQS queue.
                                        "n", // Filter messages for ""tests_v5_1".fifo"'s subscription to the topic "tests_v5.fifo"?  (y/n)
                                        "test message v5 1", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "y", // Add an attribute to this message? (y/n)
                                        "1", // Enter a number for an attribute.
                                        "y", // Post another message? (y/n)
                                        "test message v5 2", // Enter a message text to publish.
                                        "1", // Enter a message group ID for this message.
                                        "n", // Add an attribute to this message? (y/n)
                                        "n", // Post another message? (y/n)
                                        "", // Press any key to continue...
                                        "y", // Delete the SQS queues? (y/n)
                                        "y" // Delete the SNS topic? (y/n)
                                });

        auto result = AwsDoc::TopicsAndQueues::messagingWithTopicsAndQueues(
                *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
