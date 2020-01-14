//snippet-sourcedescription:[DescribeLimits.java demonstrates how to  display the shard limit and usage for a given account .]
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
//snippet-start:[kinesis.java2.DescribeLimits.complete]

package com.example.kinesis;
//snippet-start:[kinesis.java2.DescribeLimits.import]

import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.DescribeLimitsRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeLimitsResponse;

import java.util.concurrent.ExecutionException;
//snippet-end:[kinesis.java2.DescribeLimits.import]

public class DescribeLimits {

    public static void main(String[] args) {
        // snippet-start:[kinesis.java2.DescribeLimits.client]
        KinesisAsyncClient client = KinesisAsyncClient.builder()
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .maxConcurrency(100)
                        .maxPendingConnectionAcquires(10_000))
                .build();

        // snippet-end:[kinesis.java2.DescribeLimits.client]


        // snippet-start:[kinesis.java2.DescribeLimits.main]
        DescribeLimitsRequest request = DescribeLimitsRequest.builder()
                .build();


        DescribeLimitsResponse response = client.describeLimits(request).join();

        System.out.println("Number of open shards: " + response.openShardCount());
        System.out.println("Maximum shards allowed: " + response.shardLimit());

        // snippet-end:[kinesis.java2.DescribeLimits.main]
    }

}
//snippet-end:[kinesis.java2.DescribeLimits.complete]