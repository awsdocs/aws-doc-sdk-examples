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
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateLaunchTemplateRequest.h>
#include <aws/ec2/model/DeleteLaunchTemplateRequest.h>
#include <aws/ec2/model/DescribeAvailabilityZonesRequest.h>

namespace AwsDoc {
    namespace AutoScaling {
        bool groupsAndInstancesScenario(const Aws::Client::ClientConfiguration &clientConfig);
    } // AutoScaling
} // AwsDoc


//! Scenario TODO:(developer) add description.
/*!
  \sa groupsAndInstancesScenario()
  \param clientConfig Aws client configuration.
 */
bool AwsDoc::AutoScaling::groupsAndInstancesScenario(const Aws::Client::ClientConfiguration &clientConfig) {
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
        else {
            std::cerr << "Error with EC2::CreateLaunchTemplate. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

     Aws::Vector<Aws::EC2::Model::AvailabilityZone> availabilityZones;
    {
        Aws::EC2::Model::DescribeAvailabilityZonesRequest request;

        Aws::EC2::Model::DescribeAvailabilityZonesOutcome outcome = ec2Client.DescribeAvailabilityZones(request);

        if (outcome.IsSuccess()) {
            std::cout << "EC2::DescribeAvailabilityZones was successful." << std::endl;

            availabilityZones = outcome.GetResult().GetAvailabilityZones();
        }
        else {
            std::cerr << "Error with EC2::DescribeAvailabilityZones. " << outcome.GetError().GetMessage()
                      << std::endl;
        }

    }
    Aws::String groupName("examples_group"); // TODO(developer): ask for group name
    // TODO(developer): select group from availability zones

    Aws::Vector<Aws::String> availabilityGroupZones;
    for (size_t i = 0; i < std::min(availabilityZones.size(), 4ul); ++i)
    {
        availabilityGroupZones.push_back(availabilityZones[i].GetZoneName());
    }
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

        Aws::AutoScaling::Model::CreateAutoScalingGroupOutcome outcome = autoScalingClient.CreateAutoScalingGroup(request);

        if (outcome.IsSuccess()) {
            std::cout << "AutoScaling::CreateAutoScalingGroup was successful." << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::CreateAutoScalingGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    Aws::Vector<Aws::String> instanceIDs;
    {
        Aws::AutoScaling::Model::DescribeAutoScalingGroupsRequest request;
        Aws::Vector<Aws::String> groupNames;
        groupNames.push_back(groupName);
        request.SetAutoScalingGroupNames(groupNames);

        Aws::AutoScaling::Model::DescribeAutoScalingGroupsOutcome outcome = autoScalingClient.DescribeAutoScalingGroups(request);

        if (outcome.IsSuccess()) {
            std::cout << "AutoScaling::DescribeAutoScalingGroups was successful." << std::endl;

            const auto& groupsResult = outcome.GetResult().GetAutoScalingGroups();
            std::cout << "Retrieve " << groupsResult.size() << " groups." << std::endl;
            if (groupsResult.size() > 0)
            {
                const auto& instances = groupsResult[0].GetInstances();
                for (const auto& instance : instances)
                {
                    instanceIDs.push_back(instance.GetInstanceId());
                }
            }
        }
        else {
            std::cerr << "Error with AutoScaling::DescribeAutoScalingGroups. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }

    }

    bool ready = false;

    while (!ready)
    {
        Aws::AutoScaling::Model::DescribeAutoScalingInstancesRequest request;
        request.SetInstanceIds(instanceIDs);

        Aws::AutoScaling::Model::DescribeAutoScalingInstancesOutcome outcome = autoScalingClient.DescribeAutoScalingInstances(request);

        if (outcome.IsSuccess()) {
            std::cout << "AutoScaling::DescribeAutoScalingInstances was successful." << std::endl;
        }
        else {
            std::cerr << "Error with AutoScaling::DescribeAutoScalingInstances. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
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
        }
    }

    {
        Aws::EC2::Model::DeleteLaunchTemplateRequest request;
        request.SetLaunchTemplateName(templateName);

        Aws::EC2::Model::DeleteLaunchTemplateOutcome outcome = ec2Client.DeleteLaunchTemplate(request);

        if (outcome.IsSuccess()) {
            std::cout << "EC2::DeleteLaunchTemplate was successful." << std::endl;
        }
        else {
            std::cerr << "Error with EC2::DeleteLaunchTemplate. " << outcome.GetError().GetMessage()
                      << std::endl;
        }

    }

    return true;
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


