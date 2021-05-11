//snippet-sourcedescription:[CreateDeliveryStream.java demonstrates how to create a delivery stream.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.firehose;

// snippet-start:[firehose.java2.create_stream.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.services.firehose.model.CreateDeliveryStreamRequest;
import software.amazon.awssdk.services.firehose.model.ExtendedS3DestinationConfiguration;
import software.amazon.awssdk.services.firehose.model.CreateDeliveryStreamResponse;
// snippet-end:[firehose.java2.create_stream.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateDeliveryStream {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateDeliveryStream <bucketARN> <roleARN> <streamName> \n\n" +
                "Where:\n" +
                "    bucketARN - the ARN of the Amazon S3 bucket where the data stream is written. \n\n" +
                "    roleARN - the ARN of the IAM role that has the permissions that Kinesis Data Firehose needs. \n" +
                "    streamName - the name of the delivery stream. \n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketARN = args[0];
        String roleARN = args[1];
        String streamName = args[2];

        Region region = Region.US_WEST_2;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .build();

        createStream(firehoseClient, bucketARN, roleARN, streamName) ;
        firehoseClient.close();
    }

    // snippet-start:[firehose.java2.create_stream.main]
    public static void createStream(FirehoseClient firehoseClient, String bucketARN, String roleARN, String streamName) {

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

            System.out.println("Delivery Stream ARN is "+streamResponse.deliveryStreamARN());

    } catch (FirehoseException e) {
        System.out.println(e.getLocalizedMessage());
        System.exit(1);
    }
        // snippet-end:[firehose.java2.create_stream.main]
  }
}
