//snippet-sourcedescription:[ListDeliveryStreams.java demonstrates how to list all delivery streams.]
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

// snippet-start:[firehose.java2.list_streams.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.services.firehose.model.ListDeliveryStreamsResponse;
// snippet-end:[firehose.java2.list_streams.import]

import java.util.List;

public class ListDeliveryStreams {

    public static void main(String[] args) throws Exception {

        Region region = Region.US_WEST_2;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .build();

        listStreams(firehoseClient) ;
        firehoseClient.close();
    }

    // snippet-start:[firehose.java2.list_streams.main]
    public static void listStreams( FirehoseClient firehoseClient) {

        try {

            ListDeliveryStreamsResponse streamsResponse = firehoseClient.listDeliveryStreams();

            List<String> items = streamsResponse.deliveryStreamNames();
            for (String item: items) {
                System.out.println("The delivery stream name is: "+item);
            }

        } catch (FirehoseException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        // snippet-end:[firehose.java2.list_streams.main]
    }
}
