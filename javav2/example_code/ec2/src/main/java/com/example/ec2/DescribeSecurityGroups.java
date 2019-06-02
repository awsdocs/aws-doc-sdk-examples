//snippet-sourcedescription:[DescribeSecurityGroups.java demonstrates how to get information about all the EC2 security groups.]
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
// snippet-start:[ec2.java2.describe_security_groups.complete]
// snippet-start:[ec2.java2.describe_security_groups.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
 
// snippet-end:[ec2.java2.describe_security_groups.import]
/**
 * Describes all security groups
 */
public class DescribeSecurityGroups
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply a group id\n" +
            "Ex: DescribeSecurityGroups <group-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String group_id = args[0];
        // snippet-start:[ec2.java2.describe_security_groups.main]

        Ec2Client ec2 = Ec2Client.create();

        DescribeSecurityGroupsRequest request =
            DescribeSecurityGroupsRequest.builder()
                .groupIds(group_id).build();

        DescribeSecurityGroupsResponse response =
            ec2.describeSecurityGroups(request);

        // snippet-end:[ec2.java2.describe_security_groups.main]
        for(SecurityGroup group : response.securityGroups()) {
            System.out.printf(
                "Found security group with id %s, " +
                "vpc id %s " +
                "and description %s",
                group.groupId(),
                group.vpcId(),
                group.description());
        }
    }
}
 
// snippet-end:[ec2.java2.describe_security_groups.complete]
