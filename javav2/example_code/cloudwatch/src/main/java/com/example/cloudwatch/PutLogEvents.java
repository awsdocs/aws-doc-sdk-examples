//snippet-sourcedescription:[PutEvents.java demonstrates how to put a sample CloudWatch event.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[cloudwatch.java.put_log_events.complete]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.cloudwatch;
// snippet-start:[cloudwatch.java.put_log_events.import]

import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;

import java.util.Arrays;
// snippet-end:[cloudwatch.java.put_log_events.import]

/**
 * Puts a sample CloudWatch event
 */
public class PutLogEvents {
    public static void main(String[] args) {

        final String usage =
                "To run this example, supply a log group and stream name\n" +
                "Ex: PutLogEvents <log-group-name> <stream-name>\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String logGroupName = args[0];
        String streamName = args[1];

        // snippet-start:[cloudwatch.java.put_log_events.main]
        CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder().build();

        // A sequence number is required to put a log event in an existing stream.
        // Look up the stream to find its sequence number.

        // First describe all streams in the log group.
        DescribeLogStreamsRequest logStreamRequest = DescribeLogStreamsRequest.builder()
                                                                              .logGroupName(logGroupName)
                                                                              .logStreamNamePrefix(streamName)
                                                                              .build();
        DescribeLogStreamsResponse describeLogStreamsResponse = logsClient.describeLogStreams(logStreamRequest);

        // Assume that a single stream is returned.
        String sequenceToken = describeLogStreamsResponse.logStreams().get(0).uploadSequenceToken();

        // Build an input log message
        InputLogEvent inputLogEvent = InputLogEvent.builder()
                                                   .message("{ \"key1\": \"value1\", \"key2\": \"value2\" }")
                                                   .timestamp(System.currentTimeMillis())
                                                   .build();

        PutLogEventsRequest request = PutLogEventsRequest.builder()
                                                         .logEvents(Arrays.asList(inputLogEvent))
                                                         .logGroupName("log-group-name")
                                                         .logStreamName("log-stream-name")
                                                         // Sequence token is required so that the log can be written to the latest location.
                                                         .sequenceToken(sequenceToken)
                                                         .build();
        logsClient.putLogEvents(request);
        // snippet-end:[cloudwatch.java.put_log_events.main]

        System.out.println("Successfully put CloudWatch event");
    }
}
// snippet-end:[cloudwatch.java.put_log_events.complete]