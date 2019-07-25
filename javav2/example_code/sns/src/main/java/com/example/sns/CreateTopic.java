//snippet-sourcedescription:[CreateTopic.java demonstrates how to create a Topic.]
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
//snippet-start:[sns.java2.CreateTopic.complete]
package com.example.sns;

//snippet-start:[sns.java2.CreateTopic.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
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
        //snippet-start:[sns.java2.CreateTopic.main]
        String topicName = args[0];

        System.out.println("Creating a topic with name: " + topicName);

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        CreateTopicRequest request = CreateTopicRequest.builder()
            .name(topicName)
            .build();

        CreateTopicResponse result = snsClient.createTopic(request);
        System.out.println("Created topic " + topicName + "with Arn: " + result.topicArn());
        //snippet-end:[sns.java2.CreateTopic.main]
    }
}
//snippet-end:[sns.java2.CreateTopic.complete]

