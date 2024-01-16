// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[pinpoint.java.pinpoint_lookup_endpoint.complete]

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.EndpointResponse;
import com.amazonaws.services.pinpoint.model.GetEndpointRequest;
import com.amazonaws.services.pinpoint.model.GetEndpointResult;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LookUpEndpoint {

        public static void main(String[] args) {

                final String USAGE = "\n" +
                                "LookUpEndpoint - Prints the definition of the endpoint that has the specified ID." +
                                "Usage: LookUpEndpoint <applicationId> <endpointId>\n\n" +

                                "Where:\n" +
                                "  applicationId - The ID of the Amazon Pinpoint application that has the " +
                                "endpoint." +
                                "  endpointId - The ID of the endpoint ";

                if (args.length < 1) {
                        System.out.println(USAGE);
                        System.exit(1);
                }

                String applicationId = args[0];
                String endpointId = args[1];

                // Specifies the endpoint that the Amazon Pinpoint client looks up.
                GetEndpointRequest request = new GetEndpointRequest()
                                .withEndpointId(endpointId)
                                .withApplicationId(applicationId);

                // Initializes the Amazon Pinpoint client.
                AmazonPinpoint pinpointClient = AmazonPinpointClientBuilder.standard()
                                .withRegion(Regions.US_EAST_1).build();

                // Uses the Amazon Pinpoint client to get the endpoint definition.
                GetEndpointResult result = pinpointClient.getEndpoint(request);
                EndpointResponse endpoint = result.getEndpointResponse();

                // Uses the Google Gson library to pretty print the endpoint JSON.
                Gson gson = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                                .setPrettyPrinting()
                                .create();
                String endpointJson = gson.toJson(endpoint);

                System.out.println(endpointJson);
        }
}

// snippet-end:[pinpoint.java.pinpoint_lookup_endpoint.complete]
