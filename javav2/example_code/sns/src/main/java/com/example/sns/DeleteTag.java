//snippet-sourcedescription:[DeleteTag.java demonstrates how to delete tags from an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.java2.delete_tags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.UntagResourceRequest;
//snippet-end:[sns.java2.delete_tags.import]

public class DeleteTag {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "AddTags <topicArn>\n\n" +
                "Where:\n" +
                "  topicArn - the ARN of the topic to which tags are added.\n\n"+
                "  tagKey - the key of the tag to delete.";

        if (args.length != 2) {
            System.out.println(USAGE);
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

    //snippet-start:[sns.java2.delete_tags.main]
    public static void removeTag(SnsClient snsClient, String topicArn, String tagKey) {

        try {

            UntagResourceRequest resourceRequest = UntagResourceRequest.builder()
                    .resourceArn(topicArn)
                    .tagKeys(tagKey)
                    .build();

            snsClient.untagResource(resourceRequest);
            System.out.println(tagKey +" was deleted from "+topicArn);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[sns.java2.delete_tags.main]
}
