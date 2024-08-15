// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.describe_vpc.main]
// snippet-start:[ec2.java2.describe_vpc.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;
import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.describe_vpc.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeVPCs {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                   <vpcId>

                Where:
                   vpcId - A  VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f).\s
                """;

        //if (args.length != 1) {
        //    System.out.println(usage);
        //    return;
       // }

        String vpcId = "vpc-e97a4393" ; //args[0];
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            CompletableFuture<Void> future = describeEC2VpcsAsync(ec2AsyncClient, vpcId);
            future.join();
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
    }

    /**
     * Describes EC2 VPCs asynchronously.
     *
     * @param ec2AsyncClient the EC2 async client to use for the operation
     * @param vpcId the ID of the VPC to describe
     * @return a {@link CompletableFuture} that completes when the VPC description is complete
     */
    public static CompletableFuture<Void> describeEC2VpcsAsync(Ec2AsyncClient ec2AsyncClient, String vpcId) {
        DescribeVpcsRequest request = DescribeVpcsRequest.builder()
            .vpcIds(vpcId)
            .build();

        CompletableFuture<DescribeVpcsResponse> response = ec2AsyncClient.describeVpcs(request);
        response.whenComplete((vpcsResponse, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to describe EC2 VPCs.", ex);
            } else if (vpcsResponse == null || vpcsResponse.vpcs().isEmpty()) {
                throw new RuntimeException("No EC2 VPCs found.");
            } else {
                // Process the response if no exception occurred and the result is not empty
                vpcsResponse.vpcs().forEach(vpc -> System.out.printf(
                    "Found VPC with id %s, " +
                        "vpc state %s, " +
                        "and tenancy %s%n",
                    vpc.vpcId(),
                    vpc.stateAsString(),
                    vpc.instanceTenancyAsString()
                ));
            }
        });

        // Return CompletableFuture<Void> to signify the async operation's completion
        return response.thenApply(resp -> null);
    }
}
// snippet-end:[ec2.java2.describe_vpc.main]
