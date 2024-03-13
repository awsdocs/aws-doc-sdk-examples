// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef STS_EXAMPLES_STS_GTESTS_H
#define STS_EXAMPLES_STS_GTESTS_H

#include <aws/core/Aws.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/iam/model/Role.h>
#include <memory>
#include <gtest/gtest.h>
#include <aws/testing/mocks/http/MockHttpClient.h>

namespace AwsDocTest {

    class STS_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String uuidName(const Aws::String &name);

        static Aws::String preconditionError();

        static Aws::String getRoleArn();

        static Aws::IAM::Model::Role createRole();

        static Aws::String getUserArn();

        static void deleteRole(const Aws::String &role);

        static Aws::String getAssumeRolePolicyJSON();

        // "s_clientConfig" must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

    private:

        bool suppressStdOut();

        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used just to silence std::cout.
        std::streambuf *m_savedBuffer = nullptr;

        static Aws::IAM::Model::Role s_role;
        static Aws::String s_userArn;
    };


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

#endif //STS_EXAMPLES_STS_GTESTS_H
