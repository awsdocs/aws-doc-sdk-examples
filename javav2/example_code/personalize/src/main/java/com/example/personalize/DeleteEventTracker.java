//snippet-sourcedescription:[DeleteEventTracker.java demonstrates how to delete
// an Amazon Personalize event tracker.]
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

//snippet-start:[personalize.java2.delete_event_tracker.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.DeleteEventTrackerRequest;
//snippet-end:[personalize.java2.delete_event_tracker.import]
public class DeleteEventTracker {
 
    public static void main(String [] args) {

        Region region = Region.US_WEST_2;
        String eventTrackerArn = args[0];

        PersonalizeClient personalizeClient = PersonalizeClient.builder()
            .region(region)
            .build();

        deleteEventTracker(personalizeClient, eventTrackerArn);
        personalizeClient.close();
    }
    //snippet-start:[personalize.java2.delete_event_tracker.main]
    public static void deleteEventTracker(PersonalizeClient personalizeClient, String eventTrackerArn) {
        try {
            DeleteEventTrackerRequest deleteEventTrackerRequest = DeleteEventTrackerRequest.builder()
                .eventTrackerArn(eventTrackerArn)
                .build();
                
            int status = 
                personalizeClient.deleteEventTracker(deleteEventTrackerRequest).sdkHttpResponse().statusCode();

            System.out.println("Status code:" + status);
            
        }
        catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.delete_event_tracker.main]
}
