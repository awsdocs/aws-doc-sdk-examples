//snippet-sourcedescription:[RemoveQueueTag.java demonstrates how to remove a tag from an Amazon Simple Queue Service (Amazon SQS) queue.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Queue Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.sqs;

// snippet-start:[sqs.java2.remove_tag.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.UntagQueueRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
// snippet-end:[sqs.java2.remove_tag.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class RemoveQueueTag {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "   <queueName> <tagName>\n\n" +
            "Where:\n" +
            "   queueName - The name of the queue to which tags are applied.\n\n"+
            "   tagName - The name of the tag to remove." ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
         }

        String queueName = args[0];
        String tagName = args[1];
        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        removeTag(sqsClient, queueName, tagName);
        sqsClient.close();
    }

    // snippet-start:[sqs.java2.remove_tag.main]
    public static void removeTag(SqsClient sqsClient, String queueName, String tagName) {

        try {
            GetQueueUrlRequest urlRequest = GetQueueUrlRequest.builder()
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
