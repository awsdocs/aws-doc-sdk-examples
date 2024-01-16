// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sns;

// snippet-start:[sns.java2.list_tags.main]
// snippet-start:[sns.java2.list_tags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.sns.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
// snippet-end:[sns.java2.list_tags.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListTags {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <topicArn>

                Where:
                   topicArn - The ARN of the topic from which tags are listed.

                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String topicArn = args[0];
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listTopicTags(snsClient, topicArn);
        snsClient.close();
    }

    public static void listTopicTags(SnsClient snsClient, String topicArn) {
        try {
            ListTagsForResourceRequest tagsForResourceRequest = ListTagsForResourceRequest.builder()
                    .resourceArn(topicArn)
                    .build();

            ListTagsForResourceResponse response = snsClient.listTagsForResource(tagsForResourceRequest);
            System.out.println(String.format("Tags for topic %s are %s.\n",
                    topicArn, response.tags()));

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[sns.java2.list_tags.main]
