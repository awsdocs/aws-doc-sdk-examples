//snippet-sourcedescription:[ListShards.java demonstrates how to list the shards in an Amazon Kinesis data stream.]
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

//snippet-start:[kinesis.java2.ListShards.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
//snippet-end:[kinesis.java2.ListShards.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListShards {

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
        // snippet-start:[kinesis.java2.ListShards.client]
        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
                    .region(region)
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .build();
           // snippet-end:[kinesis.java2.ListShards.client]

        listKinShards(kinesisClient, name);
        kinesisClient.close();
        }

      // snippet-start:[kinesis.java2.ListShards.main]
        public static void listKinShards(KinesisClient kinesisClient, String name) {

        try {
        ListShardsRequest request = ListShardsRequest.builder()
                .streamName(name)
                .build();

            ListShardsResponse response = kinesisClient.listShards(request);
            System.out.println(request.streamName() + " has " + response.shards());

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[kinesis.java2.ListShards.main]
}
