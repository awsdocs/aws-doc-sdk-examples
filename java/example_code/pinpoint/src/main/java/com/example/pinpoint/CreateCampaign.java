//snippet-sourcedescription:[CreateCampaign.java demonstrates how to create a campaign for an application in Pinpoint.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mobiletargeting]
//snippet-sourcetype:[full-example]
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
import com.amazonaws.services.pinpoint.model.CreateCampaignRequest;
import com.amazonaws.services.pinpoint.model.CreateCampaignResult;
import com.amazonaws.services.pinpoint.model.Action;
import com.amazonaws.services.pinpoint.model.CampaignResponse;
import com.amazonaws.services.pinpoint.model.Message;
import com.amazonaws.services.pinpoint.model.MessageConfiguration;
import com.amazonaws.services.pinpoint.model.Schedule;
import com.amazonaws.services.pinpoint.model.WriteCampaignRequest;

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
        String appId = args[0];
        String segmentId = args[1];

		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    	CampaignResponse result = createCampaign(pinpoint, appId, segmentId);
    	System.out.println("Campaign " + result.getName() + " created.");
    	System.out.println(result.getDescription());
	}

	public static CampaignResponse createCampaign(AmazonPinpoint client, String appId, String segmentId) {
        Schedule schedule = new Schedule()
                .withStartTime("IMMEDIATE");

        Message defaultMessage = new Message()
                .withAction(Action.OPEN_APP)
                .withBody("My message body.")
                .withTitle("My message title.");

        MessageConfiguration messageConfiguration = new MessageConfiguration()
                .withDefaultMessage(defaultMessage);

        WriteCampaignRequest request = new WriteCampaignRequest()
                .withDescription("My description.")
                .withSchedule(schedule)
                .withSegmentId(segmentId)
                .withName("MyCampaign")
                .withMessageConfiguration(messageConfiguration);

        CreateCampaignRequest createCampaignRequest = new CreateCampaignRequest()
                .withApplicationId(appId).withWriteCampaignRequest(request);

        CreateCampaignResult result = client.createCampaign(createCampaignRequest);

        System.out.println("Campaign ID: " + result.getCampaignResponse().getId());

        return result.getCampaignResponse();
    }
}
