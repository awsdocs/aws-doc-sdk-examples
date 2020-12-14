//snippet-sourcedescription:[PublishTopic.java demonstrates how to publish an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.java2.PublishTopic.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.PublishTopic.import]

public class PublishTopic {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage: " +
                "PublishTopic <message> <topicArn>\n\n" +
                "Where:\n" +
                "  message - the message text to send.\n\n" +
                "  topicArn - the ARN of the topic to publish.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String message = args[0];
        String topicArn = args[1];
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        pubTopic(snsClient, message, topicArn);
        snsClient.close();
    }

    //snippet-start:[sns.java2.PublishTopic.main]
    public static void pubTopic(SnsClient snsClient, String message, String topicArn) {

        try {
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(topicArn)
                .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

         } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
              System.exit(1);
         }
        //snippet-end:[sns.java2.PublishTopic.main]
    }
}
