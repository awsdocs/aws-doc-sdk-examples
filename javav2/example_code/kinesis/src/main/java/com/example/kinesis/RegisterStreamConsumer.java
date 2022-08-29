//snippet-sourcedescription:[ListShards.java demonstrates how to register a consumer with a Kinesis data stream.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Kinesis]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kinesis;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.RegisterStreamConsumerRequest;
import software.amazon.awssdk.services.kinesis.model.RegisterStreamConsumerResponse;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class RegisterStreamConsumer {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <streamARN>\n\n" +
            "Where:\n" +
            "    streamARN - The Amazon Kinesis data stream (for example, arn:aws:kinesis:us-east-1:814548xxxxxx:stream/LamDataStream)\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String streamARN = args[0];
        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

       String arnValue = regConsumer(kinesisClient, streamARN);
       System.out.println(arnValue);
       kinesisClient.close();
    }

    public static String regConsumer(KinesisClient kinesisClient, String streamARN) {

        try {
            RegisterStreamConsumerRequest regCon = RegisterStreamConsumerRequest.builder()
                .consumerName("MyConsumer")
                .streamARN(streamARN)
                .build();

            RegisterStreamConsumerResponse resp = kinesisClient.registerStreamConsumer(regCon);
            return resp.consumer().consumerARN();

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
}
