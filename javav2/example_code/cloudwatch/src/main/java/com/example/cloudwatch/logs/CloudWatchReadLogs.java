// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudwatch.logs;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CloudWatchLogsException;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogStream;
import software.amazon.awssdk.services.cloudwatchlogs.model.OrderBy;
import software.amazon.awssdk.services.cloudwatchlogs.model.OutputLogEvent;
import java.util.List;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
// snippet-start:[cloudwatch.javav2.describe.log group.main]
public class CloudWatchReadLogs {
    public static void main(final String[] args) {
        final String usage = """
                             Usage:
                             <logGroupName> <startTime> <endTime>
                             Where:
                             logGroupName - The name of the log group (for example, /aws/lambda/ChatAIHandler).
                             startTime - The start time for the log events (in milliseconds since epoch).
                             endTime - The end time for the log events (in milliseconds since epoch).
                             """;
        if (args.length != 3) {
            System.out.print(usage);
            System.exit(1);
        }
        String logGroupName = args[0];
        long startTime = Long.parseLong(args[1]);
        long endTime = Long.parseLong(args[2]);
        CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder()
                .region(Region.US_EAST_1)
                .build();
        getLogEvents(logsClient, logGroupName, startTime, endTime);
    }

    /**
     * Retrieves and prints the log events from the specified log group and the most recent log stream within that group,
     * filtered by the provided start and end times.
     *
     * @param logsClient   the CloudWatchLogsClient used to interact with AWS CloudWatch Logs
     * @param logGroupName the name of the log group from which to retrieve the log events
     * @param startTime    the start time for the log events (in milliseconds since epoch)
     * @param endTime      the end time for the log events (in milliseconds since epoch)
     */
    public static void getLogEvents(CloudWatchLogsClient logsClient, String logGroupName, long startTime, long endTime) {
        DescribeLogStreamsRequest streamsRequest = DescribeLogStreamsRequest.builder()
                .logGroupName(logGroupName)
                .orderBy(OrderBy.LAST_EVENT_TIME)
                .descending(true)
                .limit(1)
                .build();
        try {
            DescribeLogStreamsResponse streamsResponse = logsClient.describeLogStreams(streamsRequest);
            List<LogStream> logStreams = streamsResponse.logStreams();
            if (logStreams.isEmpty()) {
                System.out.println("No log streams found for log group: " + logGroupName);
                return;
            }

            String logStreamName = logStreams.get(0).logStreamName();
            GetLogEventsRequest eventsRequest = GetLogEventsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamName(logStreamName)
                    .startFromHead(true)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            GetLogEventsResponse eventsResponse = logsClient.getLogEvents(eventsRequest);
            System.out.println("Log events from: " + logStreamName);
            for (OutputLogEvent event : eventsResponse.events()) {
                if (event.timestamp() >= startTime && event.timestamp() <= endTime) {
                    System.out.printf("[%s] %s%n", event.timestamp(), event.message());
                }
            }
        } catch (CloudWatchLogsException e) {
            System.err.println("Failed to fetch logs: " + e.awsErrorDetails().errorMessage());
        }
    }
}
// snippet-end:[cloudwatch.javav2.describe.log group.main]
