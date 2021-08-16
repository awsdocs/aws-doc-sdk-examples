/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateEventTrackerRequest;
import software.amazon.awssdk.services.personalize.model.CreateEventTrackerResponse;
import software.amazon.awssdk.services.personalize.model.DeleteEventTrackerRequest;
import software.amazon.awssdk.services.personalize.model.DescribeEventTrackerRequest;
import software.amazon.awssdk.services.personalize.model.EventTrackerSummary;
import software.amazon.awssdk.services.personalize.model.ListEventTrackersRequest;
import software.amazon.awssdk.services.personalize.model.ListEventTrackersResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

public class EventTrackerManager extends AbstractResourceManager {

    private final String datasetGroupArn;

    public EventTrackerManager(PersonalizeClient personalizeClient, String name, String datasetGroupArn) {
        super(personalizeClient, name);
        this.datasetGroupArn = datasetGroupArn;
    }

    @Override
    protected String createResourceInternal() {

        String eventTrackerArn = "";
        try {
            CreateEventTrackerRequest createEventTrackerRequest = CreateEventTrackerRequest.builder()
                    .name(getName())
                    .datasetGroupArn(datasetGroupArn)
                    .build();

            CreateEventTrackerResponse createEventTrackerResponse = getPersonalize().createEventTracker(createEventTrackerRequest);
            eventTrackerArn = createEventTrackerResponse.eventTrackerArn();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return eventTrackerArn;
    }

    @Override
    protected void deleteResourceInternal(String arn) {
        try {
            DeleteEventTrackerRequest deleteEventTrackerRequest = DeleteEventTrackerRequest.builder()
                    .eventTrackerArn(arn)
                    .build();
            getPersonalize().deleteEventTracker(deleteEventTrackerRequest);
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }

    @Override
    protected String getResourceStatus(String arn) {
        try {
            DescribeEventTrackerRequest describeRequest = DescribeEventTrackerRequest.builder()
                    .eventTrackerArn(arn)
                    .build();
            return getPersonalize().describeEventTracker(describeRequest).eventTracker().status();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public String getTrackingId(String arn) {
        try {
            DescribeEventTrackerRequest describeRequest = DescribeEventTrackerRequest.builder()
                    .eventTrackerArn(arn)
                    .build();
            return getPersonalize().describeEventTracker(describeRequest).eventTracker().trackingId();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {
        try {
            ListEventTrackersRequest listEventTrackersRequest = ListEventTrackersRequest.builder()
                    .maxResults(100)
                    .build();
            ListEventTrackersResponse listEventTrackersResponse = getPersonalize().listEventTrackers(listEventTrackersRequest);

            for (EventTrackerSummary eventTracker : listEventTrackersResponse.eventTrackers()) {
                if (eventTracker.name().equals(name)) {
                    return eventTracker.eventTrackerArn();
                }
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

}
