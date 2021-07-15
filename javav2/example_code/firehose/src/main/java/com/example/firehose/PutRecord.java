//snippet-sourcedescription:[PutRecord.java demonstrates how to write a data record into a delivery stream.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.firehose;

// snippet-start:[firehose.java2.put_record.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.services.firehose.model.PutRecordRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.firehose.model.Record;
import software.amazon.awssdk.services.firehose.model.PutRecordResponse;
// snippet-end:[firehose.java2.put_record.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutRecord {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutRecord <textValue> <streamName> \n\n" +
                "Where:\n" +
                "    textValue - the text used as the data to write to the data stream. \n\n" +
                "    streamName - the data stream name. \n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String textValue = args[0];
        String streamName = args[1];

        Region region = Region.US_WEST_2;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .build();

        putSingleRecord(firehoseClient, textValue, streamName) ;
        firehoseClient.close();
  }

    // snippet-start:[firehose.java2.put_record.main]
    public static void putSingleRecord( FirehoseClient firehoseClient, String textValue, String streamName) {

      try {

        SdkBytes sdkBytes = SdkBytes.fromByteArray(textValue.getBytes());

        Record record = Record.builder()
                .data(sdkBytes)
                .build();

        PutRecordRequest recordRequest = PutRecordRequest.builder()
                .deliveryStreamName(streamName)
                .record(record)
                .build();

        PutRecordResponse recordResponse = firehoseClient.putRecord(recordRequest) ;
        System.out.println("The record ID is "+recordResponse.recordId());

    } catch (FirehoseException e) {
        System.out.println(e.getLocalizedMessage());
        System.exit(1);
    }
   // snippet-end:[firehose.java2.put_record.main]
  }
}
