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
#include "ec2_samples.h"
#include "ec2_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(EC2_GTests, allocate_address_2_) {

        const Aws::String instanceID = getCachedInstanceID();
        ASSERT_FALSE(instanceID.empty()) << preconditionError() << std::endl;

        Aws::String allocationID;
        auto result = AwsDoc::EC2::AllocateAndAssociateAddress(instanceID,
                                                               allocationID,
                                                               *s_clientConfig);
        ASSERT_TRUE(result);

        releaseIPAddress(allocationID);
    }
} // namespace AwsDocTest
