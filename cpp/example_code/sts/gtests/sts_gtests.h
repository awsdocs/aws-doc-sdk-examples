/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef S3_EXAMPLES_S3_GTESTS_H
#define S3_EXAMPLES_S3_GTESTS_H

#include <aws/core/Aws.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/iam/model/Role.h>
#include <memory>
#include <gtest/gtest.h>

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
        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used just to silence std::cout.
        std::streambuf *m_savedBuffer = nullptr;

        static Aws::IAM::Model::Role s_role;
        static Aws::String s_userArn;
    };
} // AwsDocTest

#endif //S3_EXAMPLES_S3_GTESTS_H
