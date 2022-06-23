//snippet-sourcedescription:[PublishTopic.java demonstrates how to publish an Amazon Simple Notification Service (Amazon SNS) topic.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

//snippet-start:[sns.java2.PublishTopic.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.PublishTopic.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PublishTopic {

     public static void main(String[] args) {

         final String usage = "\n" +
                "Usage: " +
                "   <message> <topicArn>\n\n" +
                "Where:\n" +
                "   message - The message text to send.\n\n" +
                "   topicArn - The ARN of the topic to publish.\n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String message = args[0];
        String topicArn = args[1];
         SnsClient snsClient = SnsClient.builder()
                 .region(Region.US_EAST_1)
                 .credentialsProvider(ProfileCredentialsProvider.create())
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
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

         } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
         }
    }
    //snippet-end:[sns.java2.PublishTopic.main]
}
