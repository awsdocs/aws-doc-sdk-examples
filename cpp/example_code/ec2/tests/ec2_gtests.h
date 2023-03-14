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
#include <aws/ec2/model/InstanceStateName.h>

namespace AwsDocTest {

    class MyStringBuffer : public std::stringbuf {
        int underflow() override;
    };

    class EC2_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String preconditionError();

        void AddCommandLineResponses(const std::vector<std::string> &responses);

        static Aws::String allocateIPAddress();

        static bool releaseIPAddress(const Aws::String &allocationID);

        static bool terminateInstance(const Aws::String &instanceID);

        static Aws::String createInstance();

        static Aws::EC2::Model::InstanceStateName
        getInstanceState(const Aws::String &instanceID);

        static Aws::EC2::Model::InstanceStateName
        waitWhileInstanceInState(const Aws::String &instanceID,
                                 Aws::EC2::Model::InstanceStateName waitState);

        static Aws::String getAmiID();

        static Aws::String uuidName(const Aws::String &name);

        static Aws::String getCachedInstanceID();

        static bool createKeyPair(const Aws::String &keyPairName);

        static bool deleteKeyPair(const Aws::String &keyPairName);

        static Aws::String createSecurityGroup(const Aws::String &groupName);

        static bool deleteSecurityGroup(const Aws::String &groupID);

        static Aws::String getVpcID();

        // s_clientConfig must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

    private:

        bool suppressStdOut();

        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used to silence cout.
        std::streambuf *m_savedBuffer = nullptr;

        MyStringBuffer m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;

        static Aws::String s_instanceID;
        static Aws::String s_vpcID;
    }; // EC2_GTests
} // AwsDocTest

#endif //S3_EXAMPLES_S3_GTESTS_H
