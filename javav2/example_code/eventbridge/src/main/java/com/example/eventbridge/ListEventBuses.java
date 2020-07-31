//snippet-sourcedescription:[ListEventBuses.java demonstrates how to list your Amazon EventBridge buses.]
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

// snippet-start:[eventbridge.java2._list_buses.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
import software.amazon.awssdk.services.eventbridge.model.EventBus;
import software.amazon.awssdk.services.eventbridge.model.ListEventBusesRequest;
import software.amazon.awssdk.services.eventbridge.model.ListEventBusesResponse;
import java.util.List;
// snippet-end:[eventbridge.java2._list_buses.import]

public class ListEventBuses {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .build();

        listBuses(eventBrClient) ;
    }

    // snippet-start:[eventbridge.java2._list_buses.main]
    public static void listBuses( EventBridgeClient eventBrClient) {

        try {

            ListEventBusesRequest busesRequest = ListEventBusesRequest.builder()
                .limit(10)
                .build();

            ListEventBusesResponse response = eventBrClient.listEventBuses(busesRequest);

            List<EventBus> buses = response.eventBuses();
            for (EventBus bus: buses) {
                System.out.println("The name of the event bus is: "+bus.name());
                System.out.println("The ARN of the event bus is: "+bus.arn());
            }
        } catch (EventBridgeException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[eventbridge.java2._list_buses.main]
    }
}
