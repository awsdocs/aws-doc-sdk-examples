// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.firehose;

// snippet-start:[firehose.java2.create_stream.main]
// snippet-start:[firehose.java2.create_stream.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.*;

import java.util.concurrent.TimeUnit;
// snippet-end:[firehose.java2.create_stream.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateDeliveryStream {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <bucketARN> <roleARN> <streamName>\s

                Where:
                    bucketARN - The ARN of the Amazon S3 bucket where the data stream is written.\s
                    roleARN - The ARN of the IAM role that has the permissions that Kinesis Data Firehose needs.\s
                    streamName - The name of the delivery stream.\s
                """;

        if (args.length != 3) {
            System.out.println(usage);
            return;
        }

        String bucketARN = args[0];
        String roleARN = args[1];
        String streamName = args[2];
        Region region = Region.US_WEST_2;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .build();

        createStream(firehoseClient, bucketARN, roleARN, streamName);
        firehoseClient.close();
    }

    public static void createStream(FirehoseClient firehoseClient, String bucketARN, String roleARN,
            String streamName) {
        try {
            ExtendedS3DestinationConfiguration destinationConfiguration = ExtendedS3DestinationConfiguration.builder()
                    .bucketARN(bucketARN)
                    .roleARN(roleARN)
                    .build();

            CreateDeliveryStreamRequest deliveryStreamRequest = CreateDeliveryStreamRequest.builder()
                    .deliveryStreamName(streamName)
                    .extendedS3DestinationConfiguration(destinationConfiguration)
                    .deliveryStreamType("DirectPut")
                    .build();

            CreateDeliveryStreamResponse streamResponse = firehoseClient.createDeliveryStream(deliveryStreamRequest);
            System.out.println("Delivery Stream ARN is " + streamResponse.deliveryStreamARN());

        } catch (FirehoseException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static void waitForStreamToBecomeActive(FirehoseClient firehoseClient, String streamName) {
        System.out.println("Waiting for the stream to become ACTIVE...");
        int maxAttempts = 60; // 10 minutes (60 * 10 seconds)
        int attempt = 0;
        while (attempt < maxAttempts) {
            try {
                DescribeDeliveryStreamRequest describeRequest = DescribeDeliveryStreamRequest.builder()
                        .deliveryStreamName(streamName)
                        .build();
                DescribeDeliveryStreamResponse describeResponse = firehoseClient.describeDeliveryStream(describeRequest);
                String status = describeResponse.deliveryStreamDescription().deliveryStreamStatusAsString();

                System.out.println("Current status: " + status);
                if (status.equals("ACTIVE")) {
                    System.out.println("Stream is now ACTIVE.");
                    return;
                }

                TimeUnit.SECONDS.sleep(10);
                attempt++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            } catch (FirehoseException e) {
                System.err.println("Error while checking stream status: " + e.getMessage());
            }
        }
        System.err.println("Timed out waiting for the stream to become ACTIVE.");
        System.exit(1);
    }
}
// snippet-end:[firehose.java2.create_stream.main]
