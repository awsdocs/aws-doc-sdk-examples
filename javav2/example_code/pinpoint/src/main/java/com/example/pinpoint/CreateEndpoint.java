//snippet-sourcedescription:[CreateEndpoint.java demonstrates how to create an endpoint for an application in Pinpoint.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-service:[pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-06-01]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
//snippet-start:[pinpoint.java2.CreateEndpoint.complete]


package com.example.pinpoint;

//snippet-start:[pinpoint.java2.CreateEndpoint.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
//snippet-end:[pinpoint.java2.CreateEndpoint.import]

public class CreateEndpoint {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "CreateEndpoint - create an endpoint for an application in pinpoint\n\n" +
                "Usage: CreateEndpoint <appId>\n\n" +
                "Where:\n" +
                "  appId - the ID of the application to create an endpoint for.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[pinpoint.java2.CreateEndpoint.main]
        String appId = args[0];

        String endpointId = UUID.randomUUID().toString();
        System.out.println("Endpoint ID: " + endpointId);

        PinpointClient pinpoint = PinpointClient.builder().region(Region.US_EAST_1).build();

        EndpointResponse response = createEndpoint(pinpoint, appId);

        System.out.println(response.address());
        System.out.println(response.channelType());
        System.out.println(response.applicationId());
        System.out.println(response.endpointStatus());
        System.out.println(response.requestId());
        System.out.println(response.user());
        //snippet-end:[pinpoint.java2.CreateEndpoint.main]
    }

    //snippet-start:[pinpoint.java2.CreateEndpoint.helper]
    private static EndpointResponse createEndpoint(PinpointClient client, String appId) {
        String endpointId = UUID.randomUUID().toString();
        System.out.println("Endpoint ID: " + endpointId);

        EndpointRequest endpointRequest = createEndpointRequestData();


        UpdateEndpointRequest updateEndpointRequest = UpdateEndpointRequest.builder()
                .applicationId(appId)
                .endpointId(endpointId)
                .endpointRequest(endpointRequest)
                .build();

        UpdateEndpointResponse updateEndpointResponse = client.updateEndpoint(updateEndpointRequest);
        System.out.println("Update Endpoint Response: " + updateEndpointResponse.messageBody());

        GetEndpointRequest getEndpointRequest = GetEndpointRequest.builder()
                .applicationId(appId)
                .endpointId(endpointId)
                .build();
        GetEndpointResponse getEndpointResponse = client.getEndpoint(getEndpointRequest);

        System.out.println("Got Endpoint: " + getEndpointResponse.endpointResponse().id());
        return getEndpointResponse.endpointResponse();

    }

    private static EndpointRequest createEndpointRequestData() {
        HashMap<String, List<String>> customAttributes = new HashMap<>();
        List<String> favoriteTeams = new ArrayList<>();
        favoriteTeams.add("Lakers");
        favoriteTeams.add("Warriors");
        customAttributes.put("team", favoriteTeams);


        EndpointDemographic demographic = EndpointDemographic.builder()
                .appVersion("1.0")
                .make("apple")
                .model("iPhone")
                .modelVersion("7")
                .platform("ios")
                .platformVersion("10.1.1")
                .timezone("America/Los_Angeles")
                .build();

        EndpointLocation location = EndpointLocation.builder()
                .city("Los Angeles")
                .country("US")
                .latitude(34.0)
                .longitude(-118.2)
                .postalCode("90068")
                .region("CA")
                .build();

        Map<String,Double> metrics = new HashMap<>();
        metrics.put("health", 100.00);
        metrics.put("luck", 75.00);

        EndpointUser user = EndpointUser.builder()
                .userId(UUID.randomUUID().toString())
                .build();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        String nowAsISO = df.format(new Date());

        EndpointRequest endpointRequest = EndpointRequest.builder()
                .address(UUID.randomUUID().toString())
                .attributes(customAttributes)
                .channelType("APNS")
                .demographic(demographic)
                .effectiveDate(nowAsISO)
                .location(location)
                .metrics(metrics)
                .optOut("NONE")
                .requestId(UUID.randomUUID().toString())
                .user(user)
                .build();

        return endpointRequest;
    }
    //snippet-end:[pinpoint.java2.CreateEndpoint.helper]
}
//snippet-end:[pinpoint.java2.CreateEndpoint.complete]