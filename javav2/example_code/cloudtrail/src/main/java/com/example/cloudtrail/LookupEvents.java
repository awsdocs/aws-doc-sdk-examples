// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[LookupEvents.java demonstrates how to look up Cloud Trail events.]
// snippet-service:[AWS CloudTrail]
// snippet-keyword:[Java]
// snippet-keyword:[AWS CloudTrai]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-15]
// snippet-sourceauthor:[AWS - scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package com.example.cloudtrail;

//snippet-start:[cloudtrail.java2.events.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.Event;
import software.amazon.awssdk.services.cloudtrail.model.LookupEventsRequest;
import software.amazon.awssdk.services.cloudtrail.model.LookupEventsResponse;
import java.util.List;
//snippet-end:[cloudtrail.java2.events.import]

public class LookupEvents {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClientClient = CloudTrailClient.builder()
                .region(region)
                .build();

        lookupAllEvents(cloudTrailClientClient);
    }

    //snippet-start:[cloudtrail.java2.events.main]
    public static void lookupAllEvents(CloudTrailClient cloudTrailClientClient) {

        try {
            LookupEventsRequest eventsRequest = LookupEventsRequest.builder()
                .maxResults(20)
                .build();

            LookupEventsResponse response = cloudTrailClientClient.lookupEvents(eventsRequest);
            List<Event> events = response.events();

            for (Event event: events) {
                System.out.println("Event name is : "+event.eventName());
                System.out.println("The event source is : "+event.eventSource());
            }

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cloudtrail.java2.events.main]
}
