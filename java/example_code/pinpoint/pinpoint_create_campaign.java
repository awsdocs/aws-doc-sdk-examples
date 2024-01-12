// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[pinpoint.java.pinpoint_create_campaign.complete]

import com.amazonaws.services.pinpoint.AmazonPinpointClient;
import com.amazonaws.services.pinpoint.model.Action;
import com.amazonaws.services.pinpoint.model.CampaignResponse;
import com.amazonaws.services.pinpoint.model.CreateCampaignRequest;
import com.amazonaws.services.pinpoint.model.CreateCampaignResult;
import com.amazonaws.services.pinpoint.model.Message;
import com.amazonaws.services.pinpoint.model.MessageConfiguration;
import com.amazonaws.services.pinpoint.model.Schedule;
import com.amazonaws.services.pinpoint.model.WriteCampaignRequest;

import java.util.ArrayList;
import java.util.List;

public class PinpointCampaignSample {

        public CampaignResponse createCampaign(AmazonPinpointClient client, String appId, String segmentId) {
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

// snippet-end:[pinpoint.java.pinpoint_create_campaign.complete]
