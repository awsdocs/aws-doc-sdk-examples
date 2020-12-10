//snippet-sourcedescription:[CreateTopic.java demonstrates how to create an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.java2.CreateTopic.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.CreateTopic.import]

public class CreateTopic {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage: " +
                "CreateTopic <topicName>\n\n" +
                "Where:\n" +
                "  topicName - the name of the topic to create (for example, mytopic).\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String topicName = args[0];
        System.out.println("Creating a topic with name: " + topicName);

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        String arnVal = createSNSTopic(snsClient, topicName) ;
        System.out.println("The topic ARN is" +arnVal);
        snsClient.close();
    }

    //snippet-start:[sns.java2.CreateTopic.main]
    public static String createSNSTopic(SnsClient snsClient, String topicName ) {

        CreateTopicResponse result = null;
        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();

            result = snsClient.createTopic(request);
            return result.topicArn();
        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
        //snippet-end:[sns.java2.CreateTopic.main]
    }
}
