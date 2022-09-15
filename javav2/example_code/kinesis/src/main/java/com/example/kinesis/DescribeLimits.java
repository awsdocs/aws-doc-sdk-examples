//snippet-sourcedescription:[DescribeLimits.java demonstrates how to display the shard limit and usage for a given account.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Kinesis]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kinesis;
//snippet-start:[kinesis.java2.DescribeLimits.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeLimitsRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeLimitsResponse;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
//snippet-end:[kinesis.java2.DescribeLimits.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeLimits {

    public static void main(String[] args) {
        // snippet-start:[kinesis.java2.DescribeLimits.client]
        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();
        // snippet-end:[kinesis.java2.DescribeLimits.client]

        describeKinLimits(kinesisClient);
        kinesisClient.close();
    }
    // snippet-start:[kinesis.java2.DescribeLimits.main]
    public static void describeKinLimits(KinesisClient kinesisClient) {
        try {
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
    }
    // snippet-end:[kinesis.java2.DescribeLimits.main]
}
