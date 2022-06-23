//snippet-sourcedescription:[FilterLogEvents.java demonstrates how to get log events from Amazon CloudWatch.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcedate:[05/17/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.filter_logs.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
// snippet-end:[cloudwatch.java2.filter_logs.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FilterLogEvents {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "  <logGroupName> <startTime> <endTime>\n\n" +
                "Where:\n" +
                "  logGroupName - The name of the log group (for example, myloggroup).\n" +
                "  startTime - The start of the time range, expressed as the number of milliseconds after Jan 1, 1970 00:00:00 UTC (for example, 1620940080).\n" +
                "  endTime - The end of the time range, expressed as the number of milliseconds after Jan 1, 1970 00:00:00 UTC (for example, 1620949080)\n" ;

        if (args.length != 3) {
            System.out.print(usage);
            System.exit(1);
        }

        String logGroupName = args[0];
        Long startTime = Long.parseLong(args[1]);
        Long endTime = Long.parseLong(args[2]);
        Region region = Region.US_WEST_2;
        CloudWatchLogsClient cloudWatchLogsClient = CloudWatchLogsClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
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
    }
    // snippet-end:[cloudwatch.java2.filter_logs.main]
}
