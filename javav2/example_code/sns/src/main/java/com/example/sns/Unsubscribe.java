//snippet-sourcedescription:[Unsubscribe.java demonstrates how to remove an Amazon Simple Notification Service (Amazon SNS) subscription.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

//snippet-start:[sns.java2.Unsubscribe.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sns.model.UnsubscribeResponse;
//snippet-end:[sns.java2.Unsubscribe.import]

public class Unsubscribe {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage: " +
                "Unsubscribe <subscriptionArn>\n\n" +
                "Where:\n" +
                "  subscriptionArn - the ARN of the subscription to delete.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String subscriptionArn = args[0];
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        unSub(snsClient, subscriptionArn);
        snsClient.close();
    }

    //snippet-start:[sns.java2.Unsubscribe.main]
    public static void unSub(SnsClient snsClient, String subscriptionArn) {

        try {
            UnsubscribeRequest request = UnsubscribeRequest.builder()
                .subscriptionArn(subscriptionArn)
                .build();

            UnsubscribeResponse result = snsClient.unsubscribe(request);

            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode()
                + "\n\nSubscription was removed for " + request.subscriptionArn());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[sns.java2.Unsubscribe.main]
    }
}
