//snippet-sourcedescription:[CreateSecurityGroup.java demonstrates how to create an EC2 security group.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[ec2.java2.create_security_group.complete]
// snippet-start:[ec2.java2.create_security_group.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.IpRange;
// snippet-end:[ec2.java2.create_security_group.import]

/**
 * Creates an EC2 security group.
 */
public class CreateSecurityGroup {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a group name, group description and vpc id\n" +
                        "Ex: CreateSecurityGroup <group-name> <group-description> <vpc-id>\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String groupName = args[0];
        String groupDesc = args[1];
        String vpcId = args[2];

        // snippet-start:[ec2.java2.create_security_group.main]
        // snippet-start:[ec2.java2.create_security_group.client]
        Ec2Client ec2 = Ec2Client.create();
        // snippet-end:[ec2.java2.create_security_group.client]

        try {

            // snippet-start:[ec2.java2.create_security_group.create]
            CreateSecurityGroupRequest createRequest = CreateSecurityGroupRequest.builder()
                .groupName(groupName)
                .description(groupDesc)
                .vpcId(vpcId)
                .build();

            CreateSecurityGroupResponse createResponse =
                ec2.createSecurityGroup(createRequest);
            // snippet-end:[ec2.java2.create_security_group.create]

            System.out.printf(
                "Successfully created security group named %s",
                groupName);

            // snippet-start:[ec2.java2.create_security_group.config]
            IpRange ipRange = IpRange.builder()
                .cidrIp("0.0.0.0/0").build();

            IpPermission ipPerm = IpPermission.builder()
                .ipProtocol("tcp")
                .toPort(80)
                .fromPort(80)
                .ipRanges(ipRange)
                // .ipv4Ranges(ip_range)
                .build();

            IpPermission ipPerm2 = IpPermission.builder()
                .ipProtocol("tcp")
                .toPort(22)
                .fromPort(22)
                .ipRanges(ipRange)
                .build();

            AuthorizeSecurityGroupIngressRequest authRequest =
                AuthorizeSecurityGroupIngressRequest.builder()
                        .groupName(groupName)
                        .ipPermissions(ipPerm, ipPerm2)
                        .build();

            AuthorizeSecurityGroupIngressResponse authResponse =
                ec2.authorizeSecurityGroupIngress(authRequest);

            // snippet-end:[ec2.java2.create_security_group.config]
            // snippet-end:[ec2.java2.create_security_group.main]
            System.out.printf(
                "Successfully added ingress policy to security group %s",
                groupName);

        } catch (Ec2Exception e) {
            e.getStackTrace();
        }
    }
}

// snippet-end:[ec2.java2.create_security_group.complete]
