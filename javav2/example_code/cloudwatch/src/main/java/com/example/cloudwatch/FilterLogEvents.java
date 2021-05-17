//snippet-sourcedescription:[FilterLogEvents.java demonstrates how to get log events from Amazon CloudWatch.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.filter_logs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
// snippet-end:[cloudwatch.java2.filter_logs.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FilterLogEvents {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "  FilterLogEvents <logGroupName> <startTime> <endTime>\n\n" +
                "Where:\n" +
                "  logGroupName - the name of the log group (for example, myloggroup).\n" +
                "  startTime - the start of the time range, expressed as the number of milliseconds after Jan 1, 1970 00:00:00 UTC (for example, 1620940080).\n" +
                "  endTime - the end of the time range, expressed as the number of milliseconds after Jan 1, 1970 00:00:00 UTC (for example, 1620949080)\n" ;

        if (args.length != 3) {
            System.out.print(USAGE);
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

    // snippet-start:[cloudwatch.java2.filter_logs.main]
    public static void filterCWLogEvents(CloudWatchLogsClient cloudWatchLogsClient, String logGroupName, Long startTime, Long endTime) {

        try {
            FilterLogEventsRequest filterLogEventsRequest = FilterLogEventsRequest.builder()
                    .logGroupName(logGroupName)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            int logLimit = cloudWatchLogsClient.filterLogEvents(filterLogEventsRequest).events().size();
            for (int c = 0; c < logLimit; c++) {
                System.out.println(cloudWatchLogsClient.filterLogEvents(filterLogEventsRequest).events().get(c).message());
            }

            System.out.println("Successfully got CloudWatch log events!");
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        // snippet-end:[cloudwatch.java2.filter_logs.main]
    }
}

