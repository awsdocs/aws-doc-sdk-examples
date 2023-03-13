/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, get_server_certificate_2_) {
        auto result = AwsDoc::IAM::getServerCertificate("non-existent-certificate",
                                                        *s_clientConfig);
        EXPECT_TRUE(result);  // The routine will return true if certificate not found.
    }
} // namespace AwsDocTest
