// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


#pragma once
#ifndef SDK_CUSTOMIZATION_EXAMPLES_SDK_CUSTOMIZATION_GTESTS_H
#define SDK_CUSTOMIZATION_EXAMPLES_SDK_CUSTOMIZATION_GTESTS_H

#include <aws/core/Aws.h>
#include <memory>
#include <gtest/gtest.h>
#include <aws/testing/mocks/http/MockHttpClient.h>

namespace AwsDocTest {

    class MyStringBuffer : public std::stringbuf {
        int underflow() override;
    };

    class SdkCustomization_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String preconditionError();

        void AddCommandLineResponses(const std::vector<std::string> &responses);

        // s_clientConfig must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

        static Aws::String uuidName(const Aws::String &name);

        static bool
        deleteObjectInBucket(const Aws::String &bucketName, const Aws::String &key);

        static bool
        putFileInBucket(const Aws::String &bucketName, const Aws::String &key,
                        const Aws::String &filePath);

        static bool deleteBucket(const Aws::String &bucketName);

        static bool createBucket(const Aws::String &bucketName);

        static Aws::String getTestFilePath();


    private:

        static bool suppressStdOut();

        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used to silence cout.
        std::streambuf *m_savedBuffer = nullptr;

        MyStringBuffer m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;

    }; // SdkCustomization_GTests
} // AwsDocTest

#endif //SDK_CUSTOMIZATION_EXAMPLES_SDK_CUSTOMIZATION_GTESTS_H
