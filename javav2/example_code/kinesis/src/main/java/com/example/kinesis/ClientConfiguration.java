//snippet-sourcedescription:[ClientConfiguration.java demonstrates how to create an Amazon Kinesis asynchronous client.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.kinesis;
// snippet-start:[kinesis.java2.client_configuration.complete]
// snippet-start:[kinesis.java2.client_configuration.import]
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
// snippet-end:[kinesis.java2.client_configuration.import]




// snippet-start:[kinesis.java2.client_configuration.main]
public class ClientConfiguration {

    public static void main(String[] args) {
        // If configured with an httpClientBuilder, the SDK will manage the lifecycle of the HTTP client
        // and it will be shutdown when the client is shut down.
        // snippet-start:[kinesis.java2.client_configuration.client]
        KinesisAsyncClient client = KinesisAsyncClient.builder()
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .maxConcurrency(100)
                        .maxPendingConnectionAcquires(10_000))
                .build();

        // snippet-end:[kinesis.java2.client_configuration.client]
        // When passing in the httpClient directly, the lifecycle must be managed by the caller and the HTTP client
        // will not be shut down when the client is shut down.
        // snippet-start:[kinesis.java2.client_configuration.httpclient]
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .maxPendingConnectionAcquires(10_000)
                .build();

        KinesisAsyncClient kinesisClient = KinesisAsyncClient.builder()
                .httpClient(httpClient)
                .build();

        httpClient.close();
        // snippet-end:[kinesis.java2.client_configuration.httpclient]
    }

}

// snippet-end:[kinesis.java2.client_configuration.main]
// snippet-end:[kinesis.java2.client_configuration.complete]