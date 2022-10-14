/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, get_server_certificate) {
        auto result = AwsDoc::IAM::getServerCertificate("non-existent-certificate",
                                                        *s_clientConfig);
        EXPECT_TRUE(result);  // the routine will return true if certificate not found.
    }
} // namespace AwsDocTest
