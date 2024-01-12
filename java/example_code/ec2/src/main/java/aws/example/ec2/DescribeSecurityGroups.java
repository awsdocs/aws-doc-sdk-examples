// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;

/**
 * Describes all security groups
 */
public class DescribeSecurityGroups {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a group id\n" +
                "Ex: DescribeSecurityGroups <group-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String group_id = args[0];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest()
                .withGroupIds(group_id);

        DescribeSecurityGroupsResult response = ec2.describeSecurityGroups(request);

        for (SecurityGroup group : response.getSecurityGroups()) {
            System.out.printf(
                    "Found security group with id %s, " +
                            "vpc id %s " +
                            "and description %s",
                    group.getGroupId(),
                    group.getVpcId(),
                    group.getDescription());
        }
    }
}
