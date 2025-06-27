// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudwatch;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CloudWatchLogsException;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;
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
               <logGroupName>
            Where:
                logGroupName - The name of the log group (e.g., /aws/lambda/ChatAIHandler)
            """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String logGroupName = args[0];
        try (CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder()
                .region(Region.US_EAST_1)
                .build()) {
            fetchRecentLogs(logsClient, logGroupName);
        } catch (CloudWatchLogsException e) {
            System.err.println("Error accessing CloudWatch Logs: " + e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Retrieves and prints recent log events from the specified log group across all log streams.
     *
     * @param logsClient   the CloudWatchLogsClient used to interact with AWS CloudWatch Logs
     * @param logGroupName the name of the log group from which to retrieve the log events
     */
    public static void fetchRecentLogs(CloudWatchLogsClient logsClient, String logGroupName) {
        FilterLogEventsRequest request = FilterLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .limit(50) // Adjust as needed
                .build();

        FilterLogEventsResponse response = logsClient.filterLogEvents(request);
        if (response.events().isEmpty()) {
            System.out.println("No log events found.");
            return;
        }

        System.out.println("Recent log events:");
        for (FilteredLogEvent event : response.events()) {
            System.out.printf("[%s] %s%n", event.timestamp(), event.message());
        }
    }
}
// snippet-end:[cloudwatch.javav2.describe.log group.main]
