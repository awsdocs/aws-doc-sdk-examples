//snippet-sourcedescription:[AddDataShards.java demonstrates how to increase shard count in an Amazon Kinesis data stream.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Kinesis]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kinesis;

//snippet-start:[kinesis.java2.AddDataShards.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.UpdateShardCountRequest;
import software.amazon.awssdk.services.kinesis.model.UpdateShardCountResponse;
//snippet-end:[kinesis.java2.AddDataShards.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AddDataShards {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <streamName>\n\n" +
            "Where:\n" +
            "    streamName - The Amazon Kinesis data stream (for example, StockTradeStream)\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String name = args[0];
        String inputShards = "2";
        int goalShards = Integer.parseInt(inputShards);

        // snippet-start:[kinesis.java2.AddDataShards.client]
        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();
        // snippet-end:[kinesis.java2.AddDataShards.client]

        addShards(kinesisClient, name, goalShards);
        kinesisClient.close();
        System.out.println("Done");
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

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kinesis.java2.AddDataShards.main]
}
