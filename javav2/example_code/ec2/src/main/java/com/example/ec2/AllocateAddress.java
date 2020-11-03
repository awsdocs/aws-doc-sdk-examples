//snippet-sourcedescription:[AllocateAddress.java demonstrates how to allocate an elastic IP address for an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
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

// snippet-start:[ec2.java2.allocate_address.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AllocateAddressRequest;
import software.amazon.awssdk.services.ec2.model.DomainType;
import software.amazon.awssdk.services.ec2.model.AllocateAddressResponse;
import software.amazon.awssdk.services.ec2.model.AssociateAddressRequest;
import software.amazon.awssdk.services.ec2.model.AssociateAddressResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.allocate_address.import]

/**
 * Allocates an elastic IP address for an EC2 instance.
 */
public class AllocateAddress {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "AllocateAddress <instanceId>\n\n" +
                "Where:\n" +
                "    instanceId - an instance id value that you can obtain from the AWS Console. \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line argument
        String instanceId = args[0];

        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        System.out.println(getAllocateAddress(ec2, instanceId));
        ec2.close();
    }

    // snippet-start:[ec2.java2.allocate_address.main]
    public static String getAllocateAddress( Ec2Client ec2, String instanceId) {

       try {
           AllocateAddressRequest allocateRequest = AllocateAddressRequest.builder()
                .domain(DomainType.VPC)
                .build();

           AllocateAddressResponse allocateResponse =
                ec2.allocateAddress(allocateRequest);

           String allocationId = allocateResponse.allocationId();

           AssociateAddressRequest associateRequest =
                AssociateAddressRequest.builder()
                        .instanceId(instanceId)
                        .allocationId(allocationId)
                        .build();

            AssociateAddressResponse associateResponse = ec2.associateAddress(associateRequest);
            return associateResponse.associationId();

         } catch (Ec2Exception e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
        }
       return "";
        // snippet-end:[ec2.java2.allocate_address.main]
    }
}
