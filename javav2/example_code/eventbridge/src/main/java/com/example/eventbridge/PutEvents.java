//snippet-sourcedescription:[PutEvents.java demonstrates how to send custom events to Amazon EventBridge.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EventBridge]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[09/28/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._put_event.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[eventbridge.java2._put_event.import]
/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutEvents {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply two resources, identified by Amazon Resource Name (ARN), which the event primarily concerns. " +
                        "Any number, including zero, may be present. \n" +
                        "For example: PutEvents <resourceArn> <resourceArn2>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String resourceArn = args[0];
        String resourceArn2 = args[1];

        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .build();

        putEBEvents(eventBrClient, resourceArn, resourceArn2);
        eventBrClient.close();
    }

    // snippet-start:[eventbridge.java2._put_event.main]
    public static void putEBEvents(EventBridgeClient eventBrClient, String resourceArn, String resourceArn2 ) {

        try {
            // Populate a List with the resource ARN values
            List<String> resources = new ArrayList<String>();
            resources.add(resourceArn);
            resources.add(resourceArn2);

            PutEventsRequestEntry reqEntry = PutEventsRequestEntry.builder()
                    .resources(resources)
                    .source("com.mycompany.myapp")
                    .detailType("myDetailType")
                    .detail("{ \"key1\": \"value1\", \"key2\": \"value2\" }")
                    .build();

            PutEventsRequest eventsRequest = PutEventsRequest.builder()
                    .entries(reqEntry)
                    .build();

            PutEventsResponse result = eventBrClient.putEvents(eventsRequest);

            for (PutEventsResultEntry resultEntry : result.entries()) {
                if (resultEntry.eventId() != null) {
                    System.out.println("Event Id: " + resultEntry.eventId());
                } else {
                    System.out.println("Injection failed with Error Code: " + resultEntry.errorCode());
                }
            }

        } catch (EventBridgeException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[eventbridge.java2._put_event.main]
}
