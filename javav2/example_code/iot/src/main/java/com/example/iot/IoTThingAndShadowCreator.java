package com.example.iot;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.awssdk.services.iot.model.IotException;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class IoTThingAndShadowCreator {

    public static void main(String[] args) {
        System.out.println("Creating a new AWS IoT Thing and Shadow");

        // Create an IoT client for creating a Thing
        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Specify the thing name
        String thingName = "foo4";

        try {
            // Create Thing Request
            CreateThingRequest createThingRequest = CreateThingRequest.builder()
                .thingName(thingName)
                .build();

            // Create Thing Response
            CreateThingResponse createThingResponse = iotClient.createThing(createThingRequest);

            // Print ARN of the created thing
            System.out.println("Created Thing ARN: " + createThingResponse.thingArn());

            // Create an IoT Data Plane client for updating the Thing Shadow
            IotDataPlaneClient iotDataPlaneClient = IotDataPlaneClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com"))
                .build();

            // Create Thing Shadow State Document
            String stateDocument = "{\"state\":{\"desired\":{\"temperature\":25, \"humidity\":50}}}";

            // Update Thing Shadow Request
            UpdateThingShadowRequest updateThingShadowRequest = UpdateThingShadowRequest.builder()
                .thingName(thingName)
                .payload(SdkBytes.fromString(stateDocument, StandardCharsets.UTF_8))
                .build();

            // Update Thing Shadow
            UpdateThingShadowResponse updateThingShadowResponse = iotDataPlaneClient.updateThingShadow(updateThingShadowRequest);

            // Print the result of updating the Thing Shadow
            System.out.println("Updated Thing Shadow: " + updateThingShadowResponse.payload().asUtf8String());

        } catch (IotException e) {
            System.err.println("Error creating Thing and Shadow: " + e.getMessage());
        } finally {
            // Close the IoT clients
            iotClient.close();
        }
    }
}
