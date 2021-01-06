//snippet-sourcedescription:[SubscribeHTTPS.java demonstrates how to demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) HTTPs endpoint.]
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

//snippet-start:[sns.java2.SubscribeHTTPS.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
//snippet-end:[sns.java2.SubscribeHTTPS.import]

public class SubscribeHTTPS {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage: " +
                "SubscribeHTTPS <topicArn> <url>\n\n" +
                "Where:\n" +
                "  topicArn - the ARN of the topic to subscribe.\n\n" +
                "  url - the HTTPS endpoint that you want to receive notifications.\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String topicArn = args[0];
        String url = args[1];

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        subHTTPS(snsClient, topicArn, url) ;
        snsClient.close();
    }

    //snippet-start:[sns.java2.SubscribeHTTPS.main]
    public static void subHTTPS(SnsClient snsClient, String topicArn, String url ) {

        try {
            SubscribeRequest request = SubscribeRequest.builder()
                .protocol("http")
                .endpoint(url)
                .returnSubscriptionArn(true)
                .topicArn(topicArn)
                .build();

            SubscribeResponse result = snsClient.subscribe(request);
            System.out.println("Subscription ARN is " + result.subscriptionArn() + "\n\n Status is " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
         }
        //snippet-end:[sns.java2.SubscribeHTTPS.main]
    }
}
