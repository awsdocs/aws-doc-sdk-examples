//snippet-sourcedescription:[DeleteSecurityGroup.java demonstrates how to delete an Amazon Elastic Compute Cloud (Amazon EC2) security group.]
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

// snippet-start:[ec2.java2.delete_security_group.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.delete_security_group.import]
/**
 * Deletes an EC2 security group
 */
public class DeleteSecurityGroup {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "DeleteSecurityGroup <groupId> \n\n" +
                "Where:\n" +
                "    groupId - a security group id that you can obtain from the AWS Console (for example, sg-xxxxxx1c0b65785c3)."  ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line argument
        String groupId = args[0];

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
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

            DeleteSecurityGroupResponse response = ec2.deleteSecurityGroup(request);

            System.out.printf(
                "Successfully deleted security group with id %s", groupId);

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[ec2.java2.delete_security_group.main]
    }
}
