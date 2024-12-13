// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.firehose;

// snippet-start:[firehose.java2.list_streams.main]
// snippet-start:[firehose.java2.list_streams.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.services.firehose.model.ListDeliveryStreamsResponse;
import java.util.List;
// snippet-end:[firehose.java2.list_streams.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDeliveryStreams {

    public static void main(String[] args) throws Exception {
        Region region = Region.US_EAST_1;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .build();

        listStreams(firehoseClient);
        firehoseClient.close();
    }

    public static void listStreams(FirehoseClient firehoseClient) {
        try {
            ListDeliveryStreamsResponse streamsResponse = firehoseClient.listDeliveryStreams();
            List<String> items = streamsResponse.deliveryStreamNames();
            for (String item : items) {
                System.out.println("The delivery stream name is: " + item);
            }

        } catch (FirehoseException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
// snippet-end:[firehose.java2.list_streams.main]
