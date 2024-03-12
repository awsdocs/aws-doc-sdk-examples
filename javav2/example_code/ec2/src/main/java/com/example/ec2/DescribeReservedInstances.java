// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.describe_reserved_instances.main]
// snippet-start:[ec2.java2.describe_reserved_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesResponse;
import software.amazon.awssdk.services.ec2.model.ReservedInstances;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_reserved_instances.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeReservedInstances {
    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeReservedEC2Instances(ec2);
        ec2.close();
    }

    public static void describeReservedEC2Instances(Ec2Client ec2) {
        try {
            DescribeReservedInstancesResponse response = ec2.describeReservedInstances();
            response.reservedInstances().forEach(instance -> {
                System.out.printf(
                    "Found a Reserved Instance with id %s, " +
                        "in AZ %s, " +
                        "type %s, " +
                        "state %s " +
                        "and monitoring state %s%n",
                    instance.reservedInstancesId(),
                    instance.availabilityZone(),
                    instance.instanceType(),
                    instance.state().name());
            });

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[ec2.java2.describe_reserved_instances.main]
