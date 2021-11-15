
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
reboot_instance.cpp demonstrates how to reboot an Amazon EC2 instance.
*/


//snippet-start:[ec2.cpp.reboot_instance.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/RebootInstancesRequest.h>
#include <iostream>
//snippet-end:[ec2.cpp.reboot_instance.inc]

void RebootInstance(const Aws::String& instanceId)
{
    // snippet-start:[ec2.cpp.reboot_instance.code]
    Aws::EC2::EC2Client ec2;

    Aws::EC2::Model::RebootInstancesRequest request;
    request.AddInstanceIds(instanceId);
    request.SetDryRun(true);

    auto dry_run_outcome = ec2.RebootInstances(request);
    assert(!dry_run_outcome.IsSuccess());

    if (dry_run_outcome.GetError().GetErrorType()
        != Aws::EC2::EC2Errors::DRY_RUN_OPERATION)
    {
        std::cout << "Failed dry run to reboot instance " << instanceId << ": "
            << dry_run_outcome.GetError().GetMessage() << std::endl;
        return;
    }

    request.SetDryRun(false);
    auto outcome = ec2.RebootInstances(request);
    if (!outcome.IsSuccess())
    {
        std::cout << "Failed to reboot instance " << instanceId << ": " <<
            outcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully rebooted instance " << instanceId <<
            std::endl;
    }
    // snippet-end:[ec2.cpp.reboot_instance.code]
}

/**
 * Reboots an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: reboot_instance <instance_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String instanceId = argv[1];

        RebootInstance(instanceId);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

