//snippet-sourcedescription:[GetLogEvents.java demonstrates how to get log events from CloudWatch in a specified region. ]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-06-20]
//snippet-sourceauthor:[ceruleancee]
// snippet-start:[cloudwatch.java2.get_logs.complete]
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

// snippet-start:[cloudwatch.java2.get_logs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;
// snippet-end:[cloudwatch.java2.get_logs.import]

/**
 * Gets logs events from CloudWatch
 */
public class GetLogEvents {

    public static void main(String[] args) {

        final String usage =
                "To run this example, supply a regionName (e.g. us-east-1), logGroupName, and streamName as command line arguments\n" +
                        "Ex: GetLogEvents <regionName> <logGroupName> <streamName>\n";

        if (args.length != 3) {
            System.out.print(usage);
            System.exit(1);
        }

        // snippet-start:[cloudwatch.java2.get_logs.main]
        String region = args[0];
        String logStreamName = args[1];
        String logGroupName = args[2];

        // Create a CloudWatchLogClient
        CloudWatchLogsClient cloudWatchLogsClient = CloudWatchLogsClient.builder()
                .region(Region.of(region))
                .build();

        // Designate logGroupName and logStream you want to get logs from
        // Assume only one stream name exist, this is not always the case
        GetLogEventsRequest getLogEventsRequest = GetLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .startFromHead(true)
                .build();

        int logLimit = cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().size();
        for (int c = 0; c < logLimit; c++) {
            // Prints the messages to the console
            System.out.println(cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().get(c).message());
        }
        System.out.println("Successfully got CloudWatch log events!");
        // snippet-end:[cloudwatch.java2.get_logs.main]
    }
}
// snippet-end:[cloudwatch.java2.get_logs.complete]