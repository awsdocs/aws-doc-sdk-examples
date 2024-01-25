//snippet-sourcedescription:[IoTThingReader.java demonstrates how to get the shadow for the specified thing.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:AWS IoT]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.iot;

// snippet-start:[iot.java2.get.shadow.writer.main]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.model.IotException;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowResponse;
import java.net.URI;

public class IoTThingReader {

    public static void main(String[] args) {
        final String usage = """

            Usage:
               <thingName> <endpoint>

            Where:
               thingName - The name of the AWS IoT Thing.\s
               endpoint - The endpoint used to create the IotDataPlaneClient (ie - https://xxxxxxsoth3da-ats.iot.us-east-1.amazonaws.com ).\s
            """;

        //if (args.length != 2) {
        //    System.out.println(usage);
        //    System.exit(1);
        // }

        String thingName = "foo106";
        String endpoint = "https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com";  // You can find this in the AWS IoT console

        IotDataPlaneClient iotPlaneClient = IotDataPlaneClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(endpoint))
            .build();

        getThingShadow(iotPlaneClient, thingName);
    }

    public static void getThingShadow( IotDataPlaneClient iotPlaneClient,String thingName) {
        try {
            GetThingShadowRequest getThingShadowRequest = GetThingShadowRequest.builder()
                .thingName(thingName)
                .build();

            GetThingShadowResponse getThingShadowResponse = iotPlaneClient.getThingShadow(getThingShadowRequest);

            // Extracting payload from response.
            SdkBytes payload = getThingShadowResponse.payload();
            String payloadString = payload.asUtf8String();
            System.out.println("Received Shadow Data: " + payloadString);

        } catch (IotException e) {
            System.err.println("Error reading from IoT Thing: " + e.getMessage());
        }
    }
}
// snippet-end:[iot.java2.get.shadow.writer.main]