//snippet-sourcedescription:[DeleteSecurityGroup.java demonstrates how to delete an Amazon Elastic Compute Cloud (Amazon EC2) Security Group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon EC2]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.ec2;

// snippet-start:[ec2.java2.delete_security_group.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.delete_security_group.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteSecurityGroup {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "   <groupId> \n\n" +
            "Where:\n" +
            "   groupId - A security group id that you can obtain from the AWS Console (for example, sg-xxxxxx1c0b65785c3).";

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

        deleteEC2SecGroup(ec2,groupId);
        ec2.close();
    }

    // snippet-start:[ec2.java2.delete_security_group.main]
    public static void deleteEC2SecGroup(Ec2Client ec2,String groupId) {

        try {
            DeleteSecurityGroupRequest request = DeleteSecurityGroupRequest.builder()
                .groupId(groupId)
                .build();

            ec2.deleteSecurityGroup(request);
            System.out.printf("Successfully deleted Security Group with id %s", groupId);

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
     }
    // snippet-end:[ec2.java2.delete_security_group.main]
}
