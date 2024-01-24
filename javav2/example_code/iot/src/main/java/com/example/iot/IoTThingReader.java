package com.example.iot;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowResponse;

import java.net.URI;

public class IoTThingReader {

    public static void main(String[] args) {
        String thingName = "foo106";
        String endpoint = "https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com";  // You can find this in the AWS IoT console

        IotDataPlaneClient iotPlaneClient = IotDataPlaneClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(endpoint))
            .build();

        try {
            GetThingShadowRequest getThingShadowRequest = GetThingShadowRequest.builder()
                .thingName(thingName)
                .build();

            GetThingShadowResponse getThingShadowResponse = iotPlaneClient.getThingShadow(getThingShadowRequest);

            // Extracting payload from response
            SdkBytes payload = getThingShadowResponse.payload();
            String payloadString = payload.asUtf8String();

            System.out.println("Received Shadow Data: " + payloadString);

        } catch (IotException e) {
            System.err.println("Error reading from IoT Thing: " + e.getMessage());
        }
    }
}
