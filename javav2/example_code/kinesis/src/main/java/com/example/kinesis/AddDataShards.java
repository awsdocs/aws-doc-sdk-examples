//snippet-sourcedescription:[AddDataShards.java demonstrates how to increase shard count in a Kinesis data stream.]
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

//snippet-start:[kinesis.java2.AddDataShards.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.UpdateShardCountRequest;
import software.amazon.awssdk.services.kinesis.model.UpdateShardCountResponse;
//snippet-end:[kinesis.java2.AddDataShards.import]

public class AddDataShards {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    AddDataShards <streamName>\n\n" +
                "Where:\n" +
                "    streamName - The Kinesis data stream (i.e., StockTradeStream)\n\n" +
                "Example:\n" +
                "    AddDataShards StockTradeStream\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];
        String inputShards = "2";

        int goalShards = Integer.parseInt(inputShards);


        // snippet-start:[kinesis.java2.AddDataShards.client]
        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
                .region(region)
                .build();
        // snippet-end:[kinesis.java2.AddDataShards.client]

        addShards(kinesisClient, name, goalShards);
    }

    // snippet-start:[kinesis.java2.AddDataShards.main]
    public static void addShards(KinesisClient kinesisClient, String name , int goalShards) {

        try {
             UpdateShardCountRequest request = UpdateShardCountRequest.builder()
                .scalingType("UNIFORM_SCALING")
                .streamName(name)
                .targetShardCount(goalShards)
                .build();

            UpdateShardCountResponse response = kinesisClient.updateShardCount(request);
            System.out.println(response.streamName() + " has updated shard count to " + response.currentShardCount());
            // snippet-end:[kinesis.java2.AddDataShards.main]
        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
}
