/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates using an Amazon Elastic Compute Cloud (Amazon EC2) Auto Scaling group
 * to manage Amazon EC2 instances.
 *
 * 1.  Specify the name of an existing EC2 launch template.
 * 2.   Or create a new EC2 launch template.
 * 3.  Retrieve a list of EC2 Availability Zones.
 * 4.  Create an Auto Scaling group with the specified Availability Zone.
 * 5.  Retrieve a description of the Auto Scaling group.
 * 6.  Check lifecycle state of the EC2 instances using DescribeAutoScalingInstances.
 * 7.  Optionally enable metrics collection for the Auto Scaling group.
 * 8.  Update the Auto Scaling group, setting a new maximum size.
 * 9.  Update the Auto Scaling group, setting a new desired capacity.
 * 10. Terminate an EC2 instance in the Auto Scaling group.
 * 11. Get a description of activities for the Auto Scaling group.
 * 12. Optionally list the metrics for the Auto Scaling group.
 * 13. Disable metrics collection if enabled.
 * 14. Delete the Auto Scaling group.
 * 15. Delete the EC2 launch template.
 *
 */

#include <thread>
#include <iostream>
#include <iomanip>
#include <aws/core/Aws.h>
#include <aws/autoscaling/AutoScalingClient.h>
#include <aws/autoscaling/model/CreateAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/DeleteAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/DescribeScalingActivitiesRequest.h>
#include <aws/autoscaling/model/DescribeAutoScalingGroupsRequest.h>
#include <aws/autoscaling/model/DescribeAutoScalingInstancesRequest.h>
#include <aws/autoscaling/model/DisableMetricsCollectionRequest.h>
#include <aws/autoscaling/model/EnableMetricsCollectionRequest.h>
#include <aws/autoscaling/model/SetDesiredCapacityRequest.h>
#include <aws/autoscaling/model/TerminateInstanceInAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/UpdateAutoScalingGroupRequest.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateLaunchTemplateRequest.h>
#include <aws/ec2/model/DeleteLaunchTemplateRequest.h>
#include <aws/ec2/model/DescribeAvailabilityZonesRequest.h>
#include <aws/ec2/model/DescribeLaunchTemplatesRequest.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/GetMetricStatisticsRequest.h>
#include <aws/monitoring/model/ListMetricsRequest.h>
#include "autoscaling_samples.h"

namespace AwsDoc {
    namespace AutoScaling {
        static const Aws::String EC2_LAUNCH_TEMPLATE_IMAGE_ID("ami-0b0dcb5067f052a63");
        Aws::EC2::Model::InstanceType EC2_LAUNCH_TEMPLATE_INSTANCE_TYPE = Aws::EC2::Model::InstanceType::t1_micro;
        static const int ASTERISK_FILL_WIDTH = 88;
        static const int WAIT_FOR_INSTANCES_TIMEOUT = 300; // Time in seconds.

        //! Routine which waits for EC2 instances in an Auto Scaling group to
        //! complete startup or shutdown.
        /*!
         \sa waitForInstances()
         \param groupName: An Auto Scaling group name.
         \param autoScalingGroups: Vector to receive 'AutoScalingGroup' records.
         \param client: 'AutoScalingClient' instance.
         \return bool: Successful completion.
         */
        bool waitForInstances(const Aws::String &groupName,
                              Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups,
                              const Aws::AutoScaling::AutoScalingClient &client);

        //! Routine to cleanup resources created in 'groupsAndInstancesScenario'.
        /*!
         \sa cleanupResources()
         \param groupName: Optional Auto Scaling group name.
         \param templateName: Optional EC2 launch template name.
         \param autoScalingClient: 'AutoScalingClient' instance.
         \param ec2Client: 'EC2Client' instance.
         \return bool: Successful completion.
         */
        bool
        cleanupResources(const Aws::String &groupName,
                         const Aws::String &templateName,
                         const Aws::AutoScaling::AutoScalingClient &autoScalingClient,
                         const Aws::EC2::EC2Client &ec2Client);

        //! Routine which retrieves Auto Scaling group descriptions.
        /*!
         \sa describeGroup()
         \param groupName: An Auto Scaling group name.
         \param autoScalingGroups: Vector to receive 'AutoScalingGroup' records.
         \param client: 'AutoScalingClient' instance.
         \return bool: Successful completion.
         */
        bool describeGroup(const Aws::String &groupName,
                           Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroup,
                           const Aws::AutoScaling::AutoScalingClient &client);

        //! Routine which logs the life cycle state for a vector of
        //! 'AutoScalingInstanceDetails'.
        /*!
         \sa logInstancesLifecycleState()
         \param autoScalingGroups: Vector of 'AutoScalingInstanceDetails' records.
         */
        void logInstancesLifecycleState(
                const Aws::Vector<Aws::AutoScaling::Model::AutoScalingInstanceDetails> &
                instancesDetails);

        //! Routine which logs the Auto Scaling group info.
        /*!
         \sa logAutoScalingGroupInfo()
         \param autoScalingGroups: Vector of 'AutoScalingGroup' records.
         */
        void logAutoScalingGroupInfo(
                const Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups);

