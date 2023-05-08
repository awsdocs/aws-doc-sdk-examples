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
 */

#include <gtest/gtest.h>
#include <fstream>
#include "aurora_gtests.h"
#include "aurora_samples.h"

namespace AwsDocTest {

    extern const std::vector<std::string> RESPONSES;

    bool addHttpResponses(MockHTTP &mockHttp);

    // Only run the un-mocked test in special cases because of its long execution time.
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(Aurora_GTests, gettingStartedWithDBClusters_2L_) {
        AddCommandLineResponses(RESPONSES);

        bool result = AwsDoc::Aurora::gettingStartedWithDBClusters(*s_clientConfig);
        ASSERT_TRUE(result);
    }

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(Aurora_GTests, gettingStartedWithDBClusters_3_) {
        AddCommandLineResponses(RESPONSES);

        MockHTTP mockHttp;
        bool result = addHttpResponses(mockHttp);
        ASSERT_TRUE(result) << preconditionError();

        result = AwsDoc::Aurora::gettingStartedWithDBClusters(*s_clientConfig);
        ASSERT_TRUE(result);
    }

    const std::vector<std::string> RESPONSES = {"1", // Which family do you want to use?
                                                "3", // Enter a new value between 1-65535:
                                                "2", // Enter a new value between 1-65535:
                                                "foo", // Enter an administrator username for the database:
                                                "foo_Foo8", // Enter a password for the administrator (at least 8 characters):
                                                "1", // The available engines for your parameter group are:
                                                "1", // Which DB instance class do you want to use?
                                                "y", // Do you want to create a snapshot of your DB cluster (y/n)?
                                                "y"}; // Do you want to delete the DB cluster, DB instance, and parameter group (y/n)?

    bool addHttpResponses(MockHTTP &mockHttp) {
        if (!mockHttp.addResponseWithBody(
                "mock_input/1-DescribeDBClusterParameterGroups.xml",
                Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/2-DescribeDBEngineVersions.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/3-CreateDBClusterParameterGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/4-DescribeDBClusterParameters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/5-DescribeDBClusterParameters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/8-ModifyDBClusterParameterGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/9-DescribeDBClusterParameters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/10-DescribeDBClusters.xml",
                                          Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/11-DescribeDBEngineVersions.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/12-CreateDBCluster.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/13-DescribeDBClusters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/52-DescribeDBClusters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/53-DescribeDBInstances.xml",
                Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/54-DescribeOrderableDBInstanceOptions.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/55-CreateDBInstance.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/56-DescribeDBInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/448-DescribeDBInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/449-CreateDBClusterSnapshot.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/450-DescribeDBClusterSnapshots.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/625-DescribeDBClusterSnapshots.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/626-DeleteDBInstance.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/627-DeleteDBCluster.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/628-DescribeDBInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/629-DescribeDBClusters.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/1532-DescribeDBInstances.xml",
                                          Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/1533-DescribeDBClusters.xml",
                                          Aws::Http::HttpResponseCode::NOT_FOUND)) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/1534-DeleteDBClusterParameterGroup.xml")) {
            return false;
        }

        return true;
    }
} // namespace AwsDocTest
