//snippet-sourcedescription:[GetLogEvents.java demonstrates how to get log events from CloudWatch in a specified region. ]
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

// snippet-start:[cloudwatch.java2.get_logs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;
// snippet-end:[cloudwatch.java2.get_logs.import]

/**
 * Gets logs events from CloudWatch
 */
public class GetLogEvents {

    public static void main(String[] args) {

        final String usage =
                "To run this example, supply a logGroupName, and streamName as command line arguments\n" +
                        "Ex: GetLogEvents <logGroupName> <streamName>\n";

        if (args.length != 2) {
            System.out.print(usage);
            System.exit(1);
        }

        String logStreamName = args[0];
        String logGroupName = args[1];

        // Create a CloudWatchLogClient
        Region region = Region.US_WEST_2;
        CloudWatchLogsClient cloudWatchLogsClient = CloudWatchLogsClient.builder()
                .region(region)
                .build();

        getCWLogEvebts(cloudWatchLogsClient, logGroupName, logStreamName) ;
    }

    // snippet-start:[cloudwatch.java2.get_logs.main]
    public static void getCWLogEvebts(CloudWatchLogsClient cloudWatchLogsClient, String logGroupName, String logStreamName) {


        try {

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
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Successfully got CloudWatch log events!");
        // snippet-end:[cloudwatch.java2.get_logs.main]
    }
}
