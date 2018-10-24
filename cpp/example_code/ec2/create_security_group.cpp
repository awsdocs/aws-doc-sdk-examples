 
//snippet-sourcedescription:[create_security_group.cpp demonstrates how to create an Amazon EC2 security group.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
#include <aws/ec2/model/CreateSecurityGroupRequest.h>
#include <aws/ec2/model/CreateSecurityGroupResponse.h>
#include <aws/ec2/model/AuthorizeSecurityGroupIngressRequest.h>
#include <iostream>

void BuildSampleIngressRule(
    Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest& authorize_request)
{
    Aws::EC2::Model::IpRange ip_range;
    ip_range.SetCidrIp("0.0.0.0/0");

    Aws::EC2::Model::IpPermission permission1;
    permission1.SetIpProtocol("tcp");
    permission1.SetToPort(80);
    permission1.SetFromPort(80);
    permission1.AddIpRanges(ip_range);

    authorize_request.AddIpPermissions(permission1);

    Aws::EC2::Model::IpPermission permission2;
    permission2.SetIpProtocol("tcp");
    permission2.SetToPort(22);
    permission2.SetFromPort(22);
    permission2.AddIpRanges(ip_range);

    authorize_request.AddIpPermissions(permission2);
}

void CreateSecurityGroup(
    const Aws::String& group_name, const Aws::String& description,
    const Aws::String& vpc_id)
{
    Aws::EC2::EC2Client ec2;
    Aws::EC2::Model::CreateSecurityGroupRequest request;

    request.SetGroupName(group_name);
    request.SetDescription(description);
    request.SetVpcId(vpc_id);

    auto outcome = ec2.CreateSecurityGroup(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Failed to create security group:" <<
            outcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully created security group named " << group_name <<
        std::endl;

    Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest authorize_request;

    authorize_request.SetGroupName(group_name);

    BuildSampleIngressRule(authorize_request);

    auto ingress_request = ec2.AuthorizeSecurityGroupIngress(
        authorize_request);

    if (!ingress_request.IsSuccess())
    {
        std::cout << "Failed to set ingress policy for security group " <<
            group_name << ":" << ingress_request.GetError().GetMessage() <<
            std::endl;
        return;
    }

    std::cout << "Successfully added ingress policy to security group " <<
        group_name << std::endl;
}

/**
 * Creates an ec2 security group based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage: create_security_group <group_name> " <<
            "<group_description> <vpc_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String group_name = argv[1];
        Aws::String group_desc = argv[2];
        Aws::String vpc_id = argv[3];

        CreateSecurityGroup(group_name, group_desc, vpc_id);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

