/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to create an S3 bucket and upload objects to S3 buckets.
 *
 * 1. Create an EC2 launch template.
 * 2. Upload a local file to the bucket.
 * 3. Download the object to a local file.
 * 4. Copy the object to a different "folder" in the bucket.
 * 5. List objects in the bucket.
 * 6. Delete all objects in the bucket.
 * 7. Delete the bucket.
 *
 */

// snippet-start:[cpp.example_code.autoscaling.groups_and_instances_scenario]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/autoscaling/AutoScalingClient.h>
#include <aws/autoscaling/model/CreateAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/DeleteAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/DescribeAutoScalingGroupsRequest.h>
#include <aws/autoscaling/model/DescribeAutoScalingInstancesRequest.h>
#include <aws/autoscaling/model/EnableMetricsCollectionRequest.h>
#include <aws/autoscaling/model/TerminateInstanceInAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/UpdateAutoScalingGroupRequest.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateLaunchTemplateRequest.h>
#include <aws/ec2/model/DeleteLaunchTemplateRequest.h>
#include <aws/ec2/model/DescribeAvailabilityZonesRequest.h>

namespace AwsDoc {
    namespace AutoScaling {
        bool groupsAndInstancesScenario(
                const Aws::Client::ClientConfiguration &clientConfig);

        bool waitForInstances(const Aws::String &groupName,
                              Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups,
                              const Aws::AutoScaling::AutoScalingClient &client);

        bool describeGroup(const Aws::String &groupName,
                           Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroup,
                           const Aws::AutoScaling::AutoScalingClient &client);

        void printGroupInfo(
                const Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups);

        bool
        cleanupResources(const Aws::String &groupName,
                         const Aws::String &templateName,
                         const Aws::AutoScaling::AutoScalingClient &autoScalingClient,
                         const Aws::EC2::EC2Client &ec2Client);

        Aws::Vector<Aws::String> instancesToInstanceIDs(
                const Aws::Vector<Aws::AutoScaling::Model::Instance> &instances) {
            Aws::Vector<Aws::String> instanceIDs;
            for (const Aws::AutoScaling::Model::Instance &instance: instances) {
                instanceIDs.push_back(instance.GetInstanceId());
            }

            return instanceIDs;
        }

        bool stringInVector(const Aws::String &string,
                            const std::vector<Aws::String> &aVector) {
            return std::find(aVector.begin(), aVector.end(), string) != aVector.end();
        }

        Aws::String askQuestion(const Aws::String &string,
                                const std::function<bool(
                                        Aws::String)> &test = [](
                                        const Aws::String &) -> bool { return true; });

        int askQuestionForIntRange(const Aws::String &string, int low,
                                   int high);
    } // AutoScaling
} // AwsDoc


//! Scenario TODO:(developer) add description.
/*!
  \sa groupsAndInstancesScenario()
  \param clientConfig Aws client configuration.
 */
