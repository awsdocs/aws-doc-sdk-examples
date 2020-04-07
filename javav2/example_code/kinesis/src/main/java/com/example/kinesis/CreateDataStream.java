//snippet-sourcedescription:[CreateDataStream.java demonstrates how to create an Amazon Kinesis data stream.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3/26/2020]
//snippet-sourceauthor:[scmacdon AWS]
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

package com.example.kinesis;

//snippet-start:[kinesis.java2.create.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
//snippet-end:[kinesis.java2.create.import]

public class CreateDataStream {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateDataStream <streamName>\n\n" +
                "Where:\n" +
                "    CreateDataStream - The Kinesis data stream (i.e., StockTradeStream)\n\n" +
                "Example:\n" +
                "    CreateDataStream StockTradeStream\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String streamName = args[0];

        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
                .region(region)
                .build();

        createStream(kinesisClient, streamName);
    }

    // snippet-start:[kinesis.java2.create.main]
    public static void createStream(KinesisClient kinesisClient, String streamName) {

        try {
            CreateStreamRequest streamReq = CreateStreamRequest.builder()
                    .streamName(streamName)
                    .shardCount(1)
                    .build();

            kinesisClient.createStream(streamReq);
        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[kinesis.java2.create.main]
}
