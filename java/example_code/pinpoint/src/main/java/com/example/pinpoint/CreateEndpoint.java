 
//snippet-sourcedescription:[CreateEndpoint.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mobiletargeting]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.example.pinpoint;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.UpdateEndpointRequest;
import com.amazonaws.services.pinpoint.model.UpdateEndpointResult;
import com.amazonaws.services.pinpoint.model.EndpointDemographic;
import com.amazonaws.services.pinpoint.model.EndpointLocation;
import com.amazonaws.services.pinpoint.model.EndpointRequest;
import com.amazonaws.services.pinpoint.model.EndpointResponse;
import com.amazonaws.services.pinpoint.model.EndpointUser;
import com.amazonaws.services.pinpoint.model.GetEndpointRequest;
import com.amazonaws.services.pinpoint.model.GetEndpointResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        String appId = args[0];

		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		
		EndpointResponse response = createEndpoint(pinpoint, appId);
		
		System.out.println(response.getAddress());
		System.out.println(response.getChannelType());
		System.out.println(response.getApplicationId());
		System.out.println(response.getEndpointStatus());
		System.out.println(response.getRequestId());
		System.out.println(response.getUser());
	}
	
    public static EndpointResponse createEndpoint(AmazonPinpoint client, String appId) {
        String endpointId = UUID.randomUUID().toString();
        System.out.println("Endpoint ID: " + endpointId);

        EndpointRequest endpointRequest = createEndpointRequestData();

        UpdateEndpointRequest updateEndpointRequest = new UpdateEndpointRequest()
                .withApplicationId(appId)
                .withEndpointId(endpointId)
                .withEndpointRequest(endpointRequest);

        UpdateEndpointResult updateEndpointResponse = client.updateEndpoint(updateEndpointRequest);
        System.out.println("Update Endpoint Response: " + updateEndpointResponse.getMessageBody());

        GetEndpointRequest getEndpointRequest = new GetEndpointRequest()
                .withApplicationId(appId)
                .withEndpointId(endpointId);
        GetEndpointResult getEndpointResult = client.getEndpoint(getEndpointRequest);

        System.out.println("Got Endpoint: " + getEndpointResult.getEndpointResponse().getId());
        return getEndpointResult.getEndpointResponse();
    }

    private static EndpointRequest createEndpointRequestData() {

        HashMap<String, List<String>> customAttributes = new HashMap<>();
        List<String> favoriteTeams = new ArrayList<>();
        favoriteTeams.add("Lakers");
        favoriteTeams.add("Warriors");
        customAttributes.put("team", favoriteTeams);


        EndpointDemographic demographic = new EndpointDemographic()
                .withAppVersion("1.0")
                .withMake("apple")
                .withModel("iPhone")
                .withModelVersion("7")
                .withPlatform("ios")
                .withPlatformVersion("10.1.1")
                .withTimezone("America/Los_Angeles");

        EndpointLocation location = new EndpointLocation()
                .withCity("Los Angeles")
                .withCountry("US")
                .withLatitude(34.0)
                .withLongitude(-118.2)
                .withPostalCode("90068")
                .withRegion("CA");

        Map<String,Double> metrics = new HashMap<>();
        metrics.put("health", 100.00);
        metrics.put("luck", 75.00);

        EndpointUser user = new EndpointUser()
                .withUserId(UUID.randomUUID().toString());
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        String nowAsISO = df.format(new Date());

        EndpointRequest endpointRequest = new EndpointRequest()
                .withAddress(UUID.randomUUID().toString())
                .withAttributes(customAttributes)
                .withChannelType("APNS")
                .withDemographic(demographic)
                .withEffectiveDate(nowAsISO)
                .withLocation(location)
                .withMetrics(metrics)
                .withOptOut("NONE")
                .withRequestId(UUID.randomUUID().toString())
                .withUser(user);

        return endpointRequest;
    }
	
}
