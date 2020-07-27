//snippet-sourcedescription:[PutEvents.java demonstrates how to send custom events to Amazon EventBridge.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EventBridge]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/22/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._put_event.import]
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[eventbridge.java2._put_event.import]

public class PutEvents {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a resource arn\n" +
                        "Ex: PutEvents <resource-arn>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        EventBridgeClient eventBrClient =
                EventBridgeClient.builder().build();

        String resourceArn = args[0];
        String resourceArn2 = args[1];

        putEBEvents(eventBrClient, resourceArn, resourceArn2);
    }

    // snippet-start:[eventbridge.java2._put_event.main]
    public static void putEBEvents(EventBridgeClient eventBrClient, String resourceArn, String resourceArn2 ) {

        try {

            // Populate a List with the resource ARN values
            List<String> resources = new ArrayList<String>();
            resources.add(resourceArn);
            resources.add(resourceArn2);

            // Create a PutEventsRequestEntry object
            PutEventsRequestEntry reqEntry = PutEventsRequestEntry.builder()
                    .resources(resources)
                    .source("com.mycompany.myapp")
                    .detailType("myDetailType")
                    .detail("{ \"key1\": \"value1\", \"key2\": \"value2\" }")
                    .build();

            // Add the PutEventsRequestEntry to a list
            List<PutEventsRequestEntry> list = new ArrayList<PutEventsRequestEntry>();
            list.add(reqEntry);

            PutEventsRequest eventsRequest = PutEventsRequest.builder()
                    .entries(reqEntry)
                    .build();

            // Invoke the PutEvents method
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
        // snippet-end:[eventbridge.java2._put_event.main]
    }
}
