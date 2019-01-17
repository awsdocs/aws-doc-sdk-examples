 
//snippet-sourcedescription:[create_instance.cpp demonstrates how to create an Amazon EC2 instance.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
//snippet-start:[ec2.cpp.create_instance.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateTagsRequest.h>
#include <aws/ec2/model/RunInstancesRequest.h>
#include <aws/ec2/model/RunInstancesResponse.h>
#include <iostream>
//snippet-end:[ec2.cpp.create_instance.inc]

void StartInstance(const Aws::String& instanceName, const Aws::String& ami_id)
{
    // snippet-start:[ec2.cpp.create_instance.code]
    Aws::EC2::EC2Client ec2;

    Aws::EC2::Model::RunInstancesRequest run_request;
    run_request.SetImageId(ami_id);
    run_request.SetInstanceType(Aws::EC2::Model::InstanceType::t1_micro);
    run_request.SetMinCount(1);
    run_request.SetMaxCount(1);

    auto run_outcome = ec2.RunInstances(run_request);
    if (!run_outcome.IsSuccess())
    {
        std::cout << "Failed to start ec2 instance " << instanceName <<
            " based on ami " << ami_id << ":" <<
            run_outcome.GetError().GetMessage() << std::endl;
        return;
    }

    const auto& instances = run_outcome.GetResult().GetInstances();
    if (instances.size() == 0)
    {
        std::cout << "Failed to start ec2 instance " << instanceName <<
            " based on ami " << ami_id << ":" <<
            run_outcome.GetError().GetMessage() << std::endl;
        return;
    }
    // snippet-end:[ec2.cpp.create_instance.code]

    auto instance_id = instances[0].GetInstanceId();

    Aws::EC2::Model::Tag name_tag;
    name_tag.SetKey("Name");
    name_tag.SetValue(instanceName);

    Aws::EC2::Model::CreateTagsRequest create_request;
    create_request.AddResources(instance_id);
    create_request.AddTags(name_tag);

    auto create_outcome = ec2.CreateTags(create_request);
    if (!create_outcome.IsSuccess())
    {
        std::cout << "Failed to tag ec2 instance " << instance_id <<
            " with name " << instanceName << ":" <<
            create_outcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully started ec2 instance " << instanceName <<
        " based on ami " << ami_id << std::endl;
}

/**
 * Creates an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: create_instance <instance_name> <ami_image_id>"
            << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String instanceName = argv[1];
        Aws::String ami_id = argv[2];
        StartInstance(instanceName, ami_id);
    }
    Aws::ShutdownAPI(options);
    return 0;
}



