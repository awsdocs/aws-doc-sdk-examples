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
    TEST_F(EC2_GTests, create_tags_2_) {
        Aws::String instanceID = getCachedInstanceID();
        ASSERT_FALSE(instanceID.empty()) << preconditionError() << std::endl;
        Aws::Vector<Aws::String> resources;
        resources.push_back(instanceID);

        Aws::Vector<Aws::EC2::Model::Tag> tags;
        tags.push_back(Aws::EC2::Model::Tag().WithKey("Name").WithValue("Test"));
        bool result = AwsDoc::EC2::createTags(resources, tags, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
