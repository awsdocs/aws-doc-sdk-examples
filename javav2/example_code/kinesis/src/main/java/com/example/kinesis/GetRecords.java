//snippet-sourcedescription:[GetRecords.java demonstrates how to read multiple data records from an Amazon Kinesis data stream.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3/26/2020]
//snippet-sourceauthor:scmacdon - AWS]

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

//snippet-start:[kinesis.java2.getrecord.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import java.util.ArrayList;
import java.util.List;
//snippet-end:[kinesis.java2.getrecord.import]

/**
 * Demonstrates how to read data from a Kinesis data stream. Before running this Java code example, populate a data stream
 * by running the StockTradesWriter example. Then you can use that populated data stream for this example.
 */

public class GetRecords {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetRecords <streamName>\n\n" +
                "Where:\n" +
                "    streamName - The Kinesis data stream to read from (i.e., StockTradeStream)\n\n" +
                "Example:\n" +
                "    GetRecords streamName\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String streamName = args[0];


        Region region = Region.US_EAST_1;
        KinesisClient kinesisClient = KinesisClient.builder()
                .region(region)
                .build();

        getStockTrades(kinesisClient,streamName);
    }

    // snippet-start:[kinesis.java2.getrecord.main]
    public static void getStockTrades(KinesisClient kinesisClient, String streamName) {

            String shardIterator;
            String lastShardId = null;

            // Retrieve the shards from a data stream
            DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
                    .streamName(streamName)
                    .build();
            List<Shard> shards = new ArrayList<>();

            DescribeStreamResponse streamRes;
            do {
               // describeStreamRequest.exclusiveStartShardId(lastShardId);
                streamRes = kinesisClient.describeStream(describeStreamRequest);
                shards.addAll(streamRes.streamDescription().shards());

                if (shards.size() > 0) {
                    lastShardId = shards.get(shards.size() - 1).shardId();
                }
            } while (streamRes.streamDescription().hasMoreShards());

            GetShardIteratorRequest itReq = GetShardIteratorRequest.builder()
                    .streamName(streamName)
                    .shardIteratorType("TRIM_HORIZON")
                    .shardId(shards.get(0).shardId())
                    .build();

            GetShardIteratorResponse shardIteratorResult = kinesisClient.getShardIterator(itReq);
            shardIterator = shardIteratorResult.shardIterator();

            // Continuously read data records from a shard
            List<Record> records;

            // Create a GetRecordsRequest with the existing shardIterator,
            // and set maximum records to return to 1000
            GetRecordsRequest recordsRequest = GetRecordsRequest.builder()
                     .shardIterator(shardIterator)
                     .limit(1000)
                     .build();

           GetRecordsResponse result = kinesisClient.getRecords(recordsRequest);

           // Put result into a record list, result might be empty
           records = result.records();

            // Print records
            for (Record record : records) {
                SdkBytes byteBuffer = record.data();
                System.out.println(String.format("Seq No: %s - %s", record.sequenceNumber(),
                 new String(byteBuffer.asByteArray())));
             }

             }
        // snippet-end:[kinesis.java2.getrecord.main]
  }

