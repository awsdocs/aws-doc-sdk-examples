// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


#pragma once
#ifndef REKOGNITION_EXAMPLES_REKOGNITION_GTESTS_H
#define REKOGNITION_EXAMPLES_REKOGNITION_GTESTS_H

#include <aws/core/Aws.h>
#include <memory>
#include <gtest/gtest.h>

namespace AwsDocTest {

    class MyStringBuffer : public std::stringbuf {
        int underflow() override;
    };

    class Rekognition_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String preconditionError();

        void AddCommandLineResponses(const std::vector<std::string> &responses);

        Aws::String uuidName(const Aws::String &prefix);

        Aws::String getImageBucket();

        Aws::String getImageFileName();

        bool uploadImage(const Aws::String &bucketName, const Aws::String &imageFileName,
                         const Aws::String &keyName);

        // s_clientConfig must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

    private:

        static bool suppressStdOut();

        static void deleteBucket(const Aws::String &bucketName);

        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used to silence cout.
        std::streambuf *m_savedBuffer = nullptr;

        MyStringBuffer m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;

        static Aws::String s_bucketName;
    }; // Rekognition_GTests
} // AwsDocTest

#endif //REKOGNITION_EXAMPLES_REKOGNITION_GTESTS_H
