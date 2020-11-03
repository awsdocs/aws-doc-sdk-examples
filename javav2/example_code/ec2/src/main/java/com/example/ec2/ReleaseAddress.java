//snippet-sourcedescription:[ReleaseAddress.java demonstrates how to release an elastic IP address.]
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

// snippet-start:[ec2.java2.release_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.ReleaseAddressRequest;
import software.amazon.awssdk.services.ec2.model.ReleaseAddressResponse;
// snippet-end:[ec2.java2.release_instance.import]

public class ReleaseAddress {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "ReleaseAddress <allocId>\n\n" +
                "Where:\n" +
                "    allocId - an allocation ID value that you can obtain from the AWS Console. \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

       String allocId = args[0];
       Region region = Region.US_WEST_2;
       Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        releaseEC2Address(ec2, allocId);
        ec2.close();
    }

    // snippet-start:[ec2.java2.release_instance.main]
    public static void releaseEC2Address(Ec2Client ec2,String allocId) {

        try {
            ReleaseAddressRequest request = ReleaseAddressRequest.builder()
                .allocationId(allocId).build();

            ReleaseAddressResponse response = ec2.releaseAddress(request);

         System.out.printf(
                "Successfully released elastic IP address %s", allocId);
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
     }
    // snippet-end:[ec2.java2.release_instance.main]
}
