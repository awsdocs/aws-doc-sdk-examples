 
//snippet-sourcedescription:[delete_security_group.cpp demonstrates how to delete an Amazon EC2 security group.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
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

