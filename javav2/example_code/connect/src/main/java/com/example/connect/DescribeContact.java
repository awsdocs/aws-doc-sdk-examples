//snippet-sourcedescription:[DescribeContact.java demonstrates how to describe the specified contact.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.DescribeContactRequest;
import software.amazon.awssdk.services.connect.model.DescribeContactResponse;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeContact {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceId> <contactId>\n\n" +
            "Where:\n" +
            "   instanceId - The id of your instance.\n\n" +
            "   contactId - The id of the contact to describe.\n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        String contactId = args[1];
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        describeSpecificContact(connectClient, instanceId, contactId);
    }

    // snippet-start:[connect.java2.describe.contact.main]
    public static void describeSpecificContact(ConnectClient connectClient, String instanceId, String contactId) {
        try {
            DescribeContactRequest contactRequest = DescribeContactRequest.builder()
                .contactId(contactId)
                .instanceId(instanceId)
                .build();

            DescribeContactResponse response = connectClient.describeContact(contactRequest);
            System.out.println("The queue info is "+response.contact().queueInfo().toString());
            System.out.println("The queue id is "+response.contact().queueInfo().id());
            System.out.println("The initiation method is "+response.contact().initiationMethod().toString());

        } catch ( ConnectException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.describe.contact.main]
}
