//snippet-sourcedescription:[DescribeReservedInstances.java demonstrates how to get information about Amazon EC2 Reserved Instances.]
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

// snippet-start:[ec2.java2.describe_reserved_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesResponse;
import software.amazon.awssdk.services.ec2.model.ReservedInstances;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_reserved_instances.import]

/**
 * Describes reserved instances
 */
public class DescribeReservedInstances {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a group id\n" +
                        "Ex: DescribeReservedInstances <vpc-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceID = args[0];

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeReservedEC2Instances(ec2, instanceID);
    }
    // snippet-start:[ec2.java2.describe_reserved_instances.main]
    public static void describeReservedEC2Instances( Ec2Client ec2, String instanceID) {
        try {
            DescribeReservedInstancesRequest request = DescribeReservedInstancesRequest.builder().reservedInstancesIds(instanceID).build();

            DescribeReservedInstancesResponse response =
                ec2.describeReservedInstances(request);

            for (ReservedInstances instance : response.reservedInstances()) {
                System.out.printf(
                    "Found a reserved instance with id %s, " +
                            "in AZ %s, " +
                            "type %s, " +
                            "state %s " +
                            "and monitoring state %s",
                    instance.reservedInstancesId(),
                    instance.availabilityZone(),
                    instance.instanceType(),
                    instance.state().name());
        }

        } catch (Ec2Exception e) {
            e.getStackTrace();
    }
      // snippet-end:[ec2.java2.describe_reserved_instances.main]
  }
}
