//snippet-sourcedescription:[ConfirmSubscription.java demonstrates how to confirm a subscription for Amazon Simple Notification Service (Amazon SNS).]
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

//snippet-start:[sns.java2.ConfirmSubscription.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ConfirmSubscriptionRequest;
import software.amazon.awssdk.services.sns.model.ConfirmSubscriptionResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.ConfirmSubscription.import]

public class ConfirmSubscription {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "ConfirmSubscription <subscriptionToken> <topicArn>\n\n" +
                "Where:\n" +
                "  subscriptionToken - a short-lived token sent to an endpoint during the Subscribe action.\n\n" +
                "  topicArn - the ARN of the topic. \n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String subscriptionToken = args[0];
        String topicArn = args[1];

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        confirmSub(snsClient, subscriptionToken, topicArn ) ;
        snsClient.close();
    }

    //snippet-start:[sns.java2.ConfirmSubscription.main]
    public static void confirmSub(SnsClient snsClient, String subscriptionToken, String topicArn ) {

        try {
             ConfirmSubscriptionRequest request = ConfirmSubscriptionRequest.builder()
                .token(subscriptionToken)
                .topicArn(topicArn)
                .build();

            ConfirmSubscriptionResponse result = snsClient.confirmSubscription(request);

            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode() + "\n\nSubscription Arn: \n\n" + result.subscriptionArn());
    } catch (SnsException e) {

        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
        //snippet-end:[sns.java2.ConfirmSubscription.main]
    }
}
