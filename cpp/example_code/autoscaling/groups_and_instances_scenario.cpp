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
#include <iomanip>
#include <aws/core/Aws.h>
#include <aws/autoscaling/AutoScalingClient.h>
#include <aws/autoscaling/model/CreateAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/DeleteAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/DescribeScalingActivitiesRequest.h>
#include <aws/autoscaling/model/DescribeAutoScalingGroupsRequest.h>
#include <aws/autoscaling/model/DescribeAutoScalingInstancesRequest.h>
#include <aws/autoscaling/model/EnableMetricsCollectionRequest.h>
#include <aws/autoscaling/model/TerminateInstanceInAutoScalingGroupRequest.h>
#include <aws/autoscaling/model/UpdateAutoScalingGroupRequest.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateLaunchTemplateRequest.h>
#include <aws/ec2/model/DeleteLaunchTemplateRequest.h>
#include <aws/ec2/model/DescribeAvailabilityZonesRequest.h>
#include <aws/ec2/model/DescribeLaunchTemplatesRequest.h>

namespace AwsDoc {
    namespace AutoScaling {
        static const int ASTERISK_FILL_WIDTH = 88;

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

        void logInstancesLifecycleState(
                const Aws::Vector<Aws::AutoScaling::Model::AutoScalingInstanceDetails> &
                instancesDetails);
    } // AutoScaling
} // AwsDoc


//! Scenario TODO:(developer) add description.
/*!
  \sa groupsAndInstancesScenario()
  \param clientConfig Aws client configuration.
 */
