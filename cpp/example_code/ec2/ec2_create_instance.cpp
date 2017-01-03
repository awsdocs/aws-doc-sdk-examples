/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateTagsRequest.h>
#include <aws/ec2/model/RunInstancesRequest.h>
#include <aws/ec2/model/RunInstancesResponse.h>

#include <iostream>

void StartInstance(const Aws::String& instanceName, const Aws::String& amiImageId)
{
    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::RunInstancesRequest runInstancesRequest;
    runInstancesRequest.SetImageId(amiImageId);
    runInstancesRequest.SetInstanceType(Aws::EC2::Model::InstanceType::t1_micro);
    runInstancesRequest.SetMinCount(1);
    runInstancesRequest.SetMaxCount(1);

    auto runInstancesOutcome = ec2_client.RunInstances(runInstancesRequest);
    if(!runInstancesOutcome.IsSuccess())
    {
        std::cout << "Failed to start ec2 instance " << instanceName << " based on ami " << amiImageId << ":" << runInstancesOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    const auto& instances = runInstancesOutcome.GetResult().GetInstances();
    if(instances.size() == 0)
    {
        std::cout << "Failed to start ec2 instance " << instanceName << " based on ami " << amiImageId << ":" << runInstancesOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    auto instanceId = instances[0].GetInstanceId();

    Aws::EC2::Model::Tag nameTag;
    nameTag.SetKey("Name");
    nameTag.SetValue(instanceName);

    Aws::EC2::Model::CreateTagsRequest createTagsRequest;
    createTagsRequest.AddResources(instanceId);
    createTagsRequest.AddTags(nameTag);

    auto createTagsOutcome = ec2_client.CreateTags(createTagsRequest);
    if(!createTagsOutcome.IsSuccess())
    {
        std::cout << "Failed to tag ec2 instance " << instanceId << " with name " << instanceName << ":" << createTagsOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully started ec2 instance " << instanceName << " based on ami " << amiImageId << std::endl;
}

/**
 * Creates an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: ec2_create_instance <instance_name> <ami_image_id>" << std::endl;
        return 1;
    }

    Aws::String instanceName = argv[1];
    Aws::String amiImageId = argv[2];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    StartInstance(instanceName, amiImageId);

    Aws::ShutdownAPI(options);

    return 0;
}



