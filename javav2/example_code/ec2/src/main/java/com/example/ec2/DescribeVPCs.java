//snippet-sourcedescription:[DescribeVPCs.java demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) VPCs.]
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

// snippet-start:[ec2.java2.describe_vpc.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_vpc.import]

/**
 * Describes VPCs
 */
public class DescribeVPCs {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "DescribeVPCs <vpcId>\n\n" +
                "Where:\n" +
                "    vpcId - a VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f). \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line argument
        String vpcId = args[0];

        // Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeEC2Vpcs(ec2, vpcId);
        ec2.close();
    }

    // snippet-start:[ec2.java2.describe_vpc.main]
    public static void describeEC2Vpcs(Ec2Client ec2, String vpcId) {

        try {
            DescribeVpcsRequest request = DescribeVpcsRequest.builder()
                .vpcIds(vpcId)
                .build();

            DescribeVpcsResponse response =
                ec2.describeVpcs(request);

            for (Vpc vpc : response.vpcs()) {
                System.out.printf(
                    "Found vpc with id %s, " +
                            "vpc state %s " +
                            "and tennancy %s",
                    vpc.vpcId(),
                    vpc.stateAsString(),
                    vpc.instanceTenancyAsString());
                }
            } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[ec2.java2.describe_vpc.main]
    }
}

