//snippet-sourcedescription:[ListDeliveryStreams.java demonstrates how to list all delivery streams.]
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
