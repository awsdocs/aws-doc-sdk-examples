// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials and pre-configured resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include <fstream>
#include "autoscaling_gtests.h"
#include "autoscaling_samples.h"

namespace AwsDocTest {
    extern const std::vector<std::string> RESPONSES;
    bool addHttpResponses(MockHTTP &mockHttp);


    // Only run the un-mocked test in special cases because of its long execution time.
    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(AutoScaling_GTests, groups_and_instances_scenario_2L_) {
        AddCommandLineResponses(RESPONSES);

        bool result = AwsDoc::AutoScaling::groupsAndInstancesScenario(*s_clientConfig);
        ASSERT_TRUE(result);
    }

    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(AutoScaling_GTests, groups_and_instances_scenario_3_) {
        AddCommandLineResponses(RESPONSES);
        MockHTTP mockHttp;
        bool result = addHttpResponses(mockHttp);
        ASSERT_TRUE(result) << preconditionError();

        result = AwsDoc::AutoScaling::groupsAndInstancesScenario(*s_clientConfig);
        ASSERT_TRUE(result);
    }

    const std::vector<std::string> RESPONSES = {
            "n", // "Would you like to use an existing EC2 launch template (y/n)?"
            "integration_tests_template", // "Enter the name for a new EC2 launch template: "
            "integration_tests_group", // "Enter a name for the Amazon EC2 Auto Scaling group: "
            "1", // "Choose an Availability Zone: "
            "y", // "Do you want to collect metrics about the Amazon EC2 Auto Scaling during this demo (y/n)?"
            "", // "Press enter to continue:"
            "", // "Press enter to continue:"
            "1", // "Which EC2 instance do you want to stop?"
            "", // "Press enter to continue:"
            "1", // "Which metric would you like to view?  "
            "n",  // "Would you like to view another metric (y/n)?  "
            "", // "Press enter to continue:"
            "y", // "Delete the EC2 Auto Scaling group 'integration_tests_group'  (y/n)?"
            "y" // "Delete the EC2 launch template 'integration_tests_template' (y/n)?"
    };

    bool addHttpResponses(MockHTTP &mockHttp) {
        if (!mockHttp.addResponseWithBody(
                "mock_input/1-CreateLaunchTemplate.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/2-DescribeAvailabilityZones.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/3-CreateAutoScalingGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/4-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/5-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/6-DescribeAutoScalingInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/7-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/8-EnableMetricsCollection.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/9-UpdateAutoScalingGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/10-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/11-SetDesiredCapacity.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/12-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/13-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/14-DescribeAutoScalingInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/15-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/16-TerminateInstanceInAutoScalingGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/17-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/72-DescribeAutoScalingInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/73-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/74-DescribeScalingActivities.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/75-ListMetrics.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/76-GetMetricStatistics.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/77-DisableMetricsCollection.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/78-UpdateAutoScalingGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/79-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody("mock_input/80-TerminateInstanceInAutoScalingGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/81-TerminateInstanceInAutoScalingGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/82-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/133-DescribeAutoScalingInstances.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/134-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/135-DescribeAutoScalingGroups.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/136-DeleteAutoScalingGroup.xml")) {
            return false;
        }
        if (!mockHttp.addResponseWithBody(
                "mock_input/137-DeleteLaunchTemplate.xml")) {
            return false;
        }

        return true;
    }

} // AwsDocTest