/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AllocateAddressRequest;
import com.amazonaws.services.ec2.model.AllocateAddressResult;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.AssociateAddressResult;
import com.amazonaws.services.ec2.model.DomainType;

/**
 * Allocates an elastic IP address for an EC2 instance
 */
public class AllocateAddress {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an instance id\n" +
            "Ex: AllocateAddress <instance-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        AllocateAddressRequest allocateAddressRequest = new AllocateAddressRequest()
            .withDomain(DomainType.Vpc);

        AllocateAddressResult allocateAddressResponsee = ec2.allocateAddress(allocateAddressRequest);

        String allocationId = allocateAddressResponsee.getAllocationId();

        AssociateAddressRequest request = new AssociateAddressRequest()
            .withInstanceId(instanceId)
            .withAllocationId(allocationId);

        AssociateAddressResult response = ec2.associateAddress(request);

        System.out.printf("Successfully associated elastic ip address %s with instance %s", response.getAssociationId(), instanceId);
    }
}
