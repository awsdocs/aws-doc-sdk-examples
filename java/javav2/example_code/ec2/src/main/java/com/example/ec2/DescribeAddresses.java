//snippet-sourcedescription:[DescribeAddresses.java demonstrates how to get information about elastic IP addresses.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[ec2.java2.describe_addresses.complete]
// snippet-start:[ec2.java2.describe_addresses.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Address;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesResponse;
 
// snippet-end:[ec2.java2.describe_addresses.import]
/**
 * Describes all elastic IP addresses
 */
public class DescribeAddresses
{
    public static void main(String[] args)
    {
        // snippet-start:[ec2.java2.describe_addresses.main]
        Ec2Client ec2 = Ec2Client.create();

        DescribeAddressesResponse response = ec2.describeAddresses();

        for(Address address : response.addresses()) {
            System.out.printf(
                    "Found address with public IP %s, " +
                    "domain %s, " +
                    "allocation id %s " +
                    "and NIC id %s",
                    address.publicIp(),
                    address.domain(),
                    address.allocationId(),
                    address.networkInterfaceId());
        }
        // snippet-end:[ec2.java2.describe_addresses.main]
    }
}
 
// snippet-end:[ec2.java2.describe_addresses.complete]
