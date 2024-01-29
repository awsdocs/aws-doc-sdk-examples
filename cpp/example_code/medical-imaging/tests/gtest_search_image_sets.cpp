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
#include "medical-imaging_samples.h"
#include "medical-imaging_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(MedicalImaging_GTests, search_image_sets_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/1-searchImageSets.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;
        result = mockHttp.addResponseWithBody("mock_input/2-searchImageSets.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        const Aws::MedicalImaging::Model::SearchCriteria searchCriteria;
        Aws::Vector<Aws::String> imageSetResults;
        result = AwsDoc::Medical_Imaging::searchImageSets(
                "12345678901234567890123456789012",
                searchCriteria,
                imageSetResults,
                *s_clientConfig);
        ASSERT_TRUE(result);
        ASSERT_EQ(2, imageSetResults.size());
    }
} // namespace AwsDocTest
