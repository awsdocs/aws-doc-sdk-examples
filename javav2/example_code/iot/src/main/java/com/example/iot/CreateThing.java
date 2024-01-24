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
import software.amazon.awssdk.services.iot.model.*;
public class CreateThing {

    public static void main(String[] args) {
        System.out.println("Creating a new AWS IoT");
        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Specify the thing name
        String thingName = "foo100";

        // Create Thing Request
        CreateThingRequest createThingRequest = CreateThingRequest.builder()
            .thingName(thingName)
            .build();

        // Create Thing Response
        CreateThingResponse createThingResponse = iotClient.createThing(createThingRequest);

        // Print ARN of the created thing
        System.out.println("Created Thing ARN: " + createThingResponse.thingArn());

        // Close the IoT client
        iotClient.close();
    }

}
