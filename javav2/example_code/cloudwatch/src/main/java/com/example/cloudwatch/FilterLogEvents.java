// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.filter_logs.main]
// snippet-start:[cloudwatch.java2.filter_logs.import]
import java.util.List;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;
// snippet-end:[cloudwatch.java2.filter_logs.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FilterLogEvents {
    public static void main(String[] args) {

        final String usage = """

                Usage:
                  <logGroupName> <startTime> <endTime>

                Where:
                  logGroupName - The name of the log group (for example, myloggroup).
                  startTime - The start of the time range, expressed as the number of milliseconds after Jan 1, 1970 00:00:00 UTC (for example, 1620940080).
                  endTime - The end of the time range, expressed as the number of milliseconds after Jan 1, 1970 00:00:00 UTC (for example, 1620949080)
                """;

        if (args.length != 3) {
            System.out.print(usage);
            System.exit(1);
        }

        String logGroupName = args[0];
        Long startTime = Long.parseLong(args[1]);
        Long endTime = Long.parseLong(args[2]);
        Region region = Region.US_WEST_2;
        CloudWatchLogsClient cloudWatchLogsClient = CloudWatchLogsClient.builder()
                .region(region)
                .build();

        filterCWLogEvents(cloudWatchLogsClient, logGroupName, startTime, endTime);
        cloudWatchLogsClient.close();
    }

    public static void filterCWLogEvents(CloudWatchLogsClient cloudWatchLogsClient, String logGroupName, Long startTime,
            Long endTime) {
        try {
            FilterLogEventsRequest filterLogEventsRequest = FilterLogEventsRequest.builder()
                    .logGroupName(logGroupName)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            List<FilteredLogEvent> events = cloudWatchLogsClient.filterLogEvents(filterLogEventsRequest).events();
            for (FilteredLogEvent event : events) {
                System.out.println(event.message());
            }

            System.out.println("Successfully got CloudWatch log events!");

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[cloudwatch.java2.filter_logs.main]
