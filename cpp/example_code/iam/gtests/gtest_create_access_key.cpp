/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest { 
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, create_access_key) {
        auto userName = getUser();
        ASSERT_FALSE(userName.empty())  << preconditionError() << std::endl;

        auto accessKeyID = AwsDoc::IAM::createAccessKey(userName, *s_clientConfig);
        ASSERT_FALSE(accessKeyID.empty());

        deleteAccessKey(accessKeyID);
    }
} // namespace AwsDocTest
