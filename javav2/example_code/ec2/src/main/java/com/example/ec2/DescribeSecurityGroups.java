//snippet-sourcedescription:[DescribeSecurityGroups.java demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) Security Groups.]
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

// snippet-start:[ec2.java2.describe_security_groups.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_security_groups.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeSecurityGroups {

    public static void main(String[] args) {

        final String usage =
                "To run this example, supply a group id\n" +
                        "Ex: DescribeSecurityGroups <groupId>\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String groupId = args[0];
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
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
                    "Found Security Group with id %s, " +
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
    }
    // snippet-end:[ec2.java2.describe_security_groups.main]
}

