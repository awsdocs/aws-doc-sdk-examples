/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
#include <gtest/gtest.h>
#include <fstream>
#include "autoscaling_gtests.h"
#include "autoscaling_samples.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(AutoScaling_GTests, groups_and_instances_scenario) {
        AddCommandLineResponses({"n", // "Would you like to use an existing EC2 launch template (y/n)?"
                                 "integration_tests_template", // "Enter the name for a new EC2 launch template: "
                                 "integration_tests_group", // "Enter a name for the EC2 Auto Scaling group: "
                                 "1", // "Choose an availability zone: "
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
        });

    bool result = AwsDoc::AutoScaling::groupsAndInstancesScenario(*s_clientConfig);
    ASSERT_TRUE(result);
}
} // AwsDocTest