bool AwsDoc::AutoScaling::groupsAndInstancesScenario(
        const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::String templateName("test_template");
    Aws::EC2::Model::InstanceType instanceType = Aws::EC2::Model::InstanceType::t1_micro;
    Aws::String imageID("ami-0b0dcb5067f052a63");
    Aws::EC2::EC2Client ec2Client(clientConfig);

    {
        Aws::EC2::Model::CreateLaunchTemplateRequest request;
        request.SetLaunchTemplateName(templateName);

        Aws::EC2::Model::RequestLaunchTemplateData requestLaunchTemplateData;
        requestLaunchTemplateData.SetInstanceType(instanceType);
        requestLaunchTemplateData.SetImageId(imageID);

        request.SetLaunchTemplateData(requestLaunchTemplateData);

        Aws::EC2::Model::CreateLaunchTemplateOutcome outcome = ec2Client.CreateLaunchTemplate(
                request);


        if (outcome.IsSuccess()) {
            std::cout << "EC2::CreateLaunchTemplate was successful." << std::endl;
        }
        else if (outcome.GetError().GetExceptionName() ==
                 "InvalidLaunchTemplateName.AlreadyExistsException") {
            std::cout << "The template '" << templateName << "' already exists"
                      << std::endl;
        }
        else {
            std::cerr << "Error with EC2::CreateLaunchTemplate. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    Aws::Vector<Aws::EC2::Model::AvailabilityZone> availabilityZones;
    {
        Aws::EC2::Model::DescribeAvailabilityZonesRequest request;

        Aws::EC2::Model::DescribeAvailabilityZonesOutcome outcome = ec2Client.DescribeAvailabilityZones(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "EC2::DescribeAvailabilityZones was successful." << std::endl;

            availabilityZones = outcome.GetResult().GetAvailabilityZones();
        }
        else {
            std::cerr << "Error with EC2::DescribeAvailabilityZones. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }

    }
    Aws::String groupName("examples_group"); // TODO(developer): ask for group name
    // TODO(developer): select group from availability zones

    Aws::Vector<Aws::String> availabilityGroupZones;
    availabilityGroupZones.push_back(availabilityZones[0].GetZoneName());
    Aws::AutoScaling::AutoScalingClient autoScalingClient(clientConfig);
    {
        Aws::AutoScaling::Model::CreateAutoScalingGroupRequest request;
        request.SetAutoScalingGroupName(groupName);
        request.SetAvailabilityZones(availabilityGroupZones);
        request.SetMaxSize(1);
        request.SetMinSize(1);

        Aws::AutoScaling::Model::LaunchTemplateSpecification launchTemplateSpecification;
        launchTemplateSpecification.SetLaunchTemplateName(templateName);
        request.SetLaunchTemplate(launchTemplateSpecification);

        Aws::AutoScaling::Model::CreateAutoScalingGroupOutcome outcome = autoScalingClient.CreateAutoScalingGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "AutoScaling::CreateAutoScalingGroup was successful."
                      << std::endl;
        }
        else if (outcome.GetError().GetErrorType() ==
                 Aws::AutoScaling::AutoScalingErrors::ALREADY_EXISTS_FAULT) {
            std::cout << "AutoScaling group '" << groupName << "' already exists."
                      << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::CreateAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> autoScalingGroups;
    if (AwsDoc::AutoScaling::describeGroup(groupName, autoScalingGroups,
                                           autoScalingClient)) {
        std::cout << "Retrieved " << autoScalingGroups.size() << " groups."
                  << std::endl;
        if (!autoScalingGroups.empty()) {
            printGroupInfo(autoScalingGroups);
        }
    }
    else {
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    if (!waitForInstances(groupName, autoScalingGroups, autoScalingClient)) {
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    // TODO: ask to enable metrics

    {
        Aws::AutoScaling::Model::EnableMetricsCollectionRequest request;
        request.SetAutoScalingGroupName(groupName);

        request.AddMetrics("GroupMinSize");
        request.AddMetrics("GroupMaxSize");
        request.AddMetrics("GroupDesiredCapacity");
        request.AddMetrics("GroupInServiceInstances");
        request.AddMetrics("GroupTotalInstances");
        request.SetGranularity("1Minute");

        Aws::AutoScaling::Model::EnableMetricsCollectionOutcome outcome = autoScalingClient.EnableMetricsCollection(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "AutoScaling::EnableMetricsCollection was successful."
                      << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::EnableMetricsCollection. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
            return false;
        }
    }

    askQuestion("Update maximum instance?");
    //  update maximum instances from 1 to 3
    {
        Aws::AutoScaling::Model::UpdateAutoScalingGroupRequest request;
        request.SetAutoScalingGroupName(groupName);
        request.SetMaxSize(3);

        Aws::AutoScaling::Model::UpdateAutoScalingGroupOutcome outcome = autoScalingClient.UpdateAutoScalingGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "AutoScaling::UpdateAutoScalingGroup was successful."
                      << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::UpdateAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    if (AwsDoc::AutoScaling::describeGroup(groupName, autoScalingGroups,
                                           autoScalingClient)) {
        if (!autoScalingGroups.empty()) {
            const auto &instances = autoScalingGroups[0].GetInstances();
            std::cout
                    << "After setting the group size to 3, the instance count is "
                    << instances.size() << "." << std::endl;
            printGroupInfo(autoScalingGroups);
        }
        else {
            std::cerr << "No groups were retrieved from DescribeGroup request."
                      << std::endl;
        }
    }

    askQuestion("Change desired capacity?");

    // change the desired capacity from 1 to 2
    {
        Aws::AutoScaling::Model::UpdateAutoScalingGroupRequest request;
        request.SetAutoScalingGroupName(groupName);
        request.SetDesiredCapacity(2);

        Aws::AutoScaling::Model::UpdateAutoScalingGroupOutcome outcome = autoScalingClient.UpdateAutoScalingGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "AutoScaling::UpdateAutoScalingGroup was successful."
                      << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::UpdateAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    if (AwsDoc::AutoScaling::describeGroup(groupName, autoScalingGroups,
                                           autoScalingClient)) {
        if (!autoScalingGroups.empty()) {
            std::cout
                    << "After setting the desired capacity to 3, the instance count is "
                    << autoScalingGroups[0].GetInstances().size() << "." << std::endl;
            printGroupInfo(autoScalingGroups);
        }
        else {
            std::cerr << "No groups were retrieved from DescribeGroup request."
                      << std::endl;
        }
    }

    std::cout << "Waiting for the new instance to start..." << std::endl;

    waitForInstances(groupName, autoScalingGroups, autoScalingClient);

    std::cout << "Let's terminate one of the instances in " << groupName << "."
              << std::endl;
    std::cout << "Because the desired capacity is 2, another instance will start."
              << std::endl;
    std::cout << "The currently running instances are:" << std::endl;

    if (autoScalingGroups.empty()) {
        std::cerr << "Error describing groups. No groups returned." << std::endl;
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    int index = 1;
    Aws::Vector<Aws::String> instanceIDs = instancesToInstanceIDs(
            autoScalingGroups[0].GetInstances());
    for (const Aws::String &instanceID: instanceIDs) {
        std::cout << "   " << index << ". " << instanceID << std::endl;
        ++index;
    }

    // TODO: ask for instance choice
    askQuestion("Terminate capacity?");

    index = 0;
    {
        Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupRequest request;
        request.SetInstanceId(instanceIDs[index]);
        request.SetShouldDecrementDesiredCapacity(false);

        Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupOutcome outcome = autoScalingClient.TerminateInstanceInAutoScalingGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully terminated instance with id '"
                      << instanceIDs[index] << "'." << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::TerminateInstanceInAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }
    // Sleep one second for termination to start.
    std::this_thread::sleep_for(std::chrono::seconds(1));
    waitForInstances(groupName, autoScalingGroups, autoScalingClient);

    if (autoScalingGroups.empty()) {
        std::cerr << "Error describing groups. No groups returned." << std::endl;
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    instanceIDs = instancesToInstanceIDs(autoScalingGroups[0].GetInstances());
    std::cout
            << "The instance count is "
            << instanceIDs.size() << "." << std::endl;

    printGroupInfo(autoScalingGroups);

    return cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
}

bool AwsDoc::AutoScaling::waitForInstances(const Aws::String &groupName,
                                           Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups,
                                           const Aws::AutoScaling::AutoScalingClient &client) {
    bool ready = false;
    const std::vector<Aws::String> READY_STATES = {"InService", "Terminated"};

    int count = 0;
    while (!ready) {

        std::this_thread::sleep_for(std::chrono::seconds(1));
        ++count;
        if (!describeGroup(groupName, autoScalingGroups, client)) {
            return false;
        }
        Aws::Vector<Aws::String> instanceIDs;
        if (!autoScalingGroups.empty()) {
            instanceIDs = instancesToInstanceIDs(autoScalingGroups[0].GetInstances());
            if (count % 10 == 0)
            {
                std::cout << "Waiting for " << instanceIDs.size() << " to be terminated or in service." << std::endl;
            }
        }
        else if (count % 10 == 0)
        {
            std::cout << "Waiting for group to appear." << std::endl;
        }

        if (instanceIDs.empty()) {
            continue;
        }

        Aws::AutoScaling::Model::DescribeAutoScalingInstancesRequest request;
        request.SetInstanceIds(instanceIDs);

        Aws::AutoScaling::Model::DescribeAutoScalingInstancesOutcome outcome = client.DescribeAutoScalingInstances(
                request);

        if (outcome.IsSuccess()) {

            const auto &instancesDetails = outcome.GetResult().GetAutoScalingInstances();

            std::cout << "Waiting instance count " << instancesDetails.size()
                      << std::endl;
            ready = instancesDetails.size() > 0;
            for (const auto &instanceDetails: instancesDetails) {
                const Aws::String &lifecycleState = instanceDetails.GetLifecycleState();
                std::cout << lifecycleState << std::endl;

                if (!stringInVector(lifecycleState, READY_STATES)) {
                    ready = false;
                    break;
                }
            }
        }
        else {
            std::cerr << "Error with AutoScaling::DescribeAutoScalingInstances. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    if (!describeGroup(groupName, autoScalingGroups, client)) {
        return false;
    }

    return true;
}

bool AwsDoc::AutoScaling::cleanupResources(const Aws::String &groupName,
                                           const Aws::String &templateName,
                                           const Aws::AutoScaling::AutoScalingClient &autoScalingClient,
                                           const Aws::EC2::EC2Client &ec2Client) {
    bool result = true;
    // TODO: ask to delete group
    if (!groupName.empty()) {
        {
            Aws::AutoScaling::Model::UpdateAutoScalingGroupRequest request;
            request.SetAutoScalingGroupName(groupName);
            request.SetMinSize(0);
            request.SetDesiredCapacity(0);

            Aws::AutoScaling::Model::UpdateAutoScalingGroupOutcome outcome = autoScalingClient.UpdateAutoScalingGroup(
                    request);

            if (outcome.IsSuccess()) {
                std::cout
                        << "The minimum size of the autoscaling group was set to zero before "
                        << "terminating the instances."
                        << std::endl;
            }
            else {
                std::cerr << "Error with AutoScaling::UpdateAutoScalingGroup. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
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

                    Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupOutcome outcome = autoScalingClient.TerminateInstanceInAutoScalingGroup(
                            request);

                    if (outcome.IsSuccess()) {
                        std::cout << "Successfully terminated the instance '"
                                  << instanceID << "'." << std::endl;
                    }
                    else {
                        std::cerr
                                << "Error with AutoScaling::TerminateInstanceInAutoScalingGroup. "
                                << outcome.GetError().GetMessage() << std::endl;
                    }
                }
            }
        }
        {
            Aws::AutoScaling::Model::DeleteAutoScalingGroupRequest request;
            request.SetAutoScalingGroupName(groupName);

            Aws::AutoScaling::Model::DeleteAutoScalingGroupOutcome outcome = autoScalingClient.DeleteAutoScalingGroup(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "AutoScaling::DeleteAutoScalingGroup was successful."
                          << std::endl;
            }
            else {
                std::cerr << "Error with AutoScaling::DeleteAutoScalingGroup. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                result = false;
            }
        }
    }

// TODO: ask to delete template
    if (!templateName.empty()) {
        Aws::EC2::Model::DeleteLaunchTemplateRequest request;
        request.SetLaunchTemplateName(templateName);

        Aws::EC2::Model::DeleteLaunchTemplateOutcome outcome = ec2Client.DeleteLaunchTemplate(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "EC2::DeleteLaunchTemplate was successful." << std::endl;
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

bool AwsDoc::AutoScaling::describeGroup(const Aws::String &groupName,
                                        Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroup,
                                        const Aws::AutoScaling::AutoScalingClient &client) {
    Aws::AutoScaling::Model::DescribeAutoScalingGroupsRequest request;
    Aws::Vector<Aws::String> groupNames;
    groupNames.push_back(groupName);
    request.SetAutoScalingGroupNames(groupNames);

    Aws::AutoScaling::Model::DescribeAutoScalingGroupsOutcome outcome = client.DescribeAutoScalingGroups(
            request);

    if (outcome.IsSuccess()) {
        autoScalingGroup = outcome.GetResult().GetAutoScalingGroups();
    }
    else {
        std::cerr << "Error with AutoScaling::DescribeAutoScalingGroups. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

void AwsDoc::AutoScaling::printGroupInfo(
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

// snippet-end:[cpp.example_code.s3.Scenario_GettingStarted]

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {

    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::AutoScaling::groupsAndInstancesScenario(clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

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
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
        if (!test(result)) {
            result.clear();
        }
    } while (result.empty());

    return result;
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
