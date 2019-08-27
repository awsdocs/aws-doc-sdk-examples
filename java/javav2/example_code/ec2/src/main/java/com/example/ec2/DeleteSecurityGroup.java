    //snippet-sourcedescription:[DeleteSecurityGroup.java demonstrates how to delete an EC2 security group.]
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
// snippet-start:[ec2.java2.delete_security_group.complete]
// snippet-start:[ec2.java2.delete_security_group.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupResponse;
 
// snippet-end:[ec2.java2.delete_security_group.import]
/**
 * Deletes an EC2 security group
 */
public class DeleteSecurityGroup
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply a security group id\n" +
            "Ex: DeleteSecurityGroup <security-group-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String group_id = args[0];
        // snippet-start:[ec2.java2.delete_security_group.main]

        Ec2Client ec2 = Ec2Client.create();

        DeleteSecurityGroupRequest request = DeleteSecurityGroupRequest.builder()
            .groupId(group_id)
            .build();

        DeleteSecurityGroupResponse response = ec2.deleteSecurityGroup(request);

        // snippet-end:[ec2.java2.delete_security_group.main]
        System.out.printf(
            "Successfully deleted security group with id %s", group_id);
    }
}
 
// snippet-end:[ec2.java2.delete_security_group.complete]
