//snippet-sourcedescription:[GetLogEvents.java demonstrates how to get log events from Amazon CloudWatch.]
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

// snippet-start:[cloudwatch.java2.get_logs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;
// snippet-end:[cloudwatch.java2.get_logs.import]

public class GetLogEvents {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "  GetLogEvents <logStreamName> <logGroupName>\n\n" +
                "Where:\n" +
                "  logStreamName - the name of the log stream (for example, mystream).\n" +
                "  logGroupName - the name of the log group (for example, myloggroup).\n" ;

        if (args.length != 2) {
            System.out.print(USAGE);
            System.exit(1);
        }

        String logStreamName = args[0];
        String logGroupName = args[1];

        Region region = Region.US_WEST_2;
        CloudWatchLogsClient cloudWatchLogsClient = CloudWatchLogsClient.builder()
                .region(region)
                .build();

        getCWLogEvebts(cloudWatchLogsClient, logGroupName, logStreamName) ;
        cloudWatchLogsClient.close();
    }

    // snippet-start:[cloudwatch.java2.get_logs.main]
    public static void getCWLogEvebts(CloudWatchLogsClient cloudWatchLogsClient, String logGroupName, String logStreamName) {

        try {

             GetLogEventsRequest getLogEventsRequest = GetLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .startFromHead(true)
                .build();

            int logLimit = cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().size();
            for (int c = 0; c < logLimit; c++) {
                 System.out.println(cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().get(c).message());
            }

            System.out.println("Successfully got CloudWatch log events!");
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        // snippet-end:[cloudwatch.java2.get_logs.main]
    }
}
