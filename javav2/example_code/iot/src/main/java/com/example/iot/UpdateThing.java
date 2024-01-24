//snippet-sourcedescription:[SendEmailMessageCC.java demonstrates how to send an email message which includes CC values.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:AWS IoT]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.iot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.AttributePayload;
import software.amazon.awssdk.services.iot.model.IotException;
import software.amazon.awssdk.services.iot.model.UpdateThingRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UpdateThing {

    public static void main(String[] args) {

        /**
         * Attributes are key-value pairs that can be searchable or non-searchable.
         * Searchable attributes can be used to filter lists of things without using fleet indexing.
         * Non-searchable attributes can be used to find things, but only when fleet indexing is turned on.
         */

        System.out.println("Updating an AWS IoT Thing");
        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Specify the name of the IoT Thing to update.
        String thingName = "foo";

        // Specify the new attribute values
        String newLocation = "Office";
        String newFirmwareVersion = "v2.0";

        Map<String, String> attMap = new HashMap<>();
        attMap.put("location", newLocation);
        attMap.put("firmwareVersion", newFirmwareVersion);


        // Build the update request
        AttributePayload attributePayload = AttributePayload.builder()
            .attributes(attMap)
            .build();

        UpdateThingRequest updateThingRequest = UpdateThingRequest.builder()
            .thingName(thingName)
            .attributePayload(attributePayload)
            .build();

        try {
            // Update the IoT Thing attributes
            iotClient.updateThing(updateThingRequest);
            System.out.println("Thing attributes updated successfully.");

        } catch (IotException e) {
            System.err.println("Error updating Thing attributes: " + e.getMessage());
        } finally {
            // Close the IoT client
            iotClient.close();
        }
    }
}