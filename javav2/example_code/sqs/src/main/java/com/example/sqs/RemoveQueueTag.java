//snippet-sourcedescription:[RemoveQueueTag.java demonstrates how to remove a tag from an Amazon Simple Queue Service (Amazon SQS) queue.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/09/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.sqs;

// snippet-start:[sqs.java2.remove_tag.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.UntagQueueRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
// snippet-end:[sqs.java2.remove_tag.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class RemoveQueueTag {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: RemoveQueueTag <queueName> <tagName>\n\n" +
                "Where:\n" +
                "  queueName - the name of the queue to which tags are applied.\n\n"+
                "  tagName - the name of the tag to remove." ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
         }

        String queueName = args[0];
        String tagName = args[1];
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        removeTag(sqsClient, queueName, tagName);
        sqsClient.close();
    }

    // snippet-start:[sqs.java2.remove_tag.main]
    public static void removeTag(SqsClient sqsClient, String queueName, String tagName) {

        try {
            GetQueueUrlRequest urlRequest =  GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(urlRequest);
            String queueUrl = getQueueUrlResponse.queueUrl();

            UntagQueueRequest untagQueueRequest = UntagQueueRequest.builder()
                    .queueUrl(queueUrl)
                    .tagKeys(tagName)
                    .build();

            sqsClient.untagQueue(untagQueueRequest);
            System.out.println("The "+tagName +" tag was removed from  "+queueName);

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[sqs.java2.remove_tag.main]
}
