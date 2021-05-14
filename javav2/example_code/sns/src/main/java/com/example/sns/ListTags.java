//snippet-sourcedescription:[ListTags.java demonstrates how to retrieve tags from an Amazon Simple Notification Service (Amazon SNS) topic.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/11/2020]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

//snippet-start:[sns.java2.list_tags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.sns.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.list_tags.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListTags {

    public static void main(String[] args) {
    final String USAGE = "\n" +
            "Usage: " +
            "ListTags <topicArn>\n\n" +
            "Where:\n" +
            "  topicArn - the ARN of the topic from which tags are listed.\n\n";

    if (args.length != 1) {
        System.out.println(USAGE);
        System.exit(1);
    }

    String topicArn = args[0];
    SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .build();

    listTopicTags(snsClient, topicArn);
     snsClient.close();
}

    //snippet-start:[sns.java2.list_tags.main]
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
    //snippet-end:[sns.java2.list_tags.main]
}
