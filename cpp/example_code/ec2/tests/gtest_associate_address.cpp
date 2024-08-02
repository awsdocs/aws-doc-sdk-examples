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
    TEST_F(EC2_GTests, associate_address_2_) {
        Aws::String allocationID = allocateIPAddress();
        ASSERT_FALSE(allocationID.empty()) << preconditionError() << std::endl;

        Aws::String instanceID = getCachedInstanceID();
        ASSERT_FALSE(instanceID.empty()) << preconditionError() << std::endl;

        Aws::String associationID;

        bool result = AwsDoc::EC2::associateAddress(instanceID, allocationID, associationID, *s_clientConfig);
        EXPECT_TRUE(result);

        if (result) {
            result = dissociateAddress(associationID);
            EXPECT_TRUE(result);
        }

        result = releaseIPAddress(allocationID);
        EXPECT_TRUE(result);
    }
} // namespace AwsDocTest
