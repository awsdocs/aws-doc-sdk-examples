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
#include <aws/ec2/model/DescribeSecurityGroupsRequest.h>
#include <aws/ec2/model/DescribeSecurityGroupsResponse.h>
#include <iostream>

/**
 * Describes all ec2 security groups, or a specific group
 */
int main(int argc, char** argv)
{
    if(argc > 2) {
        std::cout << "Usage: ec2_describe_security_groups [group_id]" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::EC2::EC2Client ec2;
        Aws::EC2::Model::DescribeSecurityGroupsRequest request;

        if (argc == 2) {
            request.AddGroupIds(argv[1]);
        }

        auto outcome = ec2.DescribeSecurityGroups(request);

        if (outcome.IsSuccess()) {
            std::cout << std::left << std::setw(32) << "Name" << std::setw(20)
                << "GroupId" << std::setw(20) << "VpcId" << std::setw(64) <<
                "Description" << std::endl;

            const auto &securityGroups =
                outcome.GetResult().GetSecurityGroups();

            for (const auto &securityGroup : securityGroups) {
                std::cout << std::left << std::setw(32) <<
                    securityGroup.GetGroupName() << std::setw(20) <<
                    securityGroup.GetGroupId() << std::setw(20) <<
                    securityGroup.GetVpcId() << std::setw(64) <<
                    securityGroup.GetDescription() << std::endl;
            }
        } else {
            std::cout << "Failed to describe security groups:" <<
                outcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

