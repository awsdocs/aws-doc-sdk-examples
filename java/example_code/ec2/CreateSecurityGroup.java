/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;

/**
 * Creates an EC2 security group.
 */
public class CreateSecurityGroup {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a group name, group description and vpc id\n" +
            "Ex: CreateSecurityGroup <group-name> <group-description> <vpc-id>\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String groupName = args[0];
        String groupDescription = args[1];
        String vpcId = args[2];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest()
            .withGroupName(groupName)
            .withDescription(groupDescription)
            .withVpcId(vpcId);

        CreateSecurityGroupResult createSecurityGroupResponse = ec2.createSecurityGroup(createSecurityGroupRequest);

        System.out.printf("Successfully created security group named %s", groupName);

        IpRange ipRange = new IpRange()
            .withCidrIp("0.0.0.0/0");

        IpPermission ipPermission = new IpPermission()
            .withIpProtocol("tcp")
            .withToPort(80)
            .withFromPort(80)
            .withIpv4Ranges(ipRange);

        IpPermission ipPermission2 = new IpPermission()
            .withIpProtocol("tcp")
            .withToPort(22)
            .withFromPort(22)
            .withIpv4Ranges(ipRange);

        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest()
            .withGroupName(groupName)
            .withIpPermissions(ipPermission, ipPermission2);

        AuthorizeSecurityGroupIngressResult authorizeSecurityGroupIngressResponse =
            ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

        System.out.printf("Successfully added ingress policy to security group %s", groupName);
    }
}
