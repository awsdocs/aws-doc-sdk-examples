// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.IamPolicy;
import software.amazon.awssdk.policybuilder.iam.IamPolicyWriter;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationAsyncWaiter;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.eventbridge.model.EventBus;
import software.amazon.awssdk.services.eventbridge.model.ListEventBusesResponse;
import software.amazon.awssdk.services.eventbridge.model.PutRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.PutRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.Target;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.utils.builder.SdkBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PutBucketS3EventNotificationEventBridge {
    static final CloudFormationAsyncClient cfClient = CloudFormationAsyncClient.create();
    static final SqsAsyncClient sqsClient = SqsAsyncClient.create();
    static final SnsAsyncClient snsClient = SnsAsyncClient.create();
    static final EventBridgeAsyncClient eventBridgeClient = EventBridgeAsyncClient.create();
    static final String STACK_NAME = "queue-topic";
    static final String RULE_NAME = "s3-object-create-rule";
    static final S3AsyncClient s3Client = S3AsyncClient.create();
    private static final Logger logger = LoggerFactory.getLogger(PutBucketS3EventNotificationEventBridge.class);

    public static void main(String[] args) {
        deployCloudFormationStack();
        String bucketName = getBucketName();
        String topicArn = getTopicArn();
        String directToQueueUrl = getQueueUrl(false);
        String directToQueueArn = getQueueArn(directToQueueUrl);
        String subscriberQueueUrl = getQueueUrl(true);
        String subscriberQueueArn = getQueueArn(subscriberQueueUrl);
        String ruleArn = setBucketNotificationToEventBridge(bucketName, topicArn, directToQueueArn);
        addPermissions(directToQueueArn, directToQueueUrl,
                subscriberQueueArn, subscriberQueueUrl, topicArn, ruleArn);
        deleteRule();
        destroyCloudFormationStack();
    }
// snippet-start:[s3.java2.s3_enable_notifications_to_eventbridge]
    /** This method configures a bucket to send events to AWS EventBridge and creates a rule
     * to route the S3 object created events to a topic and a queue.
     *
     * @param bucketName Name of existing bucket
     * @param topicArn ARN of existing topic to receive S3 event notifications
     * @param queueArn ARN of existing queue to receive S3 event notifications
     *
     *  An AWS CloudFormation stack sets up the bucket, queue, topic before the method runs.
     */
    public static String setBucketNotificationToEventBridge(String bucketName, String topicArn, String queueArn) {
        try {
            // Enable bucket to emit S3 Event notifications to EventBridge.
            s3Client.putBucketNotificationConfiguration(b -> b
                    .bucket(bucketName)
                    .notificationConfiguration(b1 -> b1
                            .eventBridgeConfiguration(
                                    SdkBuilder::build)
                    ).build()).join();

            // Create an EventBridge rule to route Object Created notifications.
            PutRuleRequest putRuleRequest = PutRuleRequest.builder()
                    .name(RULE_NAME)
                    .eventPattern("""
                            {
                              "source": ["aws.s3"],
                              "detail-type": ["Object Created"],
                              "detail": {
                                "bucket": {
                                  "name": ["%s"]
                                }
                              }
                            }
                            """.formatted(bucketName))
                    .build();

            // Add the rule to the default event bus.
            PutRuleResponse putRuleResponse = eventBridgeClient.putRule(putRuleRequest)
                    .whenComplete((r, t) -> {
                        if (t != null) {
                            logger.error("Error creating event bus rule: " + t.getMessage(), t);
                            throw new RuntimeException(t.getCause().getMessage(), t);
                        }
                        logger.info("Event bus rule creation request sent successfully. ARN is: {}", r.ruleArn());
                    }).join();

            // Add the existing SNS topic and SQS queue as targets to the rule.
            eventBridgeClient.putTargets(b -> b
                    .eventBusName("default")
                    .rule(RULE_NAME)
                    .targets(List.of (
                            Target.builder()
                                    .arn(queueArn)
                                    .id("Queue")
                                    .build(),
                            Target.builder()
                                    .arn(topicArn)
                                    .id("Topic")
                                    .build())
                            )
                    ).join();
            return putRuleResponse.ruleArn();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    // snippet-end:[s3.java2.s3_enable_notifications_to_eventbridge]

    /** After we create the EventBridge rule, we add the necessary permissions to the resources
     * that receive messages from the rule.
     *
     * @param directQueueArn ARN of the queue that receives notifications from the S3 bucket directly
     * @param directQueueUrl URL of the queue that receives notifications from the S3 bucket directly
     * @param subscriberQueueArn ARN of the queue that receives notifications through the subscription to the SNS topic
     * @param subscriberQueueUrl  URL of the queue that receives notifications through the subscription to the SNS topic
     * @param topicArn ARN of the topic that receives notifications from the S3 bucket
     * @param ruleArn ARN of the EventBridge rule
     */
    static void addPermissions(String directQueueArn, String directQueueUrl, String subscriberQueueArn,
                               String subscriberQueueUrl, String topicArn, String ruleArn){
        addPermissionToDirectQueue(sqsClient, directQueueArn, directQueueUrl, ruleArn);
        addPermissionToSubscriberQueue(sqsClient, subscriberQueueArn, subscriberQueueUrl, topicArn);
        addPermissionToTopic(snsClient, topicArn, ruleArn);
    }

    static void addPermissionToDirectQueue(SqsAsyncClient sqsClient, String queueArn, String queueUrl, String roleArn){
        /*
        We use the Java SDK's IAM Policy Builder API to generate a policy.
         This requires the following Maven dependency:
         <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>iam-policy-builder</artifactId>
        </dependency>
        */
        String policyString = IamPolicy.builder()
                .version("2012-10-17")
                .id(queueArn)
                .addStatement(b -> b
                        .sid("AllowEventsToQueue")
                        .effect("Allow")
                        .addPrincipal(pb -> pb
                                .type(IamPrincipalType.SERVICE)
                                .id("events.amazonaws.com"))
                        .addAction("sqs:SendMessage")
                        .addResource(queueArn)
                        .addCondition(cb -> cb
                                .operator(IamConditionOperator.ARN_EQUALS)
                                .key("aws:SourceArn")
                                .value(roleArn))
                        .build())
                .build().toJson(
                IamPolicyWriter.builder().prettyPrint(true).build());

        sqsClient.setQueueAttributes(b -> b
                .queueUrl(queueUrl)
                .attributes(Map.of (QueueAttributeName.POLICY, policyString)))
                .join();
    }

    static void addPermissionToSubscriberQueue(SqsAsyncClient sqsClient, String queueArn, String queueUrl, String topicArn){
        String policyString = IamPolicy.builder()
                .version("2012-10-17")
                .id(queueArn)
                .addStatement(b -> b
                        .sid("AllowMessageToSubscriberQueue")
                        .effect("Allow")
                        .addPrincipal(pb -> pb.type(IamPrincipalType.SERVICE)
                                .id("sns.amazonaws.com"))
                        .addAction("sqs:SendMessage")
                        .addResource(queueArn)
                        .addCondition(cb -> cb
                                .operator(IamConditionOperator.ARN_EQUALS)
                                .key("aws:SourceArn")
                                .value(topicArn))
                        .build())
                .build().toJson(
                IamPolicyWriter.builder().prettyPrint(true).build()
        );

        sqsClient.setQueueAttributes(b -> b
                        .queueUrl(queueUrl)
                        .attributes(Map.of (QueueAttributeName.POLICY, policyString)))
                .join();
    }

    static void addPermissionToTopic(SnsAsyncClient snsClient, String topicArn, String roleArn){
        snsClient.getTopicAttributes(b -> b
                .topicArn(topicArn).build())
                .thenApply(ar -> {
                    String policy = ar.attributes().get("Policy");
                    IamPolicy iamPolicy = IamPolicy.fromJson(policy);
                    return iamPolicy.copy(b -> b.addStatement(sb -> sb
                            .sid("AllowEventsToTopic")
                            .effect("Allow")
                            .addPrincipal(pb -> pb
                                    .type(IamPrincipalType.SERVICE)
                                    .id("events.amazonaws.com"))
                            .addAction("sns:Publish")
                            .addResource(topicArn)
                            .addCondition(cb -> cb
                                    .operator(IamConditionOperator.ARN_EQUALS)
                                    .key("aws:SourceArn")
                                    .value(roleArn))
                            .build())).toJson(IamPolicyWriter.builder().prettyPrint(true).build());
                }).thenAccept(policy -> snsClient.setTopicAttributes(b -> b
                        .attributeName("Policy")
                        .attributeValue(policy)
                        .topicArn(topicArn)))
                .join();
    }

    static String getBucketName() {
        return s3Client.listBuckets().handle((r, t) -> {
            for (Bucket bucket : r.buckets()) {
                if (bucket.name().startsWith(STACK_NAME.substring(0, 10))) {
                    return bucket.name();
                }
            }
            return null;
        }).join();
    }

    static String getQueueUrl(boolean isSubscriberQueue){
        ListQueuesResponse response = sqsClient.listQueues().join();
        String queueUrl;
        if (!isSubscriberQueue) {
            queueUrl = response.queueUrls().stream()
                    .filter(url -> url.contains(STACK_NAME) && !url.contains("Subscriber"))
                    .findFirst()
                    .orElse(null);
        } else {
            queueUrl = response.queueUrls().stream()
                    .filter(url -> url.contains(STACK_NAME) && url.contains("Subscriber"))
                    .findFirst()
                    .orElse(null);
        }
        if (queueUrl == null) {
            throw new RuntimeException("Queue URL not found");
        }
        return queueUrl;
    }

    static String getQueueArn(String queueUrl) {
        return sqsClient.getQueueAttributes(b -> b
                .queueUrl(queueUrl).attributeNames(QueueAttributeName.QUEUE_ARN)).join()
                .attributes().get(QueueAttributeName.QUEUE_ARN);
    }

    static String getTopicArn() {
        ListTopicsResponse response = snsClient.listTopics().join();
        Optional<String> topicArn = response.topics().stream()
                .map(Topic::topicArn)
                .filter(s -> s.contains(STACK_NAME))
                .findFirst();
        return topicArn.orElse(null);
    }

    static String getEventBusName() {
         ListEventBusesResponse eventBusesResponse = eventBridgeClient.listEventBuses(SdkBuilder::build).join();
        final Optional<String> busName = eventBusesResponse.eventBuses().stream()
                .map(EventBus::name)
                .filter(s -> s.startsWith(STACK_NAME.substring(0, 4)))
                .findFirst();
        return busName.orElse(null);

    }

    static void deployCloudFormationStack() {
        try {
            URL fileUrl = PutBucketS3EventNotificationEventBridge.class.getClassLoader().getResource(STACK_NAME + ".yaml");
            String templateBody;
            try {
                templateBody = Files.readString(Paths.get(fileUrl.toURI()));

            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }

            cfClient.createStack(b -> b.stackName(STACK_NAME)
                            .templateBody(templateBody)
                            .capabilities(Capability.CAPABILITY_IAM))
                    .whenComplete((csr, t) -> {
                        if (csr != null) {
                            logger.info("Stack creation requested, ARN is " + csr.stackId());
                            try (CloudFormationAsyncWaiter waiter = cfClient.waiter()) {
                                waiter.waitUntilStackCreateComplete(request -> request.stackName(STACK_NAME))
                                        .whenComplete((dsr, th) -> {
                                            dsr.matched().response().orElseThrow(() -> new RuntimeException("Failed to deploy"));
                                        }).join();
                            }
                            logger.info("Stack created successfully");
                        } else {
                            logger.error("Error creating stack: " + t.getMessage(), t);
                            throw new RuntimeException(t.getCause().getMessage(), t);
                        }
                    }).join();
        } catch (CloudFormationException ex) {
            throw new RuntimeException("Failed to deploy CloudFormation stack", ex);
        }
    }

    static void destroyCloudFormationStack() {
        String stackName = STACK_NAME;
        cfClient.deleteStack(b -> b.stackName(stackName))
                .whenComplete((dsr, t) -> {
                    if (dsr != null) {
                        logger.info("Delete stack requested ....");
                        try (CloudFormationAsyncWaiter waiter = cfClient.waiter()) {
                            waiter.waitUntilStackDeleteComplete(request -> request.stackName(stackName))
                                    .whenComplete((waiterResponse, throwable) ->
                                            logger.info("Stack deleted successfully."))
                                    .join();
                        }
                    } else {
                        logger.error("Error deleting stack: " + t.getMessage(), t);
                        throw new RuntimeException(t.getCause().getMessage(), t);
                    }
                }).join();
    }

    static void deleteRule() {

        eventBridgeClient.removeTargets(b -> b
                .rule(RULE_NAME)
                .ids("Queue", "Topic")
                .build()).join();

        eventBridgeClient.deleteRule(b -> b
                .name(RULE_NAME)
                .build()).join();
    }
}