        //! Routine which retrieves the Amazon CloudWatch metrics for an Auto Scaling group
        //! and logs the results.
        /*!
         \sa logAutoScalingMetrics()
         \param autoScalingGroups: Vector of 'AutoScalingGroup' records.
         \return bool: Successful completion.
         */
        bool logAutoScalingMetrics(const Aws::String &groupName,
                                   const Aws::Client::ClientConfiguration &clientConfig);

        //! Convenience routine which returns a vector of instance ID strings for a vector of
        //! 'Instance' records.
        /*!
         \sa instancesToInstanceIDs()
         \param instances: Vector of 'Instance' records.
         \return Aws::Vector<Aws::String>: Vector of instance IDs.
         */
        Aws::Vector<Aws::String> instancesToInstanceIDs(
                const Aws::Vector<Aws::AutoScaling::Model::Instance> &instances);

        //! Convenience routine which searches a vector of strings.
        /*!
         \sa stringInVector()
         \param string: Search string.
         \param aVector: Vector of strings.
         \return bool: String found.
         */
        bool stringInVector(const Aws::String &string,
                            const std::vector<Aws::String> &aVector);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa testForEmptyString()
         \param string: A string to test.
         \return bool: True if empty.
         */
        bool testForEmptyString(const Aws::String &string);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa alwaysTrueTest()
         \return bool: Always true.
         */
        bool alwaysTrueTest(const Aws::String &) { return true; }

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        Aws::String askQuestion(const Aws::String &string,
                                const std::function<bool(
                                        Aws::String)> &test = testForEmptyString);

        //! Command line prompt/response for yes/no question.
        /*!
         \\sa askYesNoQuestion()
         \param string: A question prompt expecting a 'y' or 'n' response.
         \return bool: True if yes.
         */
        bool askYesNoQuestion(const Aws::String &string);

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \sa askQuestionForIntRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        int askQuestionForIntRange(const Aws::String &string, int low,
                                   int high);
    } // AutoScaling
} // AwsDoc

// snippet-start:[cpp.example_code.autoscaling.groups_and_instances_scenario]
//! Routine which demonstrates using an Auto Scaling group
//! to manage Amazon EC2 instances.
/*!
  \sa groupsAndInstancesScenario()
  \param clientConfig: AWS client configuration.
  \return bool: Successful completion.
 */