bool AwsDoc::AutoScaling::groupsAndInstancesScenario(
        const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::String templateName;
    Aws::EC2::Model::InstanceType instanceType = Aws::EC2::Model::InstanceType::t1_micro;
    Aws::String imageID("ami-0b0dcb5067f052a63");
    Aws::EC2::EC2Client ec2Client(clientConfig);

    std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " " << std::endl;
    std::cout << "Welcome to the Amazon EC2 Auto Scaling demo for managing groups and instances." << std::endl;
    std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " \n" << std::endl;

    std::cout << "This example requires an EC2 launch template." << std::endl;
    Aws::String answer = askQuestion("Would you like to use an existing launch template? (y/n)  ");
    if (answer == "y") {
        templateName = askQuestion("Enter the name of the existing launch template.  ");

        Aws::EC2::Model::DescribeLaunchTemplatesRequest request;
        request.AddLaunchTemplateNames(templateName);
        Aws::EC2::Model::DescribeLaunchTemplatesOutcome outcome = ec2Client.DescribeLaunchTemplates(request);

        if (outcome.IsSuccess()) {
            std::cout << "Validated the launch template '" << templateName << "' exists." << std::endl;
        }
        else {
            std::cerr << "Error validating the existence of the launch template. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }
    else { // Create a new EC2 launch template.
        templateName = askQuestion("Enter the name for a new launch template.  ");

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

    Aws::AutoScaling::AutoScalingClient autoScalingClient(clientConfig);

    std::cout << "Let's create an Auto Scaling group." << std::endl;
    Aws::String  groupName = askQuestion("Enter a name for the group:  ");


    // Describe the availability zones.
    Aws::Vector<Aws::EC2::Model::AvailabilityZone> availabilityZones;
    {
        Aws::EC2::Model::DescribeAvailabilityZonesRequest request;

        Aws::EC2::Model::DescribeAvailabilityZonesOutcome outcome = ec2Client.DescribeAvailabilityZones(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "EC2 instances can be created in the following Availability Zones:" << std::endl;

            availabilityZones = outcome.GetResult().GetAvailabilityZones();
            for (size_t i = 0; i < availabilityZones.size(); ++i)
            {
                std::cout << "   " << i + 1 << ".  " << availabilityZones[i].GetZoneName() << std::endl;
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
    int availabilityZoneChoice = askQuestionForIntRange("Choose an availability zone:  ", 1,
                                                        static_cast<int>(availabilityZones.size()));

    {
        Aws::AutoScaling::Model::CreateAutoScalingGroupRequest request;
        request.SetAutoScalingGroupName(groupName);
        Aws::Vector<Aws::String> availabilityGroupZones;
        availabilityGroupZones.push_back(availabilityZones[availabilityZoneChoice - 1].GetZoneName());
        request.SetAvailabilityZones(availabilityGroupZones);
        request.SetMaxSize(1);
        request.SetMinSize(1);

        Aws::AutoScaling::Model::LaunchTemplateSpecification launchTemplateSpecification;
        launchTemplateSpecification.SetLaunchTemplateName(templateName);
        request.SetLaunchTemplate(launchTemplateSpecification);

        Aws::AutoScaling::Model::CreateAutoScalingGroupOutcome outcome = autoScalingClient.CreateAutoScalingGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "Created autoscaling group '" << groupName << "'..." << std::endl;
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
        std::cout << "Here is the autoscaling group description." << std::endl;
        if (!autoScalingGroups.empty()) {
            printGroupInfo(autoScalingGroups);
        }
    }
    else {
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }

    std::cout
            << "Waiting for the EC2 instance in the auto scaling group to become active..."
            << std::endl;
    if (!waitForInstances(groupName, autoScalingGroups, autoScalingClient)) {
        cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
        return false;
    }


    bool enableMetrics = askQuestion("Do you want to collect metrics about Amazon "
                                      "EC2 Auto Scaling during this demo (y/n)?  ") == "y";

    if (enableMetrics)
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
            std::cout << "Autoscaling metrics have been enabled."
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

    std::cout << "Let's update the maximum number of instances in '" << groupName <<
    "' from 1 to 3." << std::endl;
    askQuestion("Enter any text to continue  ");
    {
        Aws::AutoScaling::Model::UpdateAutoScalingGroupRequest request;
        request.SetAutoScalingGroupName(groupName);
        request.SetMaxSize(3);

        Aws::AutoScaling::Model::UpdateAutoScalingGroupOutcome outcome = autoScalingClient.UpdateAutoScalingGroup(
                request);

        if (!outcome.IsSuccess()) {
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
                    << "The group still has one running instance, but it can have up to 3.\n"
                    << std::endl;
            printGroupInfo(autoScalingGroups);
        }
        else {
            std::cerr << "No groups were retrieved from DescribeGroup request."
                      << std::endl;
        }
    }

    std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " " << std::endl;
    std::cout << "Let's update the desired capacity in '" << groupName <<
              "' from 1 to 2." << std::endl;
    askQuestion("Enter any text to continue  ");
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
                    << "Here is the current state of the group." << std::endl;
            printGroupInfo(autoScalingGroups);
        }
        else {
            std::cerr << "No groups were retrieved from DescribeGroup request."
                      << std::endl;
        }
    }

    std::cout << "Waiting for the new EC2 instance to start..." << std::endl;

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

    int instanceNumber = 1;
    Aws::Vector<Aws::String> instanceIDs = instancesToInstanceIDs(
            autoScalingGroups[0].GetInstances());
    for (const Aws::String &instanceID: instanceIDs) {
        std::cout << "   " << instanceNumber << ". " << instanceID << std::endl;
        ++instanceNumber;
    }

    instanceNumber = askQuestionForIntRange("Which instance do you want to stop? ", 1,
                                            static_cast<int>(instanceIDs.size()));

     {
        Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupRequest request;
        request.SetInstanceId(instanceIDs[instanceNumber - 1]);
        request.SetShouldDecrementDesiredCapacity(false);

        Aws::AutoScaling::Model::TerminateInstanceInAutoScalingGroupOutcome outcome = autoScalingClient.TerminateInstanceInAutoScalingGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "Waiting for instance with ID '"
                      << instanceIDs[index] << "' to terminate..." << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::TerminateInstanceInAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    waitForInstances(groupName, autoScalingGroups, autoScalingClient);

    std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " " << std::endl;
    std::cout << "Let's get a report of scaling activities for '" << groupName << "'." << std::endl;
    askQuestion("Enter any text to continue  ");
    {
        Aws::AutoScaling::Model::DescribeScalingActivitiesRequest request;
        request.SetAutoScalingGroupName(groupName);

        Aws::AutoScaling::Model::DescribeScalingActivitiesOutcome outcome = autoScalingClient.DescribeScalingActivities(request);

        if (outcome.IsSuccess()) {
            const Aws::Vector<Aws::AutoScaling::Model::Activity>& activities =  outcome.GetResult().GetActivities();
            std::cout << "Found " << activities.size() << " activities." << std::endl;
            std::cout << "Activities are order with the most recent first." << std::endl;
            for (const Aws::AutoScaling::Model::Activity& activity : activities)
            {
                std::cout << activity.GetDescription() << std::endl;
                std::cout << activity.GetDetails() << std::endl;
            }
        }
        else {
            std::cerr << "Error with AutoScaling::DescribeScalingActivities. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    if (enableMetrics)
    {
        std::cout << "Let's look at CloudWatch metrics." << std::endl;
    }
    return cleanupResources(groupName, templateName, autoScalingClient, ec2Client);
}

bool AwsDoc::AutoScaling::waitForInstances(const Aws::String &groupName,
                                           Aws::Vector<Aws::AutoScaling::Model::AutoScalingGroup> &autoScalingGroups,
                                           const Aws::AutoScaling::AutoScalingClient &client) {
    bool ready = false;
    const std::vector<Aws::String> READY_STATES = {"InService", "Terminated"};

    int count = 0;
    int desiredCapacity = 0;
    while (!ready) {

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
                continue;
            }
        }

        Aws::AutoScaling::Model::DescribeAutoScalingInstancesRequest request;
        request.SetInstanceIds(instanceIDs);

        Aws::AutoScaling::Model::DescribeAutoScalingInstancesOutcome outcome = client.DescribeAutoScalingInstances(
                request);

        if (outcome.IsSuccess()) {
            const auto &instancesDetails = outcome.GetResult().GetAutoScalingInstances();
            ready = instancesDetails.size() >= desiredCapacity;
            for (const Aws::AutoScaling::Model::AutoScalingInstanceDetails &details: instancesDetails) {
                if (!stringInVector(details.GetLifecycleState(), READY_STATES)) {
                    ready = false;
                    break;
                }
            }
            // Log the status while waiting;
            if (((count % 5) == 1) || ready) {
                logInstancesLifecycleState(instancesDetails);
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
                        std::cout << "Initiating termination of EC2 instance '"
                                  << instanceID << "'." << std::endl;
                    }
                    else {
                        std::cerr
                                << "Error with AutoScaling::TerminateInstanceInAutoScalingGroup. "
                                << outcome.GetError().GetMessage() << std::endl;
                    }
                }
            }

            std::cout
                    << "Waiting for the EC2 instances to terminate before deleting the AutoScaling group..."
                    << std::endl;
            waitForInstances(groupName, autoScalingGroups, autoScalingClient);
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
