// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudtrail;

// snippet-start:[cloudtrail.java2.logging.main]
// snippet-start:[cloudtrail.java2.logging.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.StartLoggingRequest;
import software.amazon.awssdk.services.cloudtrail.model.StopLoggingRequest;
// snippet-end:[cloudtrail.java2.logging.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class StartLogging {
    public static void main(String[] args) {

        final String usage = """

                Usage:
                    <trailName>\s

                Where:
                    trailName - The name of the trail.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String trailName = args[0];
        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClient = CloudTrailClient.builder()
                .region(region)
                .build();

        startLog(cloudTrailClient, trailName);
        stopLog(cloudTrailClient, trailName);
        cloudTrailClient.close();
    }

    public static void startLog(CloudTrailClient cloudTrailClientClient, String trailName) {
        try {
            StopLoggingRequest loggingRequest = StopLoggingRequest.builder()
                    .name(trailName)
                    .build();

            cloudTrailClientClient.stopLogging(loggingRequest);
            System.out.println(trailName + " has stopped logging");

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void stopLog(CloudTrailClient cloudTrailClientClient, String trailName) {
        try {
            StartLoggingRequest loggingRequest = StartLoggingRequest.builder()
                    .name(trailName)
                    .build();

            cloudTrailClientClient.startLogging(loggingRequest);
            System.out.println(trailName + " has started logging");

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[cloudtrail.java2.logging.main]
