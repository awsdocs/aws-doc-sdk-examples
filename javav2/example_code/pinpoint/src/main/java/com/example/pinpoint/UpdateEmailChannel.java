/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pinpoint;

//snippet-start:[pinpoint.java2.updateemailchannel.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
//snippet-end:[pinpoint.java2.updateemailchannel.import]

public class UpdateEmailChannel {
    //snippet-start:[pinpoint.java2.updateemailchannel.main]
    public static void main(String[] args) {

        //TODO: Provide ApplicationId/ProjectId
        String appId = "";
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        EmailChannelResponse getResponse = getEmailChannel(pinpoint, appId);

        System.out.println("Response : " + getResponse);
        pinpoint.close();
    }
    //snippet-end:[pinpoint.java2.updateemailchannel.main]

    //snippet-start:[pinpoint.java2.updateemailchannel.helper]
    private static EmailChannelResponse getEmailChannel(PinpointClient client, String appId) {

        try {
            //TODO: Update personal identity and from address
            UpdateEmailChannelRequest updateRequest = UpdateEmailChannelRequest.builder()
                    .applicationId(appId)
                    .emailChannelRequest(EmailChannelRequest.builder()
                            .identity("")
                            .fromAddress("Test Mail <no-reply@mypmail.com>")
                            .enabled(true)
                            .build())
                    .build();

            EmailChannelResponse response = client.updateEmailChannel(updateRequest).emailChannelResponse();

            System.out.println("Channel state is " + response);
            return response;

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    //snippet-end:[pinpoint.java2.updateemailchannel.helper]
}
