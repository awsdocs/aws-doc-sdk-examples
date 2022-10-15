// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <gtest/gtest.h>
#include <aws/core/Aws.h>
#include <fstream>
#include "iam_samples.h"


TEST(IAMScenarioTest, Test_valid_arguments) {
    Aws::SDKOptions options;
    InitAPI(options);
    Aws::Client::ClientConfiguration clientConfig;

    EXPECT_TRUE(AwsDoc::IAM::IAMCreateUserAssumeRoleScenario(clientConfig, false));

    ShutdownAPI(options);
}