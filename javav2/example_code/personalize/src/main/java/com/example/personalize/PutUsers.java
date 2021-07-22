//snippet-sourcedescription:[PutUsers.java demonstrates how to incrementally import users into Amazon Personalize.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[5/26/2021]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.put_users.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeevents.PersonalizeEventsClient;
import software.amazon.awssdk.services.personalizeevents.model.PersonalizeEventsException;
import software.amazon.awssdk.services.personalizeevents.model.PutUsersRequest;
import software.amazon.awssdk.services.personalizeevents.model.User;

import java.util.ArrayList;

//snippet-end:[personalize.java2.put_users.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class PutUsers {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutUsers <datasetArn, user1Id, user1PropertyName, user1PropertyValue,\n" +
                "        user2Id, user2PropertyName, user2PropertyValue>\n\n" +
                "Where:\n" +
                "    datasetArn - The ARN (Amazon Resource Name) for the user's destination dataset.\n" +
                "    user1Id - The identification number of the first user.\n" +
                "    user1propertyName - The metadata field name (in camel case) for the first user.\n" +
                "    user1propertyValue - The metadata value for the first user.\n" +
                "    user2Id - The identification number of the second user.\n" +
                "    user2propertyName - The metadata field name (in camel case) for the second user.\n" +
                "    user2propertyValue - The metadata value for the second user.\n\n";

        if (args.length != 7) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String datasetArn = args[0];
        String user1Id = args[1];
        String user1PropertyName = args[2];
        String user1PropertyValue = args[3];
        String user2Id = args[4];
        String user2PropertyName = args[5];
        String user2PropertyValue = args[6];

        // Change to the region where your resources are located
        Region region = Region.US_WEST_2;

        // Build a personalize events client
        PersonalizeEventsClient personalizeEventsClient = PersonalizeEventsClient.builder()
                .region(region)
                .build();
        int response = putUsers(personalizeEventsClient, datasetArn,
                user1Id, user1PropertyName, user1PropertyValue,
                user2Id, user2PropertyName, user2PropertyValue);
        System.out.println("Response code: " + response);
        personalizeEventsClient.close();
    }

    //snippet-start:[personalize.java2.put_users.main]
    public static int putUsers(PersonalizeEventsClient personalizeEventsClient,
                               String datasetArn,
                               String user1Id,
                               String user1PropertyName,
                               String user1PropertyValue,
                               String user2Id,
                               String user2PropertyName,
                               String user2PropertyValue) {

        int responseCode = 0;
        ArrayList<User> users = new ArrayList<>();

        try {
            User user1 = User.builder()
                    .userId(user1Id)
                    .properties(String.format("{\"%1$s\": \"%2$s\"}",
                            user1PropertyName, user1PropertyValue))
                    .build();

            users.add(user1);

            User user2 = User.builder()
                    .userId(user2Id)
                    .properties(String.format("{\"%1$s\": \"%2$s\"}",
                            user2PropertyName, user2PropertyValue))
                    .build();

            users.add(user2);

            PutUsersRequest putUsersRequest = PutUsersRequest.builder()
                    .datasetArn(datasetArn)
                    .users(users)
                    .build();

            responseCode = personalizeEventsClient.putUsers(putUsersRequest).sdkHttpResponse().statusCode();
            System.out.println("Response code: " + responseCode);
            return responseCode;

        } catch (PersonalizeEventsException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return responseCode;
    }
    //snippet-end:[personalize.java2.put_users.main]

}

