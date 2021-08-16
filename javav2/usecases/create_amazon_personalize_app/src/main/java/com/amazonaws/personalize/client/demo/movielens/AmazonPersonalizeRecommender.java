/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.demo.movielens;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.personalize.client.datasets.DatasetProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.personalizeevents.PersonalizeEventsClient;
import software.amazon.awssdk.services.personalizeevents.model.Event;
import software.amazon.awssdk.services.personalizeevents.model.PutEventsRequest;

import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsRequest;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsResponse;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;


public class AmazonPersonalizeRecommender implements RecommendationsInterface {

    private final PersonalizeEventsClient personalizeEventsClient;
    private final PersonalizeRuntimeClient personalizeRuntimeClient;
    private final String userPersonalizationCampaignArn;
    private final String simsCampaignArn;
    private final String trackingId;
    private final Map<String, String> itemIdToNameMap;

    public AmazonPersonalizeRecommender(
            PersonalizeRuntimeClient personalizeRuntimeClient,
            PersonalizeEventsClient personalizeEventsClient,
            String userPersonalizationCampaignArn,
            String simsCampaignArn,
            String trackingId,
            DatasetProvider datasetProvider) throws IOException {
        super();
        this.personalizeRuntimeClient = personalizeRuntimeClient;
        this.personalizeEventsClient = personalizeEventsClient;
        this.userPersonalizationCampaignArn = userPersonalizationCampaignArn;
        this.simsCampaignArn = simsCampaignArn;
        this.trackingId = trackingId;
        itemIdToNameMap = datasetProvider.getItemIdToNameMapping();
    }

    public void putEvent(UserEvent e) {
        try {
            Event event = Event.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(e.event)
                    .itemId(e.itemId)
                    .sentAt(Instant.ofEpochMilli(System.currentTimeMillis() + 10 * 60 * 1000))
                    .build();

            PutEventsRequest putEventsRequest = PutEventsRequest.builder()
                    .trackingId(trackingId)
                    .userId(e.getUserId())
                    .sessionId("temp")
                    .eventList(event)
                    .build();
            personalizeEventsClient.putEvents(putEventsRequest);
            System.out.println("Event sent: " + e);
        } catch (AwsServiceException ex) {
            System.err.println(ex.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public List<Item> getItemsForUser(String userId) {
        List<Item> list = new ArrayList<>();

        try {
            GetRecommendationsRequest recommendationsRequest = GetRecommendationsRequest.builder()
                    .campaignArn(userPersonalizationCampaignArn)
                    .numResults(20)
                    .userId(userId)
                    .build();

            GetRecommendationsResponse recommendationsResponse = personalizeRuntimeClient.getRecommendations(recommendationsRequest);
            List<PredictedItem> items = recommendationsResponse.itemList();

            for (PredictedItem item : items) {
                list.add(new Item(item.itemId(), itemIdToNameMap.get(item.itemId())));
            }
        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return list;
    }

    public List<Item> getItemsForItem(String itemId) {
        List<Item> list = new ArrayList<>();
        try {
            GetRecommendationsRequest simsRequest = GetRecommendationsRequest.builder()
                    .campaignArn(simsCampaignArn)
                    .itemId(itemId)
                    .build();

            GetRecommendationsResponse simsResponse = personalizeRuntimeClient.getRecommendations(simsRequest);
            List<PredictedItem> items = simsResponse.itemList();

            for (PredictedItem item : items) {
                list.add(new Item(item.itemId(), itemIdToNameMap.get(item.itemId())));
            }
        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return list;
    }

}
