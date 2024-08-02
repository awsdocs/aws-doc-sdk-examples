// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include "ec2_samples.h"
#include "ec2_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(EC2_GTests, terminate_instances_2_) {
        auto instanceID = getCachedInstanceID();
        ASSERT_FALSE(instanceID.empty()) << preconditionError << std::endl;

        auto result = AwsDoc::EC2::terminateInstances(instanceID, *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
