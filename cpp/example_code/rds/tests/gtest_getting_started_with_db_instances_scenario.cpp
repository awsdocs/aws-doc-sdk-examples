/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "rds_gtests.h"
#include "rds_samples.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(RDS_GTests, delete_bucket) {
        AddCommandLineResponses({"foo", // Enter a new user name:
                                 "foo_1foo", // Enter a new password:
                                 "foo@bar.com", // Enter a valid email for the user:
                                 "y", // Would you like to send a new code? (y/n)
                                 "888888", // Enter the confirmation code that was emailed:
                                 "", // Type enter to continue...
                                 "888888", // Enter the 6 digit code displayed in the authenticator app:
                                 "888888", // Re-enter the 6 digit code displayed in the authenticator app:
                                 "y"} // Would you like to delete the user you created? (y/n)
        );

        bool result = AwsDoc::RDS::gettingStartedWithDBInstances(*s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
