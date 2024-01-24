package com.example.iot;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class IoTThingShadowWriter {

    public static void main(String[] args) {
        String thingName = "foo100";
        String endpoint = "https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com"; // Use https

        IotDataPlaneClient iotPlaneClient = IotDataPlaneClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(endpoint))
            .build();

        try {
            // Create Thing Shadow State Document
            String stateDocument = "{\"state\":{\"reported\":{\"temperature\":25, \"humidity\":50}}}";
            SdkBytes data= SdkBytes.fromString(stateDocument, StandardCharsets.UTF_8 );

            // Update Thing Shadow Request
            UpdateThingShadowRequest updateThingShadowRequest = UpdateThingShadowRequest.builder()
                .thingName(thingName)
                .payload(data)
                .build();

            // Update Thing Shadow
            iotPlaneClient.updateThingShadow(updateThingShadowRequest);

            System.out.println("Thing Shadow updated successfully.");

        } catch (Exception e) {
            System.err.println("Error updating Thing Shadow: " + e.getMessage());
        }
    }
}
