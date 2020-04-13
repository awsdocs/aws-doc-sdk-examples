//snippet-sourcedescription:[UpdateChannel.java demonstrates how to update a channel for an application in Amazon Pinpoint.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-service:[pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/14/2020]
//snippet-sourceauthor:[scmacdon AWS]
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

//snippet-start:[pinpoint.java2.updatechannel.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.SMSChannelResponse;
import software.amazon.awssdk.services.pinpoint.model.GetSmsChannelRequest;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import software.amazon.awssdk.services.pinpoint.model.SMSChannelRequest;
import software.amazon.awssdk.services.pinpoint.model.UpdateSmsChannelRequest;
import software.amazon.awssdk.services.pinpoint.model.UpdateSmsChannelResponse;
//snippet-end:[pinpoint.java2.updatechannel.import]

public class UpdateChannel {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "CreateChannel -  create a channel in Amazon Pinpoint\n\n" +
                "Usage: CreateChannel <appId>\n\n" +
                "Where:\n" +
                "  appId - the name of the application to create.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String appId = args[0];
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        SMSChannelResponse getResponse = getSMSChannel(pinpoint, appId);
        toggleSmsChannel(pinpoint, appId, getResponse);
    }

    //snippet-start:[pinpoint.java2.updatechannel.main]
    private static SMSChannelResponse getSMSChannel(PinpointClient client, String appId) {

        try {
            GetSmsChannelRequest request = GetSmsChannelRequest.builder()
                    .applicationId(appId)
                    .build();

            SMSChannelResponse response = client.getSmsChannel(request).smsChannelResponse();
            System.out.println("Channel state: " + response.enabled());
            return response;

        } catch ( PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    private static void toggleSmsChannel(PinpointClient client, String appId, SMSChannelResponse getResponse) {
        boolean enabled = true;

        if (getResponse.enabled()) {
            enabled = false;
        }

        try {
            SMSChannelRequest request = SMSChannelRequest.builder()
                    .enabled(enabled)
                    .build();

            UpdateSmsChannelRequest updateRequest = UpdateSmsChannelRequest.builder()
                    .smsChannelRequest(request)
                    .applicationId(appId)
                    .build();

            UpdateSmsChannelResponse result = client.updateSmsChannel(updateRequest);

            System.out.println("Channel state: " + result.smsChannelResponse().enabled());
        } catch ( PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[pinpoint.java2.updatechannel.main]
}
