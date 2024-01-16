// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;
import com.amazonaws.services.ec2.model.ReleaseAddressResult;

/**
 * Releases an elastic IP address
 */
public class ReleaseAddress {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply an allocation ID.\n" +
                "Ex: ReleaseAddress <allocation_id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alloc_id = args[0];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        ReleaseAddressRequest request = new ReleaseAddressRequest()
                .withAllocationId(alloc_id);

        ReleaseAddressResult response = ec2.releaseAddress(request);

        System.out.printf(
                "Successfully released elastic IP address %s", alloc_id);
    }
}
