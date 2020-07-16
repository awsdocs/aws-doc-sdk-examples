//snippet-sourcedescription:[CreateDeliveryStream.java demonstrates how to create a delivery stream.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/6/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
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

public class CreateDeliveryStream {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateDeliveryStream <bucketARN> <roleARN> <streamName> \n\n" +
                "Where:\n" +
                "    bucketARN - The Amazon Resource Name (ARN) of the bucket where the delivery stream is written \n\n" +
                "    roleARN - The ARN of the role that has the permissions that Amazon Kinesis Data Firehose needs \n" +
                "    streamName - The delivery stream name \n";

        if (args.length < 3) {
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

            System.out.println("Delivery stream ARN is "+streamResponse.deliveryStreamARN());

    } catch (FirehoseException e) {
        System.out.println(e.getLocalizedMessage());
        System.exit(1);
    }
        // snippet-end:[firehose.java2.create_stream.main]
  }
}
