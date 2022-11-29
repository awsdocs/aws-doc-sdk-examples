/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "glue_samples.h"
#include "glue_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(Glue_GTests, glue_getting_started_scenario) {
        const char* env_var = std::getenv("SCENARIO_ROLE_NAME");
        ASSERT_NE(env_var, nullptr) << preconditionError();
        Aws::String roleName(env_var);
        env_var = std::getenv("SCENARIO_BUCKET_NAME");
        ASSERT_NE(env_var, nullptr) << preconditionError();
        Aws::String bucketName(env_var);

        auto result = AwsDoc::Glue::runGettingStartedWithGlueScenario(bucketName, roleName, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
