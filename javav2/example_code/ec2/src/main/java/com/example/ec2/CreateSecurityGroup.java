//snippet-sourcedescription:[CreateSecurityGroup.java demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) security group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/01/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ec2;

// snippet-start:[ec2.java2.create_security_group.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.IpRange;
// snippet-end:[ec2.java2.create_security_group.import]

/**
 * Creates an EC2 security group.
 */
public class CreateSecurityGroup {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "CreateSecurityGroup <groupName> <groupDesc> <vpcId> \n\n" +
                "Where:\n" +
                "    groupName - a group name (for example, TestKeyPair). \n\n"  +
                "    groupDesc - a group description  (for example, TestKeyPair). \n\n"  +
                "    vpcId - a VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f). \n\n"  ;

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line arguments
        String groupName = args[0];
        String groupDesc = args[1];
        String vpcId = args[2];

        //Create an Ec2Client object
        // snippet-start:[ec2.java2.create_security_group.client]
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();
        // snippet-end:[ec2.java2.create_security_group.client]


        String id = createEC2SecurityGroup(ec2, groupName, groupDesc, vpcId);
        System.out.printf(
                "Successfully created security group with this ID %s",
                id);
        ec2.close();
    }

    // snippet-start:[ec2.java2.create_security_group.main]
    public static String createEC2SecurityGroup( Ec2Client ec2,String groupName, String groupDesc, String vpcId) {
        try {

            // snippet-start:[ec2.java2.create_security_group.create]
            CreateSecurityGroupRequest createRequest = CreateSecurityGroupRequest.builder()
                .groupName(groupName)
                .description(groupDesc)
                .vpcId(vpcId)
                .build();

            CreateSecurityGroupResponse resp= ec2.createSecurityGroup(createRequest);
            // snippet-end:[ec2.java2.create_security_group.create]

            // snippet-start:[ec2.java2.create_security_group.config]
            IpRange ipRange = IpRange.builder()
                .cidrIp("0.0.0.0/0").build();

            IpPermission ipPerm = IpPermission.builder()
                .ipProtocol("tcp")
                .toPort(80)
                .fromPort(80)
                .ipRanges(ipRange)
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

            return resp.groupId();

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}

