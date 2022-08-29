//snippet-sourcedescription:[DeadLetterQueues.java demonstrates how to set a queue as a dead letter queue for Amazon Simple Queue Service (Amazon SQS).]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Queue Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sqs;

// snippet-start:[sqs.java2.delete_letter_queues.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import java.util.Date;
import java.util.HashMap;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
// snippet-end:[sqs.java2.delete_letter_queues.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeadLetterQueues {
    private static final String QueueName = "testQueue" + new Date().getTime();
    private static final String DLQueueName = "DLQueue" + new Date().getTime();

    public static void main(String[] args) {

        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        setDeadLetterQueue(sqsClient);
        sqsClient.close();
    }

    // snippet-start:[sqs.java2.delete_letter_queues.main]
    public static void setDeadLetterQueue( SqsClient sqs) {

        try {
            CreateQueueRequest request = CreateQueueRequest.builder()
                .queueName(QueueName)
                .build();

            CreateQueueRequest dlrequest = CreateQueueRequest.builder()
                .queueName(DLQueueName)
                .build();

            sqs.createQueue(dlrequest);
            GetQueueUrlRequest getRequest = GetQueueUrlRequest.builder()
                .queueName(DLQueueName)
                .build();

            // Get dead-letter queue ARN
            String dlQueueUrl = sqs.getQueueUrl(getRequest).queueUrl();
            GetQueueAttributesResponse queueAttrs = sqs.getQueueAttributes(
                GetQueueAttributesRequest.builder()
                    .queueUrl(dlQueueUrl)
                    .attributeNames(QueueAttributeName.QUEUE_ARN)
                        .build());

            String dlQueueArn = queueAttrs.attributes().get(QueueAttributeName.QUEUE_ARN);

            // Set dead letter queue with redrive policy on source queue.
            GetQueueUrlRequest getRequestSource = GetQueueUrlRequest.builder()
                .queueName(DLQueueName)
                .build();

            String srcQueueUrl = sqs.getQueueUrl(getRequestSource).queueUrl();
            HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
            attributes.put(QueueAttributeName.REDRIVE_POLICY, "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\""
                + dlQueueArn + "\"}");

            SetQueueAttributesRequest setAttrRequest = SetQueueAttributesRequest.builder()
                .queueUrl(srcQueueUrl)
                .attributes(attributes)
                .build();

            sqs.setQueueAttributes(setAttrRequest);

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[sqs.java2.delete_letter_queues.main]
}
