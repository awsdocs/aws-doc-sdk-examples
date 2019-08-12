//snippet-sourcedescription:[CreateCampaign.java demonstrates how to create a campaign for an application in Pinpoint.]
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
//snippet-start:[pinpoint.java2.CreateCampaign.complete]
package com.example.pinpoint;

//snippet-start:[pinpoint.java2.CreateCampaign.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
//snippet-end:[pinpoint.java2.CreateCampaign.import]

public class CreateCampaign {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "CreateCampaign - create a campaign for an application in pinpoint\n\n" +
                "Usage: CreateCampaign <appId> <segmentId>\n\n" +
                "Where:\n" +
                "  appId - the ID of the application to create the campaign in.\n\n" +
                "  segmentId - the ID of the segment to create the campaign from.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[pinpoint.java2.CreateCampaign.main]
        String appId = args[0];
        String segmentId = args[1];

        PinpointClient pinpoint = PinpointClient.builder().region(Region.US_EAST_1).build();

        CampaignResponse result = createCampaign(pinpoint, appId, segmentId);
        System.out.println("Campaign " + result.name() + " created.");
        System.out.println(result.description());
        //snippet-end:[pinpoint.java2.CreateCampaign.main]
    }

    //snippet-start:[pinpoint.java2.CreateCampaign.helper]
    public static CampaignResponse createCampaign(PinpointClient client, String appID, String segmentID) {
        Schedule schedule = Schedule.builder()
                .startTime("IMMEDIATE")
                .build();

        Message defaultMessage = Message.builder()
                .action(Action.OPEN_APP)
                .body("My message body.")
                .title("My message title.")
                .build();

        MessageConfiguration messageConfiguration = MessageConfiguration.builder()
                .defaultMessage(defaultMessage)
                .build();

        WriteCampaignRequest request = WriteCampaignRequest.builder()
                .description("My description")
                .schedule(schedule)
                .name("MyCampaign")
                .segmentId(segmentID)
                .messageConfiguration(messageConfiguration)
                .build();

        CreateCampaignResponse result = client.createCampaign(
                CreateCampaignRequest.builder()
                        .applicationId(appID)
                        .writeCampaignRequest(request).build()
        );

        System.out.println("Campaign ID: " + result.campaignResponse().id());

        return result.campaignResponse();
    }
    //snippet-end:[pinpoint.java2.CreateCampaign.helper]


}
//snippet-end:[pinpoint.java2.CreateCampaign.complete]
