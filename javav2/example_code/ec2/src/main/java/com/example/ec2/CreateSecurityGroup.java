//snippet-sourcedescription:[CreateSecurityGroup.java demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) Security Group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ec2;

// snippet-start:[ec2.java2.create_security_group.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateSecurityGroup {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "   <groupName> <groupDesc> <vpcId> \n\n" +
                "Where:\n" +
                "   groupName - A group name (for example, TestKeyPair). \n\n" +
                "   groupDesc - A group description  (for example, TestKeyPair). \n\n"+
                "   vpcId - A VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f). \n\n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String groupName = args[0];
        String groupDesc = args[1];
        String vpcId = args[2];

        // snippet-start:[ec2.java2.create_security_group.client]
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        // snippet-end:[ec2.java2.create_security_group.client]

        String id = createEC2SecurityGroup(ec2, groupName, groupDesc, vpcId);
        System.out.printf(
                "Successfully created Security Group with this ID %s",
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

            System.out.printf(
                "Successfully added ingress policy to Security Group %s",
                groupName);

            return resp.groupId();

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[ec2.java2.create_security_group.config]
    // snippet-end:[ec2.java2.create_security_group.main]
}

