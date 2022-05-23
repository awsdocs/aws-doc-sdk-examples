//snippet-sourcedescription:[AddQueueTags.java demonstrates how to add tags to an Amazon Simple Queue Service (Amazon SQS) queue.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sqs;

// snippet-start:[sqs.java2.add_tags.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;
import software.amazon.awssdk.services.sqs.model.TagQueueRequest;
import java.util.HashMap;
// snippet-end:[sqs.java2.add_tags.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AddQueueTags {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <queueName>\n\n" +
                "Where:\n" +
                "   queueName - The name of the queue to which tags are applied.\n\n";

       if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String queueName = args[0];
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        addTags(sqsClient, queueName);
        sqsClient.close();
    }

    // snippet-start:[sqs.java2.add_tags.main]
    public static void addTags(SqsClient sqsClient, String queueName) {

        try {
            GetQueueUrlRequest urlRequest =  GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(urlRequest);
            String queueUrl = getQueueUrlResponse.queueUrl();

            HashMap<String, String> addedTags = new HashMap<>();
            addedTags.put("Team", "Development");
            addedTags.put("Priority", "Beta");
            addedTags.put("Accounting ID", "456def");

            TagQueueRequest tagQueueRequest = TagQueueRequest.builder()
                    .queueUrl(queueUrl)
                    .tags(addedTags)
                    .build();

            sqsClient.tagQueue(tagQueueRequest);
            System.out.println("Tags have been applied to "+queueName);

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[sqs.java2.add_tags.main]
 }
