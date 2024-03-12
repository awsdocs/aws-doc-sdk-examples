// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.running_instances.main]
// snippet-start:[ec2.java2.running_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.running_instances.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FindRunningInstances {
    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
            .region(region)
            .build();

        findRunningEC2InstancesUsingPaginator(ec2);
        ec2.close();
    }

    public static void findRunningEC2InstancesUsingPaginator(Ec2Client ec2) {
        try {
            // Create a DescribeInstancesRequest to filter running instances.
            DescribeInstancesRequest describeInstancesRequest = DescribeInstancesRequest.builder()
                .filters(f -> f.name("instance-state-name").values("running"))
                .build();

            // Use the describeInstancesPaginator to paginate through the results.
            ec2.describeInstancesPaginator(describeInstancesRequest).stream()
                .flatMap(response -> response.reservations().stream())
                .flatMap(reservation -> reservation.instances().stream())
                .forEach(instance -> System.out.println("Instance ID: " + instance.instanceId() + ", State: " + instance.state().name()));

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[ec2.java2.running_instances.main]