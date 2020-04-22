//snippet-sourcedescription:[DescribeVPCs.java demonstrates how to get information about all the Amazon EC2 VPCs.]
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

// snippet-start:[ec2.java2.describe_vpc.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_vpc.import]

/**
 * Describes the VPCs
 */
public class DescribeVPCs {
    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a group ID.\n" +
                        "Ex: DescribeVPCs <vpc-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String vpcId = args[0];

        // Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeEC2Vpcs(ec2, vpcId);
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
                    "Found VPC with ID %s, " +
                            "VPC state %s " +
                            "and tenancy %s",
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
