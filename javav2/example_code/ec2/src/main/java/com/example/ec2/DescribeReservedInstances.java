//snippet-sourcedescription:[DescribeReservedInstances.java demonstrates how to get information about Amazon Elastic Compute Cloud (Amazon EC2) Reserved Instances.]
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

// snippet-start:[ec2.java2.describe_reserved_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesResponse;
import software.amazon.awssdk.services.ec2.model.ReservedInstances;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_reserved_instances.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeReservedInstances {

    public static void main(String[] args) {

           final String USAGE = "\n" +
                "Usage:\n" +
                "DescribeReservedInstances <instanceId>\n\n" +
                "Where:\n" +
                "    instanceId - an instance id value that you can obtain from the AWS Console. \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeReservedEC2Instances(ec2, instanceId);
        ec2.close();
    }
    // snippet-start:[ec2.java2.describe_reserved_instances.main]
    public static void describeReservedEC2Instances( Ec2Client ec2, String instanceID) {
        try {
            DescribeReservedInstancesRequest request = DescribeReservedInstancesRequest.builder().reservedInstancesIds(instanceID).build();

            DescribeReservedInstancesResponse response =
                ec2.describeReservedInstances(request);

            for (ReservedInstances instance : response.reservedInstances()) {
                System.out.printf(
                    "Found a Reserved Instance with id %s, " +
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
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
    }
      // snippet-end:[ec2.java2.describe_reserved_instances.main]
  }
}
