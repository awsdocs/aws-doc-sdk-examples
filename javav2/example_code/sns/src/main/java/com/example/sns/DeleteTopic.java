//snippet-sourcedescription:[DeleteTopic.java demonstrates how to delete a Topic.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
//snippet-sourceauthor:[scmacdon AWS]

/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

//snippet-start:[sns.java2.DeleteTopic.complete]
package com.example.sns;

//snippet-start:[sns.java2.DeleteTopic.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.DeleteTopic.import]

public class DeleteTopic {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "DeleteTopic - delete an sns topic\n" +
                "Usage: DeleteTopic <topicArn>\n\n" +
                "Where:\n" +
                "  topicArn - the arn of the topic to delete.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[sns.java2.DeleteTopic.main]
        String topicArn = args[0];
        System.out.println("Deleting a topic with name: " + topicArn);

        // Create a SnsClient object
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try {

            DeleteTopicRequest request = DeleteTopicRequest.builder()
                .topicArn(topicArn)
                .build();

            DeleteTopicResponse result = snsClient.deleteTopic(request);
            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[sns.java2.DeleteTopic.main]
    }
}
//snippet-end:[sns.java2.DeleteTopic.complete]
