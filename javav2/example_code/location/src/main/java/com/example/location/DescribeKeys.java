package com.example.location;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.location.LocationClient;
import software.amazon.awssdk.services.location.model.DescribeKeyRequest;
import software.amazon.awssdk.services.location.model.DescribeKeyResponse;

public class DescribeKeys {

    public static void main(String[] args) {
        LocationClient locationClient = LocationClient.builder()
            .region(Region.US_EAST_1)
            .build();

        DescribeKeyRequest keyRequest = DescribeKeyRequest.builder()
            .keyName("ExampleKey")
            .build();

        DescribeKeyResponse response = locationClient.describeKey(keyRequest);
        System.out.println("The key ARN  is "+response.keyArn());
    }
}
