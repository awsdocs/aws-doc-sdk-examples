//snippet-sourcedescription:[DeleteStream.java demonstrates how to delete a delivery stream.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.firehose;

// snippet-start:[firehose.java2.delete_stream.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.services.firehose.model.DeleteDeliveryStreamRequest;
// snippet-end:[firehose.java2.delete_stream.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteStream {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <streamName> \n\n" +
                "Where:\n" +
                "    streamName - The data stream name to delete. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String streamName = args[0];
        Region region = Region.US_WEST_2;
        FirehoseClient firehoseClient = FirehoseClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        delStream(firehoseClient, streamName) ;
        firehoseClient.close();
    }

    // snippet-start:[firehose.java2.delete_stream.main]
    public static void delStream(FirehoseClient firehoseClient, String streamName) {

        try {
            DeleteDeliveryStreamRequest deleteDeliveryStreamRequest = DeleteDeliveryStreamRequest.builder()
                    .deliveryStreamName(streamName)
                    .build();

            firehoseClient.deleteDeliveryStream(deleteDeliveryStreamRequest);
            System.out.println("Delivery Stream "+streamName +" is deleted");

        } catch (FirehoseException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[firehose.java2.delete_stream.main]
}

