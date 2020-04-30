//snippet-sourcedescription:[DescribeLimits.java demonstrates how to display the shard limit and usage for a given AWS account.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:3/26/2020]
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
//snippet-start:[kinesis.java2.DescribeLimits.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeLimitsRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeLimitsResponse;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
//snippet-end:[kinesis.java2.DescribeLimits.import]

public class DescribeLimits {

    public static void main(String[] args) {
        // snippet-start:[kinesis.java2.DescribeLimits.client]
        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
                .region(region)
                .build();

        describeKinLimits(kinesisClient);
    }

    public static void describeKinLimits(KinesisClient kinesisClient) {

        // snippet-end:[kinesis.java2.DescribeLimits.client]
        try {
        // snippet-start:[kinesis.java2.DescribeLimits.main]
        DescribeLimitsRequest request = DescribeLimitsRequest.builder()
                 .build();

        DescribeLimitsResponse response = kinesisClient.describeLimits(request);

        System.out.println("Number of open shards: " + response.openShardCount());
        System.out.println("Maximum shards allowed: " + response.shardLimit());
        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
        // snippet-end:[kinesis.java2.DescribeLimits.main]
    }
}
