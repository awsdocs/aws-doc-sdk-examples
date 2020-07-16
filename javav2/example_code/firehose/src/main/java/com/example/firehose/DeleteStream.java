//snippet-sourcedescription:[DeleteStream.java demonstrates how to delete a delivery stream.]
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

// snippet-start:[firehose.java2.delete_stream.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.services.firehose.model.DeleteDeliveryStreamRequest;
// snippet-end:[firehose.java2.delete_stream.import]

public class DeleteStream {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteStream <streamName> \n\n" +
                "Where:\n" +
                "    streamName - The delivery stream name \n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

       String streamName = args[0];

        Region region = Region.US_WEST_2;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .build();

        delStream(firehoseClient, streamName) ;
    }

    // snippet-start:[firehose.java2.delete_stream.main]
    public static void delStream(FirehoseClient firehoseClient, String streamName) {

        try {
            DeleteDeliveryStreamRequest deleteDeliveryStreamRequest = DeleteDeliveryStreamRequest.builder()
                    .deliveryStreamName(streamName)
                    .build();

             firehoseClient.deleteDeliveryStream(deleteDeliveryStreamRequest);
            System.out.println("Delivery stream "+streamName +" is deleted");

        } catch (FirehoseException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        // snippet-end:[firehose.java2.delete_stream.main]
    }
}
