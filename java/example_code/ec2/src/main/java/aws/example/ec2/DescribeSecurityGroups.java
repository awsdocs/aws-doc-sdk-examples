//snippet-sourcedescription:[DescribeSecurityGroups.java demonstrates how to get a description of all security groups.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package aws.example.ec2;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;

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

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeSecurityGroupsRequest request =
            new DescribeSecurityGroupsRequest()
                .withGroupIds(group_id);

        DescribeSecurityGroupsResult response =
            ec2.describeSecurityGroups(request);

        for(SecurityGroup group : response.getSecurityGroups()) {
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
