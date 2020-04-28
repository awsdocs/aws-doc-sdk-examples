//snippet-sourcedescription:[ReleaseAddress.java demonstrates how to release an elastic IP address.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[ec2.java2.release_instance.complete]
// snippet-start:[ec2.java2.release_instance.import]

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.ReleaseAddressRequest;
import software.amazon.awssdk.services.ec2.model.ReleaseAddressResponse;

// snippet-end:[ec2.java2.release_instance.import]
/**
 * Releases an elastic IP address
 */
public class ReleaseAddress {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply an allocation ID.\n" +
                        "Ex: ReleaseAddress <allocation_id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String allocId = args[0];
        // snippet-start:[ec2.java2.release_instance.main]

        Ec2Client ec2 = Ec2Client.create();

        ReleaseAddressRequest request = ReleaseAddressRequest.builder()
                .allocationId(allocId).build();

        ReleaseAddressResponse response = ec2.releaseAddress(request);

        // snippet-end:[ec2.java2.release_instance.main]
        System.out.printf(
                "Successfully released elastic IP address %s", allocId);
    }
}

// snippet-end:[ec2.java2.release_instance.complete]
