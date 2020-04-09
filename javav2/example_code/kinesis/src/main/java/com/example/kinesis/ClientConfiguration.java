//snippet-sourcedescription:[ClientConfiguration.java demonstrates how to configure an HTTP client in the Kinesis asynchronous client.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/9/2020]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
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
