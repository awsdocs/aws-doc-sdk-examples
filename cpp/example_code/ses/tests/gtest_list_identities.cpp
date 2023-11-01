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
#include "ses_samples.h"
#include "ses_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(SES_GTests, list_identities_1_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/ListIdentities.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::Vector<Aws::String> identities;
        result = AwsDoc::SES::listIdentities(Aws::SES::Model::IdentityType::EmailAddress, identities, *s_clientConfig);
        ASSERT_TRUE(result);

        ASSERT_FALSE(identities.empty());
    }
} // namespace AwsDocTest
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
