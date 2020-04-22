//snippet-sourcedescription:[AllocateAddress.java demonstrates how to allocate an elastic IP address for an Amazon EC2 instance.]
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
 * Allocates an elastic IP address for an EC2 instance
 */
public class AllocateAddress {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply an instance id that you can obtain from the AWS Console\n" +
                        "Ex: AllocateAddress <instance_id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];

        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        System.out.println(getAllocateAddress(ec2, instanceId));
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
           e.getStackTrace();
        }
       return "";
        // snippet-end:[ec2.java2.allocate_address.main]
    }
}