bool AwsDoc::AutoScaling::groupsAndInstancesScenario(
        const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::String templateName;
    Aws::EC2::EC2Client ec2Client(clientConfig);

    std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " "
              << std::endl;
    std::cout
            << "Welcome to the Amazon Elastic Compute Cloud (Amazon EC2) Auto Scaling "
            << "demo for managing groups and instances." << std::endl;
    std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " \n"
              << std::endl;

    std::cout << "This example requires an EC2 launch template." << std::endl;
    if (askYesNoQuestion(
            "Would you like to use an existing EC2 launch template (y/n)?  ")) {

        // 1. Specify the name of an existing EC2 launch template.
        templateName = askQuestion(
                "Enter the name of the existing EC2 launch template.  ");

        Aws::EC2::Model::DescribeLaunchTemplatesRequest request;
        request.AddLaunchTemplateNames(templateName);
        Aws::EC2::Model::DescribeLaunchTemplatesOutcome outcome =
                ec2Client.DescribeLaunchTemplates(request);

        if (outcome.IsSuccess()) {
            std::cout << "Validated the EC2 launch template '" << templateName
                      << "' exists by calling DescribeLaunchTemplate." << std::endl;
        }
        else {
            std::cerr << "Error validating the existence of the launch template. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }
    else { // 2.  Or create a new EC2 launch template.
        templateName = askQuestion("Enter the name for a new EC2 launch template: ");

        Aws::EC2::Model::CreateLaunchTemplateRequest request;
        request.SetLaunchTemplateName(templateName);

        Aws::EC2::Model::RequestLaunchTemplateData requestLaunchTemplateData;
        requestLaunchTemplateData.SetInstanceType(EC2_LAUNCH_TEMPLATE_INSTANCE_TYPE);
        requestLaunchTemplateData.SetImageId(EC2_LAUNCH_TEMPLATE_IMAGE_ID);

        request.SetLaunchTemplateData(requestLaunchTemplateData);

        Aws::EC2::Model::CreateLaunchTemplateOutcome outcome =
                ec2Client.CreateLaunchTemplate(request);

        if (outcome.IsSuccess()) {
            std::cout << "The EC2 launch template '" << templateName << " was created."
                      << std::endl;
        }
        else if (outcome.GetError().GetExceptionName() ==
                 "InvalidLaunchTemplateName.AlreadyExistsException") {
            std::cout << "The EC2 template '" << templateName << "' already exists"
                      << std::endl;
        }
        else {
            std::cerr << "Error with EC2::CreateLaunchTemplate. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }
// snippet-start:[cpp.example_code.autoscaling.autoscaling_client]
    Aws::AutoScaling::AutoScalingClient autoScalingClient(clientConfig);
// snippet-end:[cpp.example_code.autoscaling.autoscaling_client]
    std::cout << "Let's create an Auto Scaling group." << std::endl;
    Aws::String groupName = askQuestion(
            "Enter a name for the Auto Scaling group:  ");
    // 3. Retrieve a list of EC2 Availability Zones.
    Aws::Vector<Aws::EC2::Model::AvailabilityZone> availabilityZones;
    {
        Aws::EC2::Model::DescribeAvailabilityZonesRequest request;

        Aws::EC2::Model::DescribeAvailabilityZonesOutcome outcome =
                ec2Client.DescribeAvailabilityZones(request);

        if (outcome.IsSuccess()) {
            std::cout
                    << "EC2 instances can be created in the following Availability Zones:"
                    << std::endl;

            availabilityZones = outcome.GetResult().GetAvailabilityZones();
            for (size_t i = 0; i < availabilityZones.size(); ++i) {
                std::cout << "   " << i + 1 << ".  "
                          << availabilityZones[i].GetZoneName() << std::endl;
            }
        }
        else {
            std::cerr << "Error with EC2::DescribeAvailabilityZones. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            cleanupResources("", templateName, autoScalingClient, ec2Client);
            return false;
        }
    }

    int availabilityZoneChoice = askQuestionForIntRange(
            "Choose an Availability Zone:  ", 1,
            static_cast<int>(availabilityZones.size()));
    // 4. Create an Auto Scaling group with the specified Availability Zone.
    {
        // snippet-start:[cpp.example_code.autoscaling.create_autoscaling_group1]
        Aws::AutoScaling::Model::CreateAutoScalingGroupRequest request;
        request.SetAutoScalingGroupName(groupName);
        Aws::Vector<Aws::String> availabilityGroupZones;
        availabilityGroupZones.push_back(
                availabilityZones[availabilityZoneChoice - 1].GetZoneName());
        request.SetAvailabilityZones(availabilityGroupZones);
        request.SetMaxSize(1);
        request.SetMinSize(1);

        Aws::AutoScaling::Model::LaunchTemplateSpecification launchTemplateSpecification;
        launchTemplateSpecification.SetLaunchTemplateName(templateName);
        request.SetLaunchTemplate(launchTemplateSpecification);

        Aws::AutoScaling::Model::CreateAutoScalingGroupOutcome outcome =
                autoScalingClient.CreateAutoScalingGroup(request);

        if (outcome.IsSuccess()) {
            std::cout << "Created Auto Scaling group '" << groupName << "'..."
                      << std::endl;
        }
        else if (outcome.GetError().GetErrorType() ==
                 Aws::AutoScaling::AutoScalingErrors::ALREADY_EXISTS_FAULT) {
            std::cout << "Auto Scaling group '" << groupName << "' already exists."
                      << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::CreateAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            // snippet-end:[cpp.example_code.autoscaling.create_autoscaling_group1]
            cleanupResources("", templateName, autoScalingClient, ec2Client);
            return false;
            // snippet-start:[cpp.example_code.autoscaling.create_autoscaling_group2]
        }
        // snippet-end:[cpp.example_code.autoscaling.create_autoscaling_group2]
    }

    Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> autoScalingGroups;
    if (AwsDoc::AutoScaling::describeGroup(groupName, autoScalingGroups,
                                           autoScalingClient)) {
        std::cout << "Here is the Auto Scaling group description." << std::endl;
        if (!autoScalingGroups.empty()) {
            logAutoScalingGroupInfo(autoScalingGroups);
        }
    }
    else {
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    std::cout
            << "Waiting for the EC2 instance in the Auto Scaling group to become active..."
            << std::endl;
    if (!waitForInstances(groupName, autoScalingGroups, autoScalingClient)) {
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    bool enableMetrics = askYesNoQuestion(
            "Do you want to collect metrics about the A"
            "Auto Scaling group during this demo (y/n)?  ");
    // 7. Optionally enable metrics collection for the Auto Scaling group.
    if (enableMetrics) {
        // snippet-start:[cpp.example_code.autoscaling.enable_metrics_collection1]
        Aws::AutoScaling::Model::EnableMetricsCollectionRequest request;
        request.SetAutoScalingGroupName(groupName);

        request.AddMetrics("GroupMinSize");
        request.AddMetrics("GroupMaxSize");
        request.AddMetrics("GroupDesiredCapacity");
        request.AddMetrics("GroupInServiceInstances");
        request.AddMetrics("GroupTotalInstances");
        request.SetGranularity("1Minute");

        Aws::AutoScaling::Model::EnableMetricsCollectionOutcome outcome =
                autoScalingClient.EnableMetricsCollection(request);
        if (outcome.IsSuccess()) {
            std::cout << "Auto Scaling metrics have been enabled."
                      << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::EnableMetricsCollection. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            // snippet-end:[cpp.example_code.autoscaling.enable_metrics_collection1]
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
            // snippet-start:[cpp.example_code.autoscaling.enable_metrics_collection2]
        }
        // snippet-end:[cpp.example_code.autoscaling.enable_metrics_collection2]
    }

    std::cout << "Let's update the maximum number of EC2 instances in '" << groupName <<
              "' from 1 to 3." << std::endl;
    askQuestion("Press enter to continue:  ", alwaysTrueTest);
    // 8. Update the Auto Scaling group, setting a new maximum size.
    {
        // snippet-start:[cpp.example_code.autoscaling.update_autoscaling_group1]
        Aws::AutoScaling::Model::UpdateAutoScalingGroupRequest request;
        request.SetAutoScalingGroupName(groupName);
        request.SetMaxSize(3);

        Aws::AutoScaling::Model::UpdateAutoScalingGroupOutcome outcome =
                autoScalingClient.UpdateAutoScalingGroup(request);

        if (!outcome.IsSuccess()) {
            std::cerr << "Error with AutoScaling::UpdateAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            // snippet-end:[cpp.example_code.autoscaling.update_autoscaling_group1]
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
            // snippet-start:[cpp.example_code.autoscaling.update_autoscaling_group2]
        }
        // snippet-end:[cpp.example_code.autoscaling.update_autoscaling_group2]
    }

    if (AwsDoc::AutoScaling::describeGroup(groupName, autoScalingGroups,
                                           autoScalingClient)) {
        if (!autoScalingGroups.empty()) {
            const auto &instances = autoScalingGroups[0].GetInstances();
            std::cout
                    << "The group still has one running EC2 instance, but it can have up to 3.\n"
                    << std::endl;
            logAutoScalingGroupInfo(autoScalingGroups);
        }
        else {
            std::cerr
                    << "No EC2 launch groups were retrieved from DescribeGroup request."
                    << std::endl;
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
        }
    }

    std::cout << "\n" << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << "\n"
              << std::endl;
    std::cout << "Let's update the desired capacity in '" << groupName <<
              "' from 1 to 2." << std::endl;
    askQuestion("Press enter to continue:  ", alwaysTrueTest);
    //  9. Update the Auto Scaling group, setting a new desired capacity.
    {
        // snippet-start:[cpp.example_code.autoscaling.set_desired_capacity1]
        Aws::AutoScaling::Model::SetDesiredCapacityRequest request;
        request.SetAutoScalingGroupName(groupName);
        request.SetDesiredCapacity(2);

        Aws::AutoScaling::Model::SetDesiredCapacityOutcome outcome =
                autoScalingClient.SetDesiredCapacity(request);

        if (!outcome.IsSuccess()) {
            std::cerr << "Error with AutoScaling::SetDesiredCapacityRequest. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            // snippet-end:[cpp.example_code.autoscaling.set_desired_capacity1]
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
            // snippet-start:[cpp.example_code.autoscaling.set_desired_capacity2]
        }
        // snippet-end:[cpp.example_code.autoscaling.set_desired_capacity2]
    }

    if (AwsDoc::AutoScaling::describeGroup(groupName, autoScalingGroups,
                                           autoScalingClient)) {
        if (!autoScalingGroups.empty()) {
            std::cout
                    << "Here is the current state of the group." << std::endl;
            logAutoScalingGroupInfo(autoScalingGroups);
        }
        else {
            std::cerr
                    << "No EC2 launch groups were retrieved from DescribeGroup request."
                    << std::endl;
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
        }
    }

    std::cout << "Waiting for the new EC2 instance to start..." << std::endl;
    waitForInstances(groupName, autoScalingGroups, autoScalingClient);

    std::cout << "\n" << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << "\n"
              << std::endl;

    std::cout << "Let's terminate one of the EC2 instances in " << groupName << "."
              << std::endl;
    std::cout << "Because the desired capacity is 2, another EC2 instance will start "
              << "to replace the terminated EC2 instance."
              << std::endl;
    std::cout << "The currently running EC2 instances are:" << std::endl;

    if (autoScalingGroups.empty()) {
        std::cerr << "Error describing groups. No groups returned." << std::endl;
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    int instanceNumber = 1;
    Aws::Vector<Aws::String> instanceIDs = instancesToInstanceIDs(
            autoScalingGroups[0].GetInstances());
    for (const Aws::String &instanceID: instanceIDs) {
        std::cout << "   " << instanceNumber << ". " << instanceID << std::endl;
        ++instanceNumber;
    }

    instanceNumber = askQuestionForIntRange("Which EC2 instance do you want to stop? ",
                                            1,
                                            static_cast<int>(instanceIDs.size()));

    // 10. Terminate an EC2 instance in the Auto Scaling group.
    {
        // snippet-start:[cpp.example_code.autoscaling.terminate_instance_autoscaling_group1]
        Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupRequest request;
        request.SetInstanceId(instanceIDs[instanceNumber - 1]);
        request.SetShouldDecrementDesiredCapacity(false);

        Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupOutcome outcome =
                autoScalingClient.TerminateInstanceInAutoScalingGroup(request);

        if (outcome.IsSuccess()) {
            std::cout << "Waiting for EC2 instance with ID '"
                      << instanceIDs[instanceNumber - 1] << "' to terminate..."
                      << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::TerminateInstanceInAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            // snippet-end:[cpp.example_code.autoscaling.terminate_instance_autoscaling_group1]
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
            // snippet-start:[cpp.example_code.autoscaling.terminate_instance_autoscaling_group2]
        }
        // snippet-end:[cpp.example_code.autoscaling.terminate_instance_autoscaling_group2]
    }

    waitForInstances(groupName, autoScalingGroups, autoScalingClient);

    std::cout << "\n" << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << "\n"
              << std::endl;
    std::cout << "Let's get a report of scaling activities for EC2 launch group '"
              << groupName << "'."
              << std::endl;
    askQuestion("Press enter to continue:  ", alwaysTrueTest);
    // 11. Get a description of activities for the Auto Scaling group.
    {
        // snippet-start:[cpp.example_code.autoscaling.describe_scaling_activities1]
        Aws::AutoScaling::Model::DescribeScalingActivitiesRequest request;
        request.SetAutoScalingGroupName(groupName);

        Aws::AutoScaling::Model::DescribeScalingActivitiesOutcome outcome =
                autoScalingClient.DescribeScalingActivities(request);

        if (outcome.IsSuccess()) {
            const Aws::Vector<Aws::AutoScaling::Model::Activity> &activities =
                    outcome.GetResult().GetActivities();
            std::cout << "Found " << activities.size() << " activities." << std::endl;
            std::cout << "Activities are ordered with the most recent first."
                      << std::endl;
            for (const Aws::AutoScaling::Model::Activity &activity: activities) {
                std::cout << activity.GetDescription() << std::endl;
                std::cout << activity.GetDetails() << std::endl;
            }
        }
        else {
            std::cerr << "Error with AutoScaling::DescribeScalingActivities. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            // snippet-end:[cpp.example_code.autoscaling.describe_scaling_activities1]
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
            // snippet-start:[cpp.example_code.autoscaling.describe_scaling_activities2]
        }
        // snippet-end:[cpp.example_code.autoscaling.describe_scaling_activities2]
    }

    if (enableMetrics) {
        if (!logAutoScalingMetrics(groupName, clientConfig)) {
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
        }
    }

    std::cout << "Let's  clean up." << std::endl;
    askQuestion("Press enter to continue:  ", alwaysTrueTest);

    // 13. Disable metrics collection if enabled.
    if (enableMetrics) {
        // snippet-start:[cpp.example_code.autoscaling.disable_metrics_collection1]
        Aws::AutoScaling::Model::DisableMetricsCollectionRequest request;
        request.SetAutoScalingGroupName(groupName);

        Aws::AutoScaling::Model::DisableMetricsCollectionOutcome outcome =
                autoScalingClient.DisableMetricsCollection(request);

        if (outcome.IsSuccess()) {
            std::cout << "Metrics collection has been disabled." << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::DisableMetricsCollection. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            // snippet-end:[cpp.example_code.autoscaling.disable_metrics_collection1]
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
            // snippet-start:[cpp.example_code.autoscaling.disable_metrics_collection2]
        }
        // snippet-end:[cpp.example_code.autoscaling.disable_metrics_collection2]
    }

    return cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
}

//! Routine which waits for EC2 instances in an Auto Scaling group to
//! complete startup or shutdown.
/*!
 \sa waitForInstances()
 \param groupName: An Auto Scaling group name.
 \param autoScalingGroups: Vector to receive 'AutoScalingGroup' records.
 \param client: 'AutoScalingClient' instance.
 \return bool: Successful completion.
 */
bool AwsDoc::AutoScaling::waitForInstances(const Aws::String &groupName,
                                           Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups,
                                           const Aws::AutoScaling::AutoScalingClient &client) {
    bool ready = false;
    const std::vector<Aws::String> READY_STATES = {"InService", "Terminated"};

    int count = 0;
    int desiredCapacity = 0;
    std::this_thread::sleep_for(std::chrono::seconds(4));
    while (!ready) {
        if (WAIT_FOR_INSTANCES_TIMEOUT < count) {
            std::cerr << "Wait for instance timed out." << std::endl;
            return false;
        }

        std::this_thread::sleep_for(std::chrono::seconds(1));
        ++count;
        if (!describeGroup(groupName, autoScalingGroups, client)) {
            return false;
        }
        Aws::Vector<Aws::String> instanceIDs;
        if (!autoScalingGroups.empty()) {
            instanceIDs = instancesToInstanceIDs(autoScalingGroups[0].GetInstances());
            desiredCapacity = autoScalingGroups[0].GetDesiredCapacity();
        }

        if (instanceIDs.empty()) {
            if (desiredCapacity == 0) {
                break;
            }
            else {
                if ((count % 5) == 0) {
                    std::cout << "No instance IDs returned for group." << std::endl;
                }

                continue;
            }
        }

        // 6.  Check lifecycle state of the instances using DescribeAutoScalingInstances.
        // snippet-start:[cpp.example_code.autoscaling.describe_autoscaling_instances1]
        Aws::AutoScaling::Model::DescribeAutoScalingInstancesRequest request;
        request.SetInstanceIds(instanceIDs);

        Aws::AutoScaling::Model::DescribeAutoScalingInstancesOutcome outcome =
                client.DescribeAutoScalingInstances(request);

        if (outcome.IsSuccess()) {
            const Aws::Vector<Aws::AutoScaling::Model::AutoScalingInstanceDetails> &instancesDetails =
                    outcome.GetResult().GetAutoScalingInstances();
            // snippet-end:[cpp.example_code.autoscaling.describe_autoscaling_instances1]
            ready = instancesDetails.size() >= desiredCapacity;
            for (const Aws::AutoScaling::Model::AutoScalingInstanceDetails &details: instancesDetails) {
                if (!stringInVector(details.GetLifecycleState(), READY_STATES)) {
                    ready = false;
                    break;
                }
            }
            // Log the status while waiting.
            if (((count % 5) == 1) || ready) {
                logInstancesLifecycleState(instancesDetails);
            }
            // snippet-start:[cpp.example_code.autoscaling.describe_autoscaling_instances2]
        }
        else {
            std::cerr << "Error with AutoScaling::DescribeAutoScalingInstances. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        // snippet-end:[cpp.example_code.autoscaling.describe_autoscaling_instances2]
    }

    if (!describeGroup(groupName, autoScalingGroups, client)) {
        return false;
    }

    return true;
}

//! Routine to cleanup resources created in 'groupsAndInstancesScenario'.
/*!
 \sa cleanupResources()
 \param groupName: Optional Auto Scaling group name.
 \param templateName: Optional EC2 launch template name.
 \param autoScalingClient: 'AutoScalingClient' instance.
 \param ec2Client: 'EC2Client' instance.
\return bool: Successful completion.
 */
bool AwsDoc::AutoScaling::cleanupResources(const Aws::String &groupName,
                                           const Aws::String &templateName,
                                           const Aws::AutoScaling::AutoScalingClient &autoScalingClient,
                                           const Aws::EC2::EC2Client &ec2Client) {
    bool result = true;

    // 14. Delete the Auto Scaling group.
    if (!groupName.empty() &&
        (askYesNoQuestion(
                Aws::String("Delete the Auto Scaling group '") + groupName +
                "'  (y/n)?"))) {
        {
            Aws::AutoScaling::Model::UpdateAutoScalingGroupRequest request;
            request.SetAutoScalingGroupName(groupName);
            request.SetMinSize(0);
            request.SetDesiredCapacity(0);

            Aws::AutoScaling::Model::UpdateAutoScalingGroupOutcome outcome =
                    autoScalingClient.UpdateAutoScalingGroup(request);

            if (outcome.IsSuccess()) {
                std::cout
                        << "The minimum size and desired capacity of the Auto Scaling group "
                        << "was set to zero before terminating the instances."
                        << std::endl;
            }
            else {
                std::cerr << "Error with AutoScaling::UpdateAutoScalingGroup. "
                          << outcome.GetError().GetMessage() << std::endl;
                result = false;
            }
        }

        Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> autoScalingGroups;
        if (AwsDoc::AutoScaling::describeGroup(groupName, autoScalingGroups,
                                               autoScalingClient)) {
            if (!autoScalingGroups.empty()) {
                Aws::Vector<Aws::String> instanceIDs = instancesToInstanceIDs(
                        autoScalingGroups[0].GetInstances());
                for (const Aws::String &instanceID: instanceIDs) {
                    Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupRequest request;
                    request.SetInstanceId(instanceID);
                    request.SetShouldDecrementDesiredCapacity(true);

                    Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupOutcome outcome =
                            autoScalingClient.TerminateInstanceInAutoScalingGroup(
                                    request);

                    if (outcome.IsSuccess()) {
                        std::cout << "Initiating termination of EC2 instance '"
                                  << instanceID << "'." << std::endl;
                    }
                    else {
                        std::cerr
                                << "Error with AutoScaling::TerminateInstanceInAutoScalingGroup. "
                                << outcome.GetError().GetMessage() << std::endl;
                        result = false;
                    }
                }
            }

            std::cout
                    << "Waiting for the EC2 instances to terminate before deleting the "
                    << "Auto Scaling group..." << std::endl;
            waitForInstances(groupName, autoScalingGroups, autoScalingClient);
        }

        {
            // snippet-start:[cpp.example_code.autoscaling.delete_autoscaling_group]
            Aws::AutoScaling::Model::DeleteAutoScalingGroupRequest request;
            request.SetAutoScalingGroupName(groupName);

            Aws::AutoScaling::Model::DeleteAutoScalingGroupOutcome outcome =
                    autoScalingClient.DeleteAutoScalingGroup(request);

            if (outcome.IsSuccess()) {
                std::cout << "Auto Scaling group '" << groupName << "' was deleted."
                          << std::endl;
            }
            else {
                std::cerr << "Error with AutoScaling::DeleteAutoScalingGroup. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                result = false;
            }
        }
        // snippet-end:[cpp.example_code.autoscaling.delete_autoscaling_group]
    }

    // 15. Delete the EC2 launch template.
    if (!templateName.empty() && (askYesNoQuestion(
            Aws::String("Delete the EC2 launch template '") + templateName +
            "' (y/n)?"))) {
        Aws::EC2::Model::DeleteLaunchTemplateRequest request;
        request.SetLaunchTemplateName(templateName);

        Aws::EC2::Model::DeleteLaunchTemplateOutcome outcome =
                ec2Client.DeleteLaunchTemplate(request);

        if (outcome.IsSuccess()) {
            std::cout << "EC2 launch template '" << templateName << "' was deleted."
                      << std::endl;
        }
        else {
            std::cerr << "Error with EC2::DeleteLaunchTemplate. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            result = false;
        }
    }

    return result;
}

//! Routine which retrieves Auto Scaling group descriptions.
/*!
 \sa describeGroup()
 \param groupName: An Auto Scaling group name.
 \param autoScalingGroups: Vector to receive 'AutoScalingGroup' records.
 \param client: 'AutoScalingClient' instance.
 \return bool: Successful completion.
 */
bool AwsDoc::AutoScaling::describeGroup(const Aws::String &groupName,
                                        Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroup,
                                        const Aws::AutoScaling::AutoScalingClient &client) {
    // 5. Retrieve a description of the Auto Scaling group.
// snippet-start:[cpp.example_code.autoscaling.describe_autoscaling_group]
    Aws::AutoScaling::Model::DescribeAutoScalingGroupsRequest request;
    Aws::Vector<Aws::String> groupNames;
    groupNames.push_back(groupName);
    request.SetAutoScalingGroupNames(groupNames);

    Aws::AutoScaling::Model::DescribeAutoScalingGroupsOutcome outcome =
            client.DescribeAutoScalingGroups(request);

    if (outcome.IsSuccess()) {
        autoScalingGroup = outcome.GetResult().GetAutoScalingGroups();
    }
    else {
        std::cerr << "Error with AutoScaling::DescribeAutoScalingGroups. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }
// snippet-end:[cpp.example_code.autoscaling.describe_autoscaling_group]

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.autoscaling.groups_and_instances_scenario]

/*
 *  main function
 *
 *  Usage: 'run_groups_and_instances_scenario'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
    (void) argc; // Suppress lint warning.
    (void) argv; // Suppress lint warning.

    Aws::SDKOptions options;
    InitAPI(options);

    {
        // snippet-start:[cpp.example_code.autoscaling.client_configuration]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        // snippet-end:[cpp.example_code.autoscaling.client_configuration]

        AwsDoc::AutoScaling::groupsAndInstancesScenario(clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

//! Routine which logs the life cycle state for a vector of
//! 'AutoScalingInstanceDetails'.
/*!
 \sa logInstancesLifecycleState()
 \param autoScalingGroups: Vector of 'AutoScalingInstanceDetails' records.
 */
void AwsDoc::AutoScaling::logInstancesLifecycleState(
        const Aws::Vector<Aws::AutoScaling::Model::AutoScalingInstanceDetails> &instancesDetails) {

    std::cout << "Retrieved the lifecycle state for " << instancesDetails.size()
              << (instancesDetails.size() == 1 ? " instance." : " instances.")
              << std::endl;


    for (size_t i = 0; i < instancesDetails.size(); ++i) {
        if (i == 0) {
            std::cout << "   (";
        }
        std::cout << instancesDetails[i].GetLifecycleState();

        if (i == instancesDetails.size() - 1) {
            std::cout << ")" << std::endl;
        }
        else {
            std::cout << ", ";
        }
    }
}

//! Routine which logs the Auto Scaling group info.
/*!
 \sa logAutoScalingGroupInfo()
 \param autoScalingGroups: Vector of 'AutoScalingGroup' records.
 */
void AwsDoc::AutoScaling::logAutoScalingGroupInfo(
        const Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups) {
    if (!autoScalingGroups.empty()) {
        const Aws::AutoScaling::Model::AutoScalingGroup &group = autoScalingGroups[0];
        std::cout << group.GetAutoScalingGroupName() << std::endl;
        std::cout << "   Launch template: "
                  << group.GetLaunchTemplate().GetLaunchTemplateName() << std::endl;
        std::cout << "   Min: " << group.GetMinSize() << ", Max: " << group.GetMaxSize()
                  <<
                  ", Desired: " << group.GetDesiredCapacity() << std::endl;
        const Aws::Vector<Aws::AutoScaling::Model::Instance> &instances = group.GetInstances();
        if (!instances.empty()) {
            std::cout << "   Instances:" << std::endl;
            for (const Aws::AutoScaling::Model::Instance &instance: instances) {
                std::cout << "      " << instance.GetInstanceId() << ": " <<
                          Aws::AutoScaling::Model::LifecycleStateMapper::GetNameForLifecycleState(
                                  instance.GetLifecycleState()) << std::endl;
            }
        }
    }
    else {
        std::cerr << "Error printing group. Group list is empty." << std::endl;
    }
}

//! Routine which retrieves the Amazon CloudWatch metrics for an Auto Scaling group
//! and logs the results.
/*!
 \sa logAutoScalingMetrics()
 \param autoScalingGroups: Vector of 'AutoScalingGroup' records.
 \return bool: Successful completion.
 */
bool AwsDoc::AutoScaling::logAutoScalingMetrics(const Aws::String &groupName,
                                                const Aws::Client::ClientConfiguration &clientConfig) {
    std::cout << "Let's look at CloudWatch metrics." << std::endl;

    //  12. Optionally list the metrics for the Auto Scaling group.
    Aws::CloudWatch::CloudWatchClient cloudWatchClient(clientConfig);
    Aws::Vector<Aws::CloudWatch::Model::Metric> allMetrics;
    {
        Aws::CloudWatch::Model::ListMetricsRequest request;
        request.SetNamespace("AWS/AutoScaling");

        Aws::CloudWatch::Model::DimensionFilter dimensionFilter;
        dimensionFilter.SetName("AutoScalingGroupName");
        dimensionFilter.SetValue(groupName);
        request.SetDimensions({dimensionFilter});
        Aws::String nextToken;

        do {
            Aws::CloudWatch::Model::ListMetricsOutcome outcome = cloudWatchClient.ListMetrics(
                    request);

            if (outcome.IsSuccess()) {
                const Aws::Vector<Aws::CloudWatch::Model::Metric> &metrics = outcome.GetResult().GetMetrics();

                allMetrics.insert(allMetrics.end(), metrics.begin(), metrics.end());

                nextToken = outcome.GetResult().GetNextToken();
            }
            else {
                std::cerr << "Error with CloudWatch::ListMetrics. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                return false;
            }
        } while (!nextToken.empty());

    }

    if (allMetrics.empty()) {
        std::cerr << "No metrics were retrieved." << std::endl;
        return false;
    }

    std::cout << "The following metrics are enabled for '" << groupName << "'."
              << std::endl;
    for (size_t i = 0; i < allMetrics.size(); ++i) {
        std::cout << "   " << i + 1 << ". " << allMetrics[i].GetMetricName()
                  << std::endl;
    }

    do {
        int choice = askQuestionForIntRange("Which metric would you like to view?  ", 1,
                                            static_cast<int>(allMetrics.size()));
        const Aws::CloudWatch::Model::Metric &metric = allMetrics[choice - 1];
        Aws::CloudWatch::Model::GetMetricStatisticsRequest request;
        request.SetNamespace(metric.GetNamespace());
        request.SetMetricName(metric.GetMetricName());
        request.SetDimensions(metric.GetDimensions());

        // Set start time to one week ago (now - 7 * 24 hours).
        request.SetStartTime(
                Aws::Utils::DateTime::Now() - std::chrono::hours(7 * 24));
        request.SetEndTime(Aws::Utils::DateTime::Now());
        request.SetPeriod(60 * 60);
        request.AddStatistics(Aws::CloudWatch::Model::Statistic::Average);
        request.AddStatistics(Aws::CloudWatch::Model::Statistic::Minimum);
        request.AddStatistics(Aws::CloudWatch::Model::Statistic::Maximum);

        Aws::CloudWatch::Model::GetMetricStatisticsOutcome outcome = cloudWatchClient.GetMetricStatistics(
                request);

        if (outcome.IsSuccess()) {
            const Aws::Vector<Aws::CloudWatch::Model::Datapoint> &datapoints = outcome.GetResult().GetDatapoints();

            std::cout << "Statistics for " << outcome.GetResult().GetLabel() << "."
                      << std::endl;
            for (const Aws::CloudWatch::Model::Datapoint &datapoint: datapoints) {
                std::cout << "   " << datapoint.GetTimestamp().ToGmtString("%FT%TZ")
                          << ": max "
                          << datapoint.GetMaximum() << ", min "
                          << datapoint.GetMinimum() << ", avg "
                          << datapoint.GetAverage() << ", units " <<
                          Aws::CloudWatch::Model::StandardUnitMapper::GetNameForStandardUnit(
                                  datapoint.GetUnit())
                          << "." << std::endl;
            }
        }
        else {
            std::cerr << "Error with CloudWatch::GetMetricStatistics. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    } while (askYesNoQuestion("Would you like to view another metric (y/n)?  "));

    return true;
}

//! Convenience routine which returns a vector of instance ID strings for a vector of
//! 'Instance' records.
/*!
 \sa instancesToInstanceIDs()
 \param instances: Vector of 'Instance' records.
 \return Aws::Vector<Aws::String>: Vector of instance IDs.
 */
Aws::Vector<Aws::String> AwsDoc::AutoScaling::instancesToInstanceIDs(
        const Aws::Vector<Aws::AutoScaling::Model::Instance> &instances) {
    Aws::Vector<Aws::String> instanceIDs;
    for (const Aws::AutoScaling::Model::Instance &instance: instances) {
        instanceIDs.push_back(instance.GetInstanceId());
    }

    return instanceIDs;
}

//! Convenience routine which searches a vector of strings.
/*!
 \sa stringInVector()
 \param string: Search string.
 \param aVector: Vector of strings.
 \return bool: String found.
 */
bool AwsDoc::AutoScaling::stringInVector(const Aws::String &string,
                                         const std::vector<Aws::String> &aVector) {
    return std::find(aVector.begin(), aVector.end(), string) != aVector.end();
}

//! Test routine passed as argument to askQuestion routine.
/*!
 \sa testForEmptyString()
 \param string: A string to test.
 \return bool: True if empty.
 */
bool AwsDoc::AutoScaling::testForEmptyString(const Aws::String &string) {
    if (string.empty()) {
        std::cout << "Please enter some text." << std::endl;
        return false;
    }

    return true;
}

//! Command line prompt/response utility function.
/*!
 \\sa askQuestion()
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::AutoScaling::askQuestion(const Aws::String &string,
                                             const std::function<bool(
                                                     Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
    } while (!test(result));

    return result;
}

//! Command line prompt/response for yes/no question.
/*!
 \\sa askYesNoQuestion()
 \param string: A question prompt expecting a 'y' or 'n' response.
 \return bool: True if yes.
 */
bool AwsDoc::AutoScaling::askYesNoQuestion(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](
            const Aws::String &string1) -> bool {
            bool result = false;
            if (string1.length() == 1) {
                int answer = std::tolower(string1[0]);
                result = (answer == 'y') || (answer == 'n');
            }

            if (!result) {
                std::cout << "Please answer 'y' or 'n'." << std::endl;
            }

            return result;
    });

    return std::tolower(resultString[0]) == 'y';
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \sa askQuestionForIntRange()
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int AwsDoc::AutoScaling::askQuestionForIntRange(const Aws::String &string, int low,
                                                int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cout << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cout << "\nNot a valid number." << std::endl;
                return false;
            }
    });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}
