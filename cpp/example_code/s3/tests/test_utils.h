/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef S3_EXAMPLES_TESTUTILS_H
#define S3_EXAMPLES_TESTUTILS_H

#include <aws/iam/model/GetUserRequest.h>
#include <aws/core/client/ClientConfiguration.h>

namespace AwsTest {
    class TestUtils {
    public:
        static Aws::String getArnForUser(const Aws::Client::ClientConfiguration &clientConfig);
    };
} // AwsTest

#endif //S3_EXAMPLES_TESTUTILS_H
