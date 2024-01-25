//snippet-sourcedescription:[IoTThingShadowWriter.java demonstrates how to updates the shadow for the specified thing.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:AWS IoT]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.iot;

// snippet-start:[iot.java2.shadow.writer.main]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.model.IotException;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class IoTThingShadowWriter {
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

        String thingName = "foo200"; // args[0] ;
        String endpoint = "https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com"; // args[1] ;
        IotDataPlaneClient iotPlaneClient = IotDataPlaneClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(endpoint))
            .build();

        updateThingShadow(iotPlaneClient, thingName);
    }

    public static void updateThingShadow(IotDataPlaneClient iotPlaneClient, String thingName) {
        try {
            // Create Thing Shadow State Document.
            String stateDocument = "{\"state\":{\"reported\":{\"temperature\":25, \"humidity\":50}}}";
            SdkBytes data= SdkBytes.fromString(stateDocument, StandardCharsets.UTF_8 );

            // Update Thing Shadow Request.
            UpdateThingShadowRequest updateThingShadowRequest = UpdateThingShadowRequest.builder()
                .thingName(thingName)
                .payload(data)
                .build();

            // Update Thing Shadow.
            iotPlaneClient.updateThingShadow(updateThingShadowRequest);
            System.out.println(thingName +" updated successfully.");

        } catch (IotException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[iot.java2.shadow.writer.main]