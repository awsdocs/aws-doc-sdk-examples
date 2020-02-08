//snippet-sourcedescription:[UpdateChannel.java demonstrates how to create a channel for an application in Pinpoint.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
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
//snippet-start:[pinpoint.java2.UpdateChannel.complete]
package com.example.pinpoint;

//snippet-start:[pinpoint.java2.UpdateChannel.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
//snippet-end:[pinpoint.java2.UpdateChannel.import]

public class UpdateChannel {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "CreateChannel -  create a channel in pinpoint\n\n" +
                "Usage: CreateChannel <appId>\n\n" +
                "Where:\n" +
                "  appId - the name of the application to create.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[pinpoint.java2.UpdateChannel.main]
        String appId = args[0];

        PinpointClient pinpoint = PinpointClient.builder().region(Region.US_EAST_1).build();
        APNSChannelResponse getResponse = getApnsChannel(pinpoint, appId);
        toggleApnsChannel(pinpoint, appId, getResponse);
        getApnsChannel(pinpoint, appId);
        //snippet-end:[pinpoint.java2.UpdateChannel.main]
    }

    //snippet-start:[pinpoint.java2.UpdateChannel.helper]
    private static APNSChannelResponse getApnsChannel(PinpointClient client, String appId) {
        GetApnsChannelRequest request = GetApnsChannelRequest.builder()
                .applicationId(appId)
                .build();

        APNSChannelResponse response = client.getApnsChannel(request).apnsChannelResponse();
        System.out.println("Channel state: " + response.enabled());
        return response;
    }

    private static void toggleApnsChannel(PinpointClient client, String appId, APNSChannelResponse getResponse) {
        boolean enabled = true;

        if (getResponse.enabled()) {
            enabled = false;
        }

        APNSChannelRequest request = APNSChannelRequest.builder()
                .enabled(enabled)
                .build();

        UpdateApnsChannelRequest updateRequest = UpdateApnsChannelRequest.builder()
                .apnsChannelRequest(request)
                .applicationId(appId)
                .build();
        UpdateApnsChannelResponse result = client.updateApnsChannel(updateRequest);
        System.out.println("Channel state: " + result.apnsChannelResponse().enabled());
    }//snippet-end:[pinpoint.java2.UpdateChannel.helper]
}
//snippet-end:[pinpoint.java2.UpdateChannel.complete]