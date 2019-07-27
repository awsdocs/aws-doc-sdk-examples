//snippet-sourcedescription:[PublishTopic.java demonstrates how to  send a message to an SNS Topic.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[sns.java2.PublishTopic.complete]
package com.example.sns;

//snippet-start:[sns.java2.PublishTopic.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
//snippet-end:[sns.java2.PublishTopic.import]

public class PublishTopic {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "PublishTopic - publish an sns topic\n" +
                "Usage: PublishTopic <message> <topicArn>\n\n" +
                "Where:\n" +
                "  message - message text to send.\n\n" +
                "  topicArn - the arn of the topic to look up.\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[sns.java2.PublishTopic.main]
        String message = args[0];
        String topicArn = args[1];

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(topicArn)
                .build();

        PublishResponse result = snsClient.publish(request);

        System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
        //snippet-end:[sns.java2.PublishTopic.main]
    }
}
//snippet-end:[sns.java2.PublishTopic.complete]