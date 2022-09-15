/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include <aws/core/http/HttpClientFactory.h>
#include "awsdoc/s3/s3_examples.h"
#include "S3_GTests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, list_buckets_diabling_dns_cache) {

        bool result = AwsDoc::S3::ListBucketDisablingDnsCache(*s_clientConfig);
        EXPECT_TRUE(result);

        // reset the http client factory
        Aws::Http::CleanupHttp();
        Aws::Http::InitHttp();
    }
} // namespace AwsDocTest
