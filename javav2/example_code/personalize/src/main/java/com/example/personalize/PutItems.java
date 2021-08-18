//snippet-sourcedescription:[PutItems.java demonstrates how to incrementally import items into Amazon Personalize.]
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

//snippet-start:[personalize.java2.put_items.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeevents.PersonalizeEventsClient;
import software.amazon.awssdk.services.personalizeevents.model.PersonalizeEventsException;
import software.amazon.awssdk.services.personalizeevents.model.PutItemsRequest;
import software.amazon.awssdk.services.personalizeevents.model.Item;

import java.util.ArrayList;

//snippet-end:[personalize.java2.put_items.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutItems {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutItems <datasetArn, item1Id, item1PropertyName, item1PropertyValue,\n" +
                "        item2Id, item2PropertyName, item2PropertyValue>\n\n" +
                "Where:\n" +
                "    datasetArn - The ARN (Amazon Resource Name) for the item's destination dataset.\n" +
                "    item1Id - The identification number of the first item.\n" +
                "    item1propertyName - The metadata field name (in camel case) for the first item.\n" +
                "    item1propertyValue - The metadata value for the first item.\n" +
                "    item2Id - The identification number of the second item.\n" +
                "    item2propertyName - The metadata field name (in camel case) for the second item.\n" +
                "    item2propertyValue - The metadata value for the second item.\n\n";

        if (args.length != 7) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String datasetArn = args[0];
        String item1Id = args[1];
        String item1PropertyName = args[2];
        String item1PropertyValue = args[3];
        String item2Id = args[4];
        String item2PropertyName = args[5];
        String item2PropertyValue = args[6];

        // Change to the region where your resources are located
        Region region = Region.US_WEST_2;

        // Build a personalize events client
        PersonalizeEventsClient personalizeEventsClient = PersonalizeEventsClient.builder()
                .region(region)
                .build();
        int response = putItems(personalizeEventsClient, datasetArn, item1Id,
                item1PropertyName, item1PropertyValue, item2Id, item2PropertyName, item2PropertyValue);
        System.out.println("Response code: " + response);
        personalizeEventsClient.close();
    }

    //snippet-start:[personalize.java2.put_items.main]
    public static int putItems(PersonalizeEventsClient personalizeEventsClient,
                               String datasetArn,
                               String item1Id,
                               String item1PropertyName,
                               String item1PropertyValue,
                               String item2Id,
                               String item2PropertyName,
                               String item2PropertyValue) {

        int responseCode = 0;
        ArrayList<Item> items = new ArrayList<>();

        try {
            Item item1 = Item.builder()
                    .itemId(item1Id)
                    .properties(String.format("{\"%1$s\": \"%2$s\"}",
                            item1PropertyName, item1PropertyValue))
                    .build();

            items.add(item1);

            Item item2 = Item.builder()
                    .itemId(item2Id)
                    .properties(String.format("{\"%1$s\": \"%2$s\"}",
                            item2PropertyName, item2PropertyValue))
                    .build();

            items.add(item2);

            PutItemsRequest putItemsRequest = PutItemsRequest.builder()
                    .datasetArn(datasetArn)
                    .items(items)
                    .build();

            responseCode = personalizeEventsClient.putItems(putItemsRequest).sdkHttpResponse().statusCode();
            System.out.println("Response code: " + responseCode);
            return responseCode;

        } catch (PersonalizeEventsException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return responseCode;
    }
    //snippet-end:[personalize.java2.put_items.main]

}

