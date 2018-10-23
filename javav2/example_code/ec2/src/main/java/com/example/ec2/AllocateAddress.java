 
//snippet-sourcedescription:[AllocateAddress.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AllocateAddressRequest;
import software.amazon.awssdk.services.ec2.model.AllocateAddressResponse;
import software.amazon.awssdk.services.ec2.model.AssociateAddressRequest;
import software.amazon.awssdk.services.ec2.model.AssociateAddressResponse;
import software.amazon.awssdk.services.ec2.model.DomainType;

/**
 * Allocates an elastic IP address for an EC2 instance
 */
public class AllocateAddress
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply an instance id\n" +
            "Ex: AllocateAddress <instance_id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instance_id = args[0];

        Ec2Client ec2 = Ec2Client.create();

        AllocateAddressRequest allocate_request = AllocateAddressRequest.builder()
            .domain(DomainType.VPC)
            .build();

        AllocateAddressResponse allocate_response =
            ec2.allocateAddress(allocate_request);

        String allocation_id = allocate_response.allocationId();

        AssociateAddressRequest associate_request =
            AssociateAddressRequest.builder()
                .instanceId(instance_id)
                .allocationId(allocation_id)
                .build();

        AssociateAddressResponse associate_response =
            ec2.associateAddress(associate_request);

        System.out.printf(
            "Successfully associated Elastic IP address %s " +
            "with instance %s",
            associate_response.associationId(),
            instance_id);
    }
}

