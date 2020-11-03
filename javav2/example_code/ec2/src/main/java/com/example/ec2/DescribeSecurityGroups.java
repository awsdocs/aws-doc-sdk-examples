//snippet-sourcedescription:[DescribeSecurityGroups.java demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) security groups.]
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

// snippet-start:[ec2.java2.describe_security_groups.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_security_groups.import]

/**
 * Describes all security groups
 */
public class DescribeSecurityGroups {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a group id\n" +
                        "Ex: DescribeSecurityGroups <group-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String groupId = args[0];

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeEC2SecurityGroups(ec2, groupId);
        ec2.close();

    }
        // snippet-start:[ec2.java2.describe_security_groups.main]
     public static void describeEC2SecurityGroups(Ec2Client ec2, String groupId) {

        try {

            DescribeSecurityGroupsRequest request =
                DescribeSecurityGroupsRequest.builder()
                        .groupIds(groupId).build();

            DescribeSecurityGroupsResponse response =
                ec2.describeSecurityGroups(request);

             for(SecurityGroup group : response.securityGroups()) {
                System.out.printf(
                    "Found security group with id %s, " +
                            "vpc id %s " +
                            "and description %s",
                    group.groupId(),
                    group.vpcId(),
                    group.description());
            }
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
         // snippet-end:[ec2.java2.describe_security_groups.main]
    }
}

