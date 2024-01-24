//snippet-sourcedescription:[SendEmailMessageCC.java demonstrates how to send an email message which includes CC values.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:AWS IoT]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.iot;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.IotException;
import software.amazon.awssdk.services.iot.model.SearchIndexRequest;
import software.amazon.awssdk.services.iot.model.SearchIndexResponse;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class SearchThings {

    public static void main(String[] args) {
        System.out.println("Searching for an AWS IoT Thing using a query string");
        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Specify the search query for the thing name "foo"
        String queryString = "thingName:foo";
        SearchIndexRequest searchIndexRequest = SearchIndexRequest.builder()
            .queryString(queryString)
            .build();

        try {
            // Perform the search and get the result.
            SearchIndexResponse searchIndexResponse = iotClient.searchIndex(searchIndexRequest);

            // Process the result
            searchIndexResponse.things().forEach(thing -> {
                System.out.println("Thing id: " + thing.thingId());
            });

        } catch (IotException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } finally {
            // Close the IoT client
            iotClient.close();
        }
    }
}
