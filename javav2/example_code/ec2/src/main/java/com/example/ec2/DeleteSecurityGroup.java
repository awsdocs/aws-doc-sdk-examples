//snippet-sourcedescription:[DeleteSecurityGroup.java demonstrates how to delete an Amazon EC2 security group.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/11/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
        final String USAGE =
                "To run this example, supply a security group id\n" +
                        "Ex: DeleteSecurityGroup <security-group-id>\n";

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

        deleteEC2SecGroup(ec2,groupId);
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
            e.getStackTrace();
        }
        // snippet-end:[ec2.java2.delete_security_group.main]
    }
}
