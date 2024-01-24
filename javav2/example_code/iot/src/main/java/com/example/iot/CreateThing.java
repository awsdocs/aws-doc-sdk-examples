//snippet-sourcedescription:[CreateThing.java demonstrates how to create an AWS IoT Thing.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:AWS IoT]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.iot;

// snippet-start:[iot.java2.create.thing.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.awssdk.services.iot.model.IotException;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */
public class CreateThing {

    public static void main(String[] args) {
        System.out.println("Creating a new AWS IoT");
        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Specify the thing name
        String thingName = "foo100";
        createThing(iotClient, thingName);
        iotClient.close();
    }

    public static void createThing(IotClient iotClient, String thingName) {
        try {
            CreateThingRequest createThingRequest = CreateThingRequest.builder()
                .thingName(thingName)
                .build();

            // Create Thing Response.
            CreateThingResponse createThingResponse = iotClient.createThing(createThingRequest);
            System.out.println("Created Thing ARN: " + createThingResponse.thingArn());

        } catch (IotException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[iot.java2.create.thing.main]
