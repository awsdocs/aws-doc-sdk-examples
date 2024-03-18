// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.redshift;

// snippet-start:[redshift.java2._events.main]
// snippet-start:[redshift.java2._events.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.DescribeEventsRequest;
import software.amazon.awssdk.services.redshift.model.SourceType;
import software.amazon.awssdk.services.redshift.paginators.DescribeEventsIterable;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import java.util.Date;
// snippet-end:[redshift.java2._events.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListEvents {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                     <clusterId> <eventSourceType>\s

                Where:
                    clusterId - The id of the cluster.\s
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterId = args[0];
        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
                .region(region)
                .build();

        listRedShiftEvents(redshiftClient, clusterId);
        redshiftClient.close();
    }

    public static void listRedShiftEvents(RedshiftClient redshiftClient, String clusterId) {
        try {
            long oneWeeksAgoMilli = (new Date()).getTime() - (7L * 24L * 60L * 60L * 1000L);
            Date oneWeekAgo = new Date();
            oneWeekAgo.setTime(oneWeeksAgoMilli);

            DescribeEventsRequest describeEventsRequest = DescribeEventsRequest.builder()
                .sourceIdentifier(clusterId)
                .sourceType(SourceType.CLUSTER)
                .startTime(oneWeekAgo.toInstant())
                .maxRecords(20)
                .build();

            DescribeEventsIterable eventsResponse = redshiftClient.describeEventsPaginator(describeEventsRequest);
            eventsResponse.stream()
                .flatMap(r -> r.events().stream())
                .forEach(event -> System.out.println("Source type: " + event.sourceTypeAsString() +
                    " Event message: " + event.message()));

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[redshift.java2._events.main]
}