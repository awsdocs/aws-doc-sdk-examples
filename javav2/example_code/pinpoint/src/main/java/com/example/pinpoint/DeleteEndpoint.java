//snippet-sourcedescription:[DeleteEndpoint.java demonstrates how to delete an endpoint.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-service:[pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

//snippet-start:[pinpoint.java2.deleteendpoint.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.DeleteEndpointRequest;
import software.amazon.awssdk.services.pinpoint.model.DeleteEndpointResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
//snippet-end:[pinpoint.java2.deleteendpoint.import]

public class DeleteEndpoint {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "CreateApp - create an application in the Amazon Pinpoint dashboard\n\n" +
                "Usage: CreateApp <appName>\n\n" +
                "Where:\n" +
                "  appId - the ID of the application to delete.\n\n" +
                "  endpointId - the ID of the endpoint to delete.\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String appId = args[0];
        String endpointId = args[0];

        System.out.println("Deleting an endpoint with ID: " + endpointId);

        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deletePinEncpoint(pinpoint, appId, endpointId );
    }

    //snippet-start:[pinpoint.java2.deleteendpoint.main]
    public static void deletePinEncpoint(PinpointClient pinpoint, String appId, String endpointId ) {

        try {

            DeleteEndpointRequest appRequest = DeleteEndpointRequest.builder()
                    .applicationId(appId)
                    .endpointId(endpointId)
                    .build();

            DeleteEndpointResponse result = pinpoint.deleteEndpoint(appRequest);
            String id = result.endpointResponse().id();
            System.out.println("The deleted endpoint ID  " + id);
        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done");
        //snippet-end:[pinpoint.java2.deleteendpoint.main]
    }
}
