/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef AUTOSCALING_EXAMPLES_AUTOSCALING_GTESTS_H
#define AUTOSCALING_EXAMPLES_AUTOSCALING_GTESTS_H

#include <aws/core/Aws.h>
#include <memory>
#include <gtest/gtest.h>

namespace AwsDocTest {

    class AutoScaling_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String preconditionError();

        // s_clientConfig must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

    private:
        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used to silence cout.
        std::streambuf *m_savedBuffer = nullptr;
    };
} // AwsDocTest

#endif // AUTOSCALING_EXAMPLES_AUTOSCALING_GTESTS_H
