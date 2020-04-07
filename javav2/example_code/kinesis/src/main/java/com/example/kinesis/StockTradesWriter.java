//snippet-sourcedescription:[StockTradesWriter.java demonstrates how to write multiple data records into an Amazon Kinesis data stream.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3-26-2020]
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

//snippet-start:[kinesis.java2.putrecord.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
//snippet-end:[kinesis.java2.putrecord.import]

public class StockTradesWriter {

    public static void main(String[] args) {

       final String USAGE = "\n" +
                "Usage:\n" +
                "    StockTradesWriter <streamName>\n\n" +
                "Where:\n" +
                "    streamName - The Kinesis data stream to which records are written (i.e., StockTradeStream)\n\n" +
                "Example:\n" +
                "    StockTradesWriter streamName\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String streamName = args[0];


            Region region = Region.US_EAST_1;
            KinesisClient kinesisClient = KinesisClient.builder()
                    .region(region)
                    .build();

            //Ensure that the Kinesis Stream is valid
            validateStream(kinesisClient, streamName);
            setStockData( kinesisClient, streamName);
    }

        // snippet-start:[kinesis.java2.putrecord.main]
        public static void setStockData( KinesisClient kinesisClient, String streamName) {

            try {
            // Repeatedly send stock trades with a 100 milliseconds wait in between
            StockTradeGenerator stockTradeGenerator = new StockTradeGenerator();

            //Put in 50 Records for this example
            int index = 50;
            for (int x=0; x<index; x++){
                StockTrade trade = stockTradeGenerator.getRandomTrade();
                sendStockTrade(trade, kinesisClient, streamName);
                Thread.sleep(100);
             }

        } catch (KinesisException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }

    private static void sendStockTrade(StockTrade trade, KinesisClient kinesisClient,
                                       String streamName) {
        byte[] bytes = trade.toJsonAsBytes();
        // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
        if (bytes == null) {
            System.out.println("Could not get JSON bytes for stock trade");
            return;
        }

        System.out.println("Putting trade: " + trade.toString());
        PutRecordRequest request = PutRecordRequest.builder()
                .partitionKey(trade.getTickerSymbol()) // We use the ticker symbol as the partition key, explained in the Supplemental Information section below.
                .streamName(streamName)
                .data(SdkBytes.fromByteArray(bytes))
                .build();
        try {
            kinesisClient.putRecord(request);
        } catch (KinesisException e) {
            e.getMessage();
        }
    }

    private static void validateStream(KinesisClient kinesisClient, String streamName) {
        try {
            DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
                    .streamName(streamName)
                    .build();

            DescribeStreamResponse describeStreamResponse = kinesisClient.describeStream(describeStreamRequest);

            if(!describeStreamResponse.streamDescription().streamStatus().toString().equals("ACTIVE")) {
                System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
                System.exit(1);
            }
        }catch (KinesisException e) {
            System.err.println("Error found while describing the stream " + streamName);
            System.err.println(e);
            System.exit(1);
        }
        // snippet-end:[kinesis.java2.putrecord.main]
    }
}
