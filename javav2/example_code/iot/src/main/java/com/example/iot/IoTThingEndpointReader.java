package com.example.iot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.DescribeEndpointRequest;
import software.amazon.awssdk.services.iot.model.DescribeEndpointResponse;
import software.amazon.awssdk.services.iot.model.IotException;

public class IoTThingEndpointReader {

    public static void main(String[] args) {
        String thingName = "foo2";

        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            DescribeEndpointRequest describeEndpointRequest = DescribeEndpointRequest.builder()
                .endpointType("iot:Data-ATS")
                .build();

            DescribeEndpointResponse describeEndpointResponse = iotClient.describeEndpoint(describeEndpointRequest);

            String endpoint = describeEndpointResponse.endpointAddress();
            System.out.println("Endpoint for Thing " + thingName + ": " + endpoint);

        } catch (IotException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
