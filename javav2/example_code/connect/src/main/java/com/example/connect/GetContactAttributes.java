//snippet-sourcedescription:[GetContactAttributes.java demonstrates how to describe the specified contact attributes.]
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
import software.amazon.awssdk.services.connect.model.GetContactAttributesRequest;
import software.amazon.awssdk.services.connect.model.GetContactAttributesResponse;
import java.util.Map;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetContactAttributes {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceId>\n\n" +
            "Where:\n" +
            "   instanceId - The identifier of the Amazon Connect instance.\n\n" +
            "   contactId - The identifier of the contact.\n\n" ;

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

        getContactAttrs(connectClient, instanceId, contactId);
    }

    // snippet-start:[connect.java2.contact.attr.main]
    public static void getContactAttrs( ConnectClient connectClient, String instanceId, String contactId ) {
        try {
            GetContactAttributesRequest attributesRequest = GetContactAttributesRequest.builder()
               .instanceId(instanceId)
                .initialContactId(contactId)
                .build();

            GetContactAttributesResponse response = connectClient.getContactAttributes(attributesRequest);
            Map<String, String> attributeMap = response.attributes();
            for (Map.Entry<String,String> entry : attributeMap.entrySet())
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.contact.attr.main]
}

