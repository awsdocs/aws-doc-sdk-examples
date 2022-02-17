//snippet-sourcedescription:[CreateEventTracker.java demonstrates how to create
// an Amazon Personalize event tracker that you can use with the PutEvents operation.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/13/2021]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_event_tracker.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateEventTrackerRequest;
import software.amazon.awssdk.services.personalize.model.CreateEventTrackerResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.DescribeEventTrackerRequest;

import java.time.Instant;
//snippet-end:[personalize.java2.create_event_tracker.import]
public class CreateEventTracker {

    

    public static void main(String [] args) {
        
        final String USAGE = "Usage:\n" +
        "    CreateDatasetGroup <name, datasetGroupArn>\n\n" +
        "Where:\n" +
        "   datasetGroupArn - The Amazon Resource Name (ARN) of the dataset group that receives the event data\n" +
        "   name - The name for the event tracker.\n\n";
        
        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        
        String datasetGroupArn = args[0];
        String eventTrackerName = args[1];
        Region region = Region.US_WEST_2;

        PersonalizeClient personalizeClient = PersonalizeClient.builder()
            .region(region)
            .build();

        String trackerId = createEventTracker(personalizeClient, eventTrackerName, datasetGroupArn);
        System.out.println(trackerId);
        personalizeClient.close();
    }
    //snippet-start:[personalize.java2.create_event_tracker.main]
    public static String createEventTracker(PersonalizeClient personalizeClient, String eventTrackerName, String datasetGroupArn) {
        
        String eventTrackerId = "";
        String eventTrackerArn;
        long maxTime = 3 * 60 * 60; // 3 hours
        long waitInMilliseconds = 20 * 1000; // 20 seconds
        String status;
        
        try {

            CreateEventTrackerRequest createEventTrackerRequest = CreateEventTrackerRequest.builder()
                .name(eventTrackerName)
                .datasetGroupArn(datasetGroupArn)
                .build();
           
            CreateEventTrackerResponse createEventTrackerResponse = personalizeClient.createEventTracker(createEventTrackerRequest);
            
            eventTrackerArn = createEventTrackerResponse.eventTrackerArn();
            eventTrackerId = createEventTrackerResponse.trackingId();
            System.out.println("Event tracker ARN: " + eventTrackerArn);
            System.out.println("Event tracker ID: " + eventTrackerId);

            maxTime = Instant.now().getEpochSecond() + maxTime;

            DescribeEventTrackerRequest describeRequest = DescribeEventTrackerRequest.builder()
                .eventTrackerArn(eventTrackerArn)
                .build();

            
            while (Instant.now().getEpochSecond() < maxTime) {

                status = personalizeClient.describeEventTracker(describeRequest).eventTracker().status();
                System.out.println("EventTracker status: " + status);

                if (status.equals("ACTIVE") || status.equals("CREATE FAILED")) {
                    break;
                }
                try {
                    Thread.sleep(waitInMilliseconds);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            return eventTrackerId;
        }
        catch (PersonalizeException e){
            System.out.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return eventTrackerId;
    }
    //snippet-end:[personalize.java2.create_event_tracker.main]
}
