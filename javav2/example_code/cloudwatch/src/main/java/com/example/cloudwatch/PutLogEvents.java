//snippet-sourcedescription:[PutEvents.java demonstrates how to put a sample CloudWatch event.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-start:[cloudwatch.java2.put_log_events.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;
import java.util.Arrays;
// snippet-end:[cloudwatch.java2.put_log_events.import]

/**
 * Puts a sample CloudWatch log event
 */
public class PutLogEvents {
    public static void main(String[] args) {

        final String usage =
                "To run this example, supply a region id (eg. us-east-1), log group, and stream name as command line arguments\n" +
                        "Ex: PutLogEvents <region-id> <log-group-name> <stream-name>\n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String regionId = args[0];
        String logGroupName = args[1];
        String streamName = args[2];


        CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder()
            .region(Region.of(regionId))
            .build();

        putCWLogEvents(logsClient, regionId, logGroupName, streamName) ;

    }

    // snippet-start:[cloudwatch.java2.put_log_events.main]
    public static void putCWLogEvents(CloudWatchLogsClient logsClient, String regionId, String logGroupName, String streamName) {


        // A sequence token is required to put a log event in an existing stream.
        // Look up the stream to find its sequence token.

        // First describe all streams in the log group.
        DescribeLogStreamsRequest logStreamRequest = DescribeLogStreamsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamNamePrefix(streamName)
                .build();
        DescribeLogStreamsResponse describeLogStreamsResponse = logsClient.describeLogStreams(logStreamRequest);

        // Assume that a single stream is returned since a specific stream name was specified in the previous request.
        String sequenceToken = describeLogStreamsResponse.logStreams().get(0).uploadSequenceToken();

        // Build an input log message to put to CloudWatch.
        InputLogEvent inputLogEvent = InputLogEvent.builder()
                .message("{ \"key1\": \"value1\", \"key2\": \"value2\" }")
                .timestamp(System.currentTimeMillis())
                .build();

        // Specify the request parameters.
        PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
                .logEvents(Arrays.asList(inputLogEvent))
                .logGroupName(logGroupName)
                .logStreamName(streamName)
                // Sequence token is required so that the log can be written to the
                // latest location in the stream.
                .sequenceToken(sequenceToken)
                .build();
        logsClient.putLogEvents(putLogEventsRequest);
        // snippet-end:[cloudwatch.java2.put_log_events.main]

        System.out.println("Successfully put CloudWatch log event");
    }
}
