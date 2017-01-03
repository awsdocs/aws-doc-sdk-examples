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
#include <aws/ec2/model/AuthorizeSecurityGroupIngressRequest.h>
#include <aws/ec2/model/CreateSecurityGroupRequest.h>
#include <aws/ec2/model/CreateSecurityGroupResponse.h>

#include <iostream>

void BuildSampleIngressRule(Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest& request)
{
    Aws::EC2::Model::IpRange ipRange;
    ipRange.SetCidrIp("0.0.0.0/0");

    Aws::EC2::Model::IpPermission permission1;
    permission1.SetIpProtocol("tcp");
    permission1.SetToPort(80);
    permission1.SetFromPort(80);
    permission1.AddIpRanges(ipRange);

    request.AddIpPermissions(permission1);

    Aws::EC2::Model::IpPermission permission2;
    permission2.SetIpProtocol("tcp");
    permission2.SetToPort(22);
    permission2.SetFromPort(22);
    permission2.AddIpRanges(ipRange);

    request.AddIpPermissions(permission2);
}

void CreateSecurityGroup(const Aws::String& groupName, const Aws::String& description, const Aws::String& vpcId)
{
    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::CreateSecurityGroupRequest createSecurityGroupRequest;
    createSecurityGroupRequest.SetGroupName(groupName);
    createSecurityGroupRequest.SetDescription(description);
    createSecurityGroupRequest.SetVpcId(vpcId);

    auto createSecurityGroupOutcome = ec2_client.CreateSecurityGroup(createSecurityGroupRequest);
    if(!createSecurityGroupOutcome.IsSuccess())
    {
        std::cout << "Failed to create security group:" << createSecurityGroupOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully created security group named " << groupName << std::endl;

    Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest authorizeRequest;
    authorizeRequest.SetGroupName(groupName);
    BuildSampleIngressRule(authorizeRequest);

    auto authorizeSecurityGroupIngressOutcome = ec2_client.AuthorizeSecurityGroupIngress(authorizeRequest);
    if(!authorizeSecurityGroupIngressOutcome.IsSuccess())
    {
        std::cout << "Failed to set ingress policy for security group " << groupName << ":" << authorizeSecurityGroupIngressOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully added ingress policy to security group " << groupName << std::endl;
}

/**
 * Creates an ec2 security group based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 4)
    {
        std::cout << "Usage: ec2_create_security_group <group_name> <group_description> <vpc_id>" << std::endl;
        return 1;
    }

    Aws::String groupName = argv[1];
    Aws::String groupDescription = argv[2];
    Aws::String vpcId = argv[3];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    CreateSecurityGroup(groupName, groupDescription, vpcId);

    Aws::ShutdownAPI(options);

    return 0;
}



