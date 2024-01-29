// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sns;

// snippet-start:[sns.java2.delete_tags.main]
// snippet-start:[sns.java2.delete_tags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.UntagResourceRequest;
// snippet-end:[sns.java2.delete_tags.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteTag {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <topicArn> <tagKey>

                Where:
                  topicArn - The ARN of the topic to which tags are added.
                  tagKey - The key of the tag to delete.
                  """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String topicArn = args[0];
        String tagKey = args[1];
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        removeTag(snsClient, topicArn, tagKey);
        snsClient.close();
    }

    public static void removeTag(SnsClient snsClient, String topicArn, String tagKey) {
        try {
            UntagResourceRequest resourceRequest = UntagResourceRequest.builder()
                    .resourceArn(topicArn)
                    .tagKeys(tagKey)
                    .build();

            snsClient.untagResource(resourceRequest);
            System.out.println(tagKey + " was deleted from " + topicArn);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[sns.java2.delete_tags.main]
