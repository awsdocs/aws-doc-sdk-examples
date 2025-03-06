// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

// snippet-start:[sqs.java2.sqs_sse_example.main]
// snippet-start:[sqs.java2.sqs_sse_example.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.InvalidAttributeNameException;
import software.amazon.awssdk.services.sqs.model.InvalidAttributeValueException;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;

import java.util.Map;
// snippet-end:[sqs.java2.sqs_sse_example.import]

/**
 * <p>Before running this Java V2 code example, set up your development
 * environment, including your credentials.</p>
 *
 * <p>For more information, see the following documentation topic:<br/>
 * <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html">Get started with the AWS
 * SDK for Java 2.x</a>.</a></p>
 */
public class SSEncryptionExample {
    static final String STACK_NAME = "sqs-sse-example-stack";
    static final String CFN_TEMPLATE_FILE_NAME = "cfn_template.yaml";
    private static final Logger LOGGER = LoggerFactory.getLogger(SSEncryptionExample.class);

    public static void main(String[] args) {
        String queueName;
        String kmsMasterKeyAlias;
        try {
            CloudFormationHelper.deployCloudFormationStack(STACK_NAME, CFN_TEMPLATE_FILE_NAME);
            final Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(STACK_NAME);

            queueName = stackOutputs.get("QueueName");
            kmsMasterKeyAlias = stackOutputs.get("KeyAlias");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            addEncryption(queueName, kmsMasterKeyAlias);
        } catch (Exception e) {
            LOGGER.error("Exception thrown in `addEncryption` method. (Ending program.)");
        } finally {
            CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
            LOGGER.info("Program ended.");
        }
    }

    /** This method enables server-side encryption (SSE) using a custom KMS key for an existing Amazon SQS queue.
     *
     * @param queueName Name of the queue.
     * @param kmsMasterKeyAlias Alias of the AWS KMS key.
     */
    // snippet-start:[sqs.java2.sqs_sse_example.add-encryption-method]
    public static void addEncryption(String queueName, String kmsMasterKeyAlias) {
        SqsClient sqsClient = SqsClient.create();

        GetQueueUrlRequest urlRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

        GetQueueUrlResponse getQueueUrlResponse;
        try {
            getQueueUrlResponse = sqsClient.getQueueUrl(urlRequest);
        } catch (QueueDoesNotExistException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        String queueUrl = getQueueUrlResponse.queueUrl();


        Map<QueueAttributeName, String> attributes = Map.of(
                QueueAttributeName.KMS_MASTER_KEY_ID, kmsMasterKeyAlias,
                QueueAttributeName.KMS_DATA_KEY_REUSE_PERIOD_SECONDS, "140" // Set the data key reuse period to 140 seconds.
        );                                                                  // This is how long SQS can reuse the data key before requesting a new one from KMS.

        SetQueueAttributesRequest attRequest = SetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributes(attributes)
                .build();
        try {
            sqsClient.setQueueAttributes(attRequest);
            LOGGER.info("The attributes have been applied to {}", queueName);
        } catch (InvalidAttributeNameException | InvalidAttributeValueException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            sqsClient.close();
        }
    }
    // snippet-end:[sqs.java2.sqs_sse_example.add-encryption-method]
}
// snippet-end:[sqs.java2.sqs_sse_example.main]
