//snippet-sourcedescription:[DescribeLimits.java demonstrates how to display the shard limit and usage for a given account.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
        kinesisClient.close();
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
