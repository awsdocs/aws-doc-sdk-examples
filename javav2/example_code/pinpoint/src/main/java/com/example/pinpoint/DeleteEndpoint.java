//snippet-sourcedescription:[DeleteEndpoint.java demonstrates how to delete an endpoint.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
                "Usage: " +
                "DeleteEndpoint <appName> <endpointId >\n\n" +
                "Where:\n" +
                "  appId - the id of the application to delete.\n\n" +
                "  endpointId - the id of the endpoint to delete.\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String appId = args[0];
        String endpointId = args[1];
        System.out.println("Deleting an endpoint with id: " + endpointId);

        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deletePinEncpoint(pinpoint, appId, endpointId );
        pinpoint.close();
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
            System.out.println("The deleted endpoint id  " + id);
        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done");
        //snippet-end:[pinpoint.java2.deleteendpoint.main]
    }
}
