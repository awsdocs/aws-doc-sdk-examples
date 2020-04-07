//snippet-sourcedescription:[ListShards.java demonstrates how to list the shards in a Kinesis data stream.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3/26/2020
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

//snippet-start:[kinesis.java2.ListShards.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
//snippet-end:[kinesis.java2.ListShards.import]

public class ListShards {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListShards <streamName>\n\n" +
                "Where:\n" +
                "    streamName - The Kinesis data stream (i.e., StockTradeStream)\n\n" +
                "Example:\n" +
                "    ListShards StockTradeStream\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];

        // snippet-start:[kinesis.java2.ListShards.client]
        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
                    .region(region)
                    .build();
           // snippet-end:[kinesis.java2.ListShards.client]

        listKinShards(kinesisClient, name);
        }

      // snippet-start:[kinesis.java2.ListShards.main]
        public static void listKinShards(KinesisClient kinesisClient, String name) {

        try {
        ListShardsRequest request = ListShardsRequest.builder()
                .streamName(name)
                .build();

            ListShardsResponse response = kinesisClient.listShards(request);
            System.out.println(request.streamName() + " has " + response.shards());

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[kinesis.java2.ListShards.main]
}
