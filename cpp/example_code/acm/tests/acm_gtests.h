// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef ACM_EXAMPLES_S3_GTESTS_H
#define ACM_EXAMPLES_S3_GTESTS_H

#include <aws/core/Aws.h>
#include <memory>
#include <gtest/gtest.h>
#include <aws/testing/mocks/http/MockHttpClient.h>

namespace AwsDocTest {

    class MyStringBuffer : public std::stringbuf {
        int underflow() override;
    };

    class ACM_GTests : public testing::Test {
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

        static std::string testCertificateArn() {
            return "arn:aws:acm:region:123456789012:certificate/12345678-1234-1234-1234-123456789012";
        };

    private:

        static bool suppressStdOut();

        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used to silence cout.
        std::streambuf *m_savedBuffer = nullptr;

        MyStringBuffer m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;
    }; // ACM_GTests

    class MockHTTP {
    public:
        MockHTTP();

        virtual ~MockHTTP();

        bool addResponseWithBody(const std::string &fileName,
                                 Aws::Http::HttpResponseCode httpResponseCode = Aws::Http::HttpResponseCode::OK);

    private:

        std::shared_ptr<MockHttpClient> mockHttpClient;
        std::shared_ptr<MockHttpClientFactory> mockHttpClientFactory;
        std::shared_ptr<Aws::Http::HttpRequest> requestTmp;
    }; // MockHTTP
} // AwsDocTest

#endif //ACM_EXAMPLES_S3_GTESTS_H
