package com.example.cloudwatch;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;

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
    }
}
