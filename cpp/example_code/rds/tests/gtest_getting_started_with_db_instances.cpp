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
 * -*L_ Designates a test with a long execution time.
 *
 */

#include <gtest/gtest.h>
#include <fstream>
#include "rds_gtests.h"
#include "rds_samples.h"

namespace AwsDocTest {

    extern const std::vector<std::string> RESPONSES;

    bool addHttpResponses(MockHTTP &mockHttp);

    // Only run the un-mocked test in special cases because of its long execution time.
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(RDS_GTests, gettingStartedWithDBInstances_2L_) {
        AddCommandLineResponses(RESPONSES);

        bool result = AwsDoc::RDS::gettingStartedWithDBInstances(*s_clientConfig);
        ASSERT_TRUE(result);
    }

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(RDS_GTests, gettingStartedWithDBInstances_3_) {
        AddCommandLineResponses(RESPONSES);

        MockHTTP mockHttp;
        bool result = addHttpResponses(mockHttp);
        ASSERT_TRUE(result) << preconditionError();

        result = AwsDoc::RDS::gettingStartedWithDBInstances(*s_clientConfig);
        ASSERT_TRUE(result);
    }

    const std::vector<std::string> RESPONSES = {"1", // Which family do you want to use?
                                                "3", // Enter a new value in the range 1-65535:
                                                "2", // Enter a new value in the range 1-65535:
                                                "foo", // Enter an administrator username for the database:
                                                "foo_Foo8", // Enter a password for the administrator (at least 8 characters):
                                                "1", // The available engines for your parameter group are:
                                                "1", // Which micro DB instance class do you want to use?
                                                "y", // Do you want to create a snapshot of your DB instance (y/n)?
                                                "y"}; // Do you want to delete the DB instance and parameter group (y/n)?

    bool addHttpResponses(MockHTTP &mockHttp) {
        if (!mockHttp.addResponseWithBody("mock_input/1-DescribeDBParameterGroups.xml",
                                          Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/2-DescribeDBEngineVersions.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/3-CreateDBParameterGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/4-DescribeDBParameters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/8-DescribeDBParameters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/9-ModifyDBParameterGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/10-DescribeDBParameters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/11-DescribeDBInstances.xml",
                                          Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/12-DescribeDBEngineVersions.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/13-DescribeOrderableDBInstanceOptions.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/15-CreateDBInstance.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/16-DescribeDBInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/457-DescribeDBInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/458-CreateDBSnapshot.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/459-DescribeDBSnapshots.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/564-DescribeDBSnapshots.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/565-DeleteDBInstance.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/566-DescribeDBInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/666-DescribeDBInstances.xml",
                                          Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/667-DeleteDBParameterGroup.xml")) {
            return false;
        }

        return true;
    }
} // namespace AwsDocTest
