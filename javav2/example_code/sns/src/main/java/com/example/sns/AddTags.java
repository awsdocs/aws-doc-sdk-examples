//snippet-sourcedescription:[AddTags.java demonstrates how to add tags to an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.java2.add_tags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.Tag;
import software.amazon.awssdk.services.sns.model.TagResourceRequest;
import java.util.ArrayList;
import java.util.List;
//snippet-end:[sns.java2.add_tags.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AddTags {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "AddTags <topicArn>\n\n" +
                "Where:\n" +
                "  topicArn - the ARN of the topic to which tags are added.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String topicArn = args[0];
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        addTopicTags(snsClient, topicArn);
        snsClient.close();
       }

    public static void addTopicTags(SnsClient snsClient, String topicArn) {


     try {
        Tag tag = Tag.builder()
                .key("Team")
                .value("Development")
                .build();

        Tag tag2 = Tag.builder()
                .key("Environment")
                .value("Gamma")
                .build();

        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        tagList.add(tag2);

        TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                .resourceArn(topicArn)
                .tags(tagList)
                .build();

        snsClient.tagResource(tagResourceRequest);
        System.out.println("Tags have been added to "+topicArn);

      } catch (SnsException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
   }
}
