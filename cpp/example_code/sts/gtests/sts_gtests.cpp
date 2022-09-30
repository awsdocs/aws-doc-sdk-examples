/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "service_gtests.h"
#include <fstream>

Aws::SDKOptions AwsDocTest::S3_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::S3_GTests::s_clientConfig;

void AwsDocTest::S3_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized after InitAPI
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::S3_GTests::TearDownTestSuite() {
     ShutdownAPI(s_options);

}

void AwsDocTest::S3_GTests::SetUp() {
    m_savedBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);
}

void AwsDocTest::S3_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }
}



