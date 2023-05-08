/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef S3_EXAMPLES_S3_GTESTS_H
#define S3_EXAMPLES_S3_GTESTS_H

#include <aws/core/Aws.h>
#include <memory>
#include <gtest/gtest.h>

namespace AwsDocTest {

    class MyStringBuffer : public std::stringbuf {
        int underflow() override;
    };

    class SQS_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String preconditionError();

        void AddCommandLineResponses(const std::vector<std::string> &responses);

        static Aws::String uuidName(const Aws::String &name);

        static bool deleteQueueWithName(const Aws::String &name);

        static Aws::String getQueueUrl(const Aws::String &name);

        static bool deleteQueueWithUrl(const Aws::String &queueUrl);

        static Aws::String createQueue(const Aws::String &name);

        static Aws::String getCachedQueueUrl();

        static Aws::String getMessageReceiptHandle();

        static Aws::String getQueueArn(const Aws::String &queueUrl);

        static bool sendMessage(const Aws::String &queueUrl,
                                const Aws::String &messageText);

        // s_clientConfig must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

    private:

        static bool suppressStdOut();

        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used to silence cout.
        std::streambuf *m_savedBuffer = nullptr;

        MyStringBuffer m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;

        static Aws::String s_cachedQueueUrl;
    }; // SQS_GTests
} // AwsDocTest

#endif //S3_EXAMPLES_S3_GTESTS_H
