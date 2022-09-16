/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef S3_EXAMPLES_S3_GTESTS_H
#define S3_EXAMPLES_S3_GTESTS_H

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <memory>
#include <gtest/gtest.h>

namespace AwsDocTest {

    class DynamoDB_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        // s_clientConfig must be a pointer because the client config must be initialized after InitAPI
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

    private:
        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // used just to silence cout
        std::streambuf *m_savedOutBuffer = nullptr;

        std::stringbuf  m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;
    };
} // AwsDocTest

#endif //S3_EXAMPLES_S3_GTESTS_H
