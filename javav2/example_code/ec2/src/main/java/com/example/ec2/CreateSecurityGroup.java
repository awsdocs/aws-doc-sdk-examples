//snippet-sourcedescription:[CreateSecurityGroup.java demonstrates how to create an EC2 security group.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[ec2.java.create_security_group.complete]
// snippet-start:[ec2.java.create_security_group.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressResponse;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.IpRange;
 
// snippet-end:[ec2.java.create_security_group.import]
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
        
        // snippet-start:[ec2.java.create_security_group.main]
        // snippet-start:[ec2.java.create_security_group.client]
        Ec2Client ec2 = Ec2Client.create();
        // snippet-end:[ec2.java.create_security_group.client]

        // snippet-start:[ec2.java.create_security_group.create]
        CreateSecurityGroupRequest create_request = CreateSecurityGroupRequest.builder()
                .groupName(group_name)
                .description(group_desc)
                .vpcId(vpc_id)
                .build();

        CreateSecurityGroupResponse create_response =
            ec2.createSecurityGroup(create_request);
        // snippet-end:[ec2.java.create_security_group.create]

        System.out.printf(
            "Successfully created security group named %s",
            group_name);

        // snippet-start:[ec2.java.create_security_group.config]
        IpRange ip_range = IpRange.builder()
            .cidrIp("0.0.0.0/0").build();

        IpPermission ip_perm = IpPermission.builder()
            .ipProtocol("tcp")
            .toPort(80)
            .fromPort(80)
                .ipRanges(ip_range)
           // .ipv4Ranges(ip_range)
            .build();

        IpPermission ip_perm2 = IpPermission.builder()
            .ipProtocol("tcp")
            .toPort(22)
            .fromPort(22)
            .ipRanges(ip_range)
            .build();

        AuthorizeSecurityGroupIngressRequest auth_request =
            AuthorizeSecurityGroupIngressRequest.builder()
                .groupName(group_name)
                .ipPermissions(ip_perm, ip_perm2)
                .build();

        AuthorizeSecurityGroupIngressResponse auth_response =
            ec2.authorizeSecurityGroupIngress(auth_request);

        // snippet-end:[ec2.java.create_security_group.config]
        // snippet-end:[ec2.java.create_security_group.main]
        System.out.printf(
            "Successfully added ingress policy to security group %s",
            group_name);
    }
}
 
// snippet-end:[ec2.java.create_security_group.complete]
