//snippet-sourcedescription:[SubscribeLambda.java demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) lambda function.]
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

//snippet-start:[sns.java2.SubscribeLambda.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
//snippet-end:[sns.java2.SubscribeLambda.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class SubscribeLambda {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage: " +
                "SubscribeLambda <topicArn> <lambdaArn>\n\n" +
                "Where:\n" +
                "  topicArn - the ARN of the topic to subscribe.\n\n" +
                "  lambdaArn - the ARN of an AWS Lambda function.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String topicArn = args[0];
        String lambdaArn = args[1];

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        String arnValue = subLambda(snsClient, topicArn, lambdaArn) ;
        System.out.println("Subscription ARN: " + arnValue);
        snsClient.close();
    }

    //snippet-start:[sns.java2.SubscribeLambda.main]
    public static String subLambda(SnsClient snsClient, String topicArn, String lambdaArn) {

        try {

            SubscribeRequest request = SubscribeRequest.builder()
                .protocol("lambda")
                .endpoint(lambdaArn)
                .returnSubscriptionArn(true)
                .topicArn(topicArn)
                .build();

            SubscribeResponse result = snsClient.subscribe(request);

            return result.subscriptionArn();


         } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
        }
        return "";
     //snippet-end:[sns.java2.SubscribeLambda.main]
    }
}
