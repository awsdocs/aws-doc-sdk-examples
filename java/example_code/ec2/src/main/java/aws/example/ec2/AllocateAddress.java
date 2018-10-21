 
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
package aws.example.ec2;
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

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        AllocateAddressRequest allocate_request = new AllocateAddressRequest()
            .withDomain(DomainType.Vpc);

        AllocateAddressResult allocate_response =
            ec2.allocateAddress(allocate_request);

        String allocation_id = allocate_response.getAllocationId();

        AssociateAddressRequest associate_request =
            new AssociateAddressRequest()
                .withInstanceId(instance_id)
                .withAllocationId(allocation_id);

        AssociateAddressResult associate_response =
            ec2.associateAddress(associate_request);

        System.out.printf(
            "Successfully associated Elastic IP address %s " +
            "with instance %s",
            associate_response.getAssociationId(),
            instance_id);
    }
}

