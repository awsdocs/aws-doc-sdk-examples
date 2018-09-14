/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.ec2;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressResponse;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.IpRange;

/**
 * Creates an EC2 security group.
 */
public class CreateSecurityGroup
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply a group name, group description and vpc id\n" +
            "Ex: CreateSecurityGroup <group-name> <group-description> <vpc-id>\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String group_name = args[0];
        String group_desc = args[1];
        String vpc_id = args[2];

        Ec2Client ec2 = Ec2Client.create();

        CreateSecurityGroupRequest create_request = CreateSecurityGroupRequest.builder()
                .groupName(group_name)
                .description(group_desc)
                .vpcId(vpc_id)
                .build();

        CreateSecurityGroupResponse create_response =
            ec2.createSecurityGroup(create_request);

        System.out.printf(
            "Successfully created security group named %s",
            group_name);

        IpRange ip_range = IpRange.builder()
            .cidrIp("0.0.0.0/0").build();

        IpPermission ip_perm = IpPermission.builder()
            .ipProtocol("tcp")
            .toPort(80)
            .fromPort(80)
            .ipv4Ranges(ip_range)
            .build();

        IpPermission ip_perm2 = IpPermission.builder()
            .ipProtocol("tcp")
            .toPort(22)
            .fromPort(22)
            .ipv4Ranges(ip_range)
            .build();

        AuthorizeSecurityGroupIngressRequest auth_request = 
            AuthorizeSecurityGroupIngressRequest.builder()
                .groupName(group_name)
                .ipPermissions(ip_perm, ip_perm2)
                .build();

        AuthorizeSecurityGroupIngressResponse auth_response =
            ec2.authorizeSecurityGroupIngress(auth_request);

        System.out.printf(
            "Successfully added ingress policy to security group %s",
            group_name);
    }
}

