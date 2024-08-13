// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.describe_addresses.main]
// snippet-start:[ec2.java2.describe_addresses.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.Address;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesResponse;
import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.describe_addresses.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeAddresses {
    public static void main(String[] args) {
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            CompletableFuture<DescribeAddressesResponse> future = describeEC2AddressAsync(ec2AsyncClient);
            future.join(); // Get the value.
            System.out.println("EC2 Addresses described successfully.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
    }

    /**
     * Asynchronously describes the Elastic Compute Cloud (EC2) addresses associated with the provided
     * {@code Ec2AsyncClient}.
     *
     * @param ec2AsyncClient the EC2 asynchronous client to use for the request
     * @return a {@link CompletableFuture} containing the {@link DescribeAddressesResponse} with the
     *     details of the described addresses
     */
    public static CompletableFuture<DescribeAddressesResponse> describeEC2AddressAsync(Ec2AsyncClient ec2AsyncClient) {
        CompletableFuture<DescribeAddressesResponse> response = ec2AsyncClient.describeAddresses();
        response.whenComplete((addressesResponse, ex) -> {
            if (addressesResponse != null) {
                for (Address address : addressesResponse.addresses()) {
                    System.out.printf(
                        "Found address with public IP %s, " +
                            "domain %s, " +
                            "allocation id %s " +
                            "and NIC id %s%n",
                        address.publicIp(),
                        address.domain(),
                        address.allocationId(),
                        address.networkInterfaceId());
                }
            } else {
                throw new RuntimeException("Failed to describe EC2 addresses.", ex);
            }
        });

        return response;
    }
}
// snippet-end:[ec2.java2.describe_addresses.main]
