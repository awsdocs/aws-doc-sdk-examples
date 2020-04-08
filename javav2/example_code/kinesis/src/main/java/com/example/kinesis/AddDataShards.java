//snippet-sourcedescription:[AddDataShards.java demonstrates how to increase shard count in a Kinesis data stream.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-06-28]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

//snippet-start:[kinesis.java2.AddDataShards.complete]

package com.example.kinesis;
//snippet-start:[kinesis.java2.AddDataShards.import]

import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.UpdateShardCountRequest;
import software.amazon.awssdk.services.kinesis.model.UpdateShardCountResponse;
//snippet-end:[kinesis.java2.AddDataShards.import]

public class AddDataShards {

    public static void main(String[] args) {

        String name = args[0];
        String input_shards = args[1];
        int goal_shards = Integer.parseInt(input_shards);

        // snippet-start:[kinesis.java2.AddDataShards.client]
        KinesisAsyncClient client = KinesisAsyncClient.builder()
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .maxConcurrency(100)
                        .maxPendingConnectionAcquires(10_000))
                .build();

        // snippet-end:[kinesis.java2.AddDataShards.client]


        // snippet-start:[kinesis.java2.AddDataShards.main]
        UpdateShardCountRequest request = UpdateShardCountRequest.builder()
                .scalingType("UNIFORM_SCALING")
                .streamName(name)
                .targetShardCount(goal_shards)
                .build();

        UpdateShardCountResponse response = client.updateShardCount(request).join();


        System.out.println(response.streamName() + " has updated shard count to " + response.currentShardCount());
        // snippet-end:[kinesis.java2.AddDataShards.main]
    }
}
//snippet-end:[kinesis.java2.AddDataShards.complete]