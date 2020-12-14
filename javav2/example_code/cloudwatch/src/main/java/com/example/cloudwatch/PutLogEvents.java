//snippet-sourcedescription:[PutEvents.java demonstrates how to put a sample CloudWatch event.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.put_log_events.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;
import java.util.Arrays;
// snippet-end:[cloudwatch.java2.put_log_events.import]

public class PutLogEvents {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "  PutLogEvents <logGroupName> <streamName>\n\n" +
                "Where:\n" +
                "  logGroupName - a log group name.\n" +
                "  streamName - a stream name .\n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String logGroupName = args[1];
        String streamName = args[2];

        CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder()
                .build();

        putCWLogEvents(logsClient, logGroupName, streamName) ;
        logsClient.close();
    }

    // snippet-start:[cloudwatch.java2.put_log_events.main]
    public static void putCWLogEvents(CloudWatchLogsClient logsClient, String logGroupName, String streamName) {

     try {
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
        // Sequence token is required so that the log can be written to the
        // latest location in the stream.
        PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
                .logEvents(Arrays.asList(inputLogEvent))
                .logGroupName(logGroupName)
                .logStreamName(streamName)
                .sequenceToken(sequenceToken)
                .build();

        logsClient.putLogEvents(putLogEventsRequest);
        // snippet-end:[cloudwatch.java2.put_log_events.main]

        System.out.println("Successfully put CloudWatch log event");

     } catch (CloudWatchException e) {
         System.err.println(e.awsErrorDetails().errorMessage());
         System.exit(1);
     }
    }
}
