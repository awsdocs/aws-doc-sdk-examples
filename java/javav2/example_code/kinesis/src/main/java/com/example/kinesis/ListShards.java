//snippet-sourcedescription:[ListShards.java demonstrates how to list the shards in a Kinesis data stream.]
//snippet-keyword:[Java]
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

//snippet-start:[kinesis.java2.ListShards.complete]

package com.example.kinesis;
//snippet-start:[kinesis.java2.ListShards.import]

import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
//snippet-end:[kinesis.java2.ListShards.import]

public class ListShards {

    public static void main(String[] args) {

        String name = args[0];

        // snippet-start:[kinesis.java2.ListShards.client]
        KinesisAsyncClient client = KinesisAsyncClient.builder()
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .maxConcurrency(100)
                        .maxPendingConnectionAcquires(10_000))
                .build();

        // snippet-end:[kinesis.java2.ListShards.client]


        // snippet-start:[kinesis.java2.ListShards.main]
        ListShardsRequest request = ListShardsRequest.builder()
                .streamName(name)
                .build();

        ListShardsResponse response = client.listShards(request).join();


        System.out.println(request.streamName() + " has " + response.shards());
        // snippet-end:[kinesis.java2.ListShards.main]
    }
}
//snippet-end:[kinesis.java2.ListShards.complete]