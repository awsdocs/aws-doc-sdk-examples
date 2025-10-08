// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.logs;

// snippet-start:[cloudwatch.java2.put_log_events.main]
// snippet-start:[cloudwatch.java2.put_log_events.import]

import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;

import java.util.Arrays;
import java.util.Collections;
// snippet-end:[cloudwatch.java2.put_log_events.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutLogEvents {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                  <logGroupName> <streamName>

                Where:
                  logGroupName - A log group name.
                  streamName - A stream name.
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String logGroupName = args[0];
        String streamName = args[1];
        CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder()
                .build();

        putCWLogEvents(logsClient, logGroupName, streamName);
        logsClient.close();
    }

    public static void putCWLogEvents(CloudWatchLogsClient logsClient, String logGroupName, String streamNamePrefix) {
        try {
            // First resolve the exact log stream name
            DescribeLogStreamsRequest logStreamRequest = DescribeLogStreamsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamNamePrefix(streamNamePrefix)
                    .limit(1) // get the first matching stream
                    .build();

            DescribeLogStreamsResponse describeLogStreamsResponse = logsClient.describeLogStreams(logStreamRequest);

            if (describeLogStreamsResponse.logStreams().isEmpty()) {
                System.err.println("No matching log stream found for prefix: " + streamNamePrefix);
                return;
            }

            String exactStreamName = describeLogStreamsResponse.logStreams().get(0).logStreamName();
            String sequenceToken = describeLogStreamsResponse.logStreams().get(0).uploadSequenceToken();

            // Build an input log message to put to CloudWatch.
            InputLogEvent inputLogEvent = InputLogEvent.builder()
                    .message("{ \"key1\": \"value1\", \"key2\": \"value2\" }")
                    .timestamp(System.currentTimeMillis())
                    .build();

            PutLogEventsRequest.Builder putLogEventsBuilder = PutLogEventsRequest.builder()
                    .logEvents(Collections.singletonList(inputLogEvent))
                    .logGroupName(logGroupName)
                    .logStreamName(exactStreamName);

            // Only set sequenceToken if it exists
            if (sequenceToken != null) {
                putLogEventsBuilder.sequenceToken(sequenceToken);
            }

            logsClient.putLogEvents(putLogEventsBuilder.build());
            System.out.println("Successfully put CloudWatch log event to stream: " + exactStreamName);

        } catch (CloudWatchException e) {
            System.err.println("CloudWatch error: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

}
// snippet-end:[cloudwatch.java2.put_log_events.main]
