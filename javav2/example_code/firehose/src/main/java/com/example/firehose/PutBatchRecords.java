//snippet-sourcedescription:[PutBatchRecords.java demonstrates how to write multiple data records into a delivery stream.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/6/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/


package com.example.firehose;

// snippet-start:[firehose.java2.put_batch_records.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.Record;
import software.amazon.awssdk.services.firehose.model.PutRecordBatchRequest;
import software.amazon.awssdk.services.firehose.model.PutRecordBatchResponse;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.core.SdkBytes;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[firehose.java2.put_batch_records.import]


public class PutBatchRecords {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutBatchRecords <streamName> \n\n" +
                "Where:\n" +
                "    streamName - The delivery stream name \n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String streamName = args[0];

        Region region = Region.US_WEST_2;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .build();

        addStockTradeData(firehoseClient, streamName);
    }

    // snippet-start:[firehose.java2.put_batch_records.main]
    public static void addStockTradeData(FirehoseClient firehoseClient, String streamName) {

        List<Record> recordList = new ArrayList<>();

        try {

            // Repeatedly send stock trades with a 100 milliseconds wait in between
            StockTradeGenerator stockTradeGenerator = new StockTradeGenerator();
            int index = 100;

            // Populate the list with StockTrade data
            for (int x=0; x<index; x++){
                StockTrade trade = stockTradeGenerator.getRandomTrade();
                byte[] bytes = trade.toJsonAsBytes();

                Record myRecord = Record.builder()
                        .data(SdkBytes.fromByteArray(bytes))
                        .build();

                System.out.println("Adding trade: " + trade.toString());
                recordList.add(myRecord);
                Thread.sleep(100);
            }

            PutRecordBatchRequest recordBatchRequest = PutRecordBatchRequest.builder()
                    .deliveryStreamName(streamName)
                    .records(recordList)
                    .build();

            PutRecordBatchResponse recordResponse = firehoseClient.putRecordBatch(recordBatchRequest) ;
            System.out.println("The number of records added is: "+recordResponse.requestResponses().size());

        } catch (FirehoseException | InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        // snippet-end:[firehose.java2.put_batch_records.main]
    }
}
