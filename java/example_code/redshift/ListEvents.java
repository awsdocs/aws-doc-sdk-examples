/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
*/

// snippet-sourcedescription:[ListEvents demonstrates how to list Amazon Redshift events.]
// snippet-service:[redshift]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Redshift]
// snippet-keyword:[Code Sample]
// snippet-keyword:[DescribeEvents]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[redshift.java.ListEvents.complete]

package com.amazonaws.services.redshift;

import java.util.Date;
import java.io.IOException;

import com.amazonaws.services.redshift.model.*;

public class ListEvents {

    public static AmazonRedshift client;
    public static String clusterIdentifier = "***provide cluster identifier***";
    public static String eventSourceType = "cluster"; // e.g. cluster-snapshot

    public static void main(String[] args) throws IOException {

        // Default client using the {@link com.amazonaws.auth.DefaultAWSCredentialsProviderChain}

       client = AmazonRedshiftClientBuilder.defaultClient();

      try {
             listEvents();
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    private static void listEvents() {
        long oneWeeksAgoMilli = (new Date()).getTime() - (7L*24L*60L*60L*1000L);
        Date oneWeekAgo = new Date();
        oneWeekAgo.setTime(oneWeeksAgoMilli);
        String marker = null;

        do {
            DescribeEventsRequest request = new DescribeEventsRequest()
            .withSourceIdentifier(clusterIdentifier)
            .withSourceType(eventSourceType)
            .withStartTime(oneWeekAgo)
            .withMaxRecords(20);
            DescribeEventsResult result = client.describeEvents(request);
            marker = result.getMarker();
            for (Event event : result.getEvents()) {
                printEvent(event);
            }
        } while (marker != null);


    }
    static void printEvent(Event event)
    {
        if (event == null)
        {
            System.out.println("\nEvent object is null.");
            return;
        }

        System.out.println("\nEvent metadata:\n");
        System.out.format("SourceID: %s\n", event.getSourceIdentifier());
        System.out.format("Type: %s\n", event.getSourceType());
        System.out.format("Message: %s\n", event.getMessage());
        System.out.format("Date: %s\n", event.getDate());
    }

}
 // snippet-end:[redshift.java.ListEvents.complete]import java.io.IOException;
