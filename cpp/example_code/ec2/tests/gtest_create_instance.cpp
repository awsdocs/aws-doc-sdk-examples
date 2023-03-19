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
    TEST_F(EC2_GTests, create_instance_2_) {
        auto amiID = getAmiID();
        ASSERT_FALSE(amiID.empty()) << preconditionError() << std::endl;

        auto instanceName = uuidName("test-create");

        Aws::String instanceID;
        auto result = AwsDoc::EC2::RunInstance(instanceName, amiID,
                                               instanceID, *s_clientConfig);
        ASSERT_TRUE(result);

        terminateInstance(instanceID);
    }

} // namespace AwsDocTest
