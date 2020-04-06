//snippet-sourcedescription:[CreateTopic.java demonstrates how to create an Amazon SNS topic.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/6/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
                "CreateTopic - create an sns topic\n" +
                "Usage: CreateTopic <topicName>\n\n" +
                "Where:\n" +
                "  topicName - the name of the topic to create.\n\n";

        if (args.length < 1) {
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
