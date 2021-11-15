// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_security_group.cpp demonstrates how to delete an Amazon EC2 security group.

*/


//snippet-start:[ec2.cpp.delete_security_group.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DeleteSecurityGroupRequest.h>
#include <iomanip>
#include <iostream>
//snippet-end:[ec2.cpp.delete_security_group.inc]

/**
 * Deletes a security group based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: delete_security_group <group_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String groupId = argv[1];

        // snippet-start:[ec2.cpp.delete_security_group.code]
        Aws::EC2::EC2Client ec2;
        Aws::EC2::Model::DeleteSecurityGroupRequest request;

        request.SetGroupId(groupId);
        auto outcome = ec2.DeleteSecurityGroup(request);

        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to delete security group " << groupId <<
                ":" << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully deleted security group " << groupId <<
                std::endl;
        }
        // snippet-end:[ec2.cpp.delete_security_group.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

