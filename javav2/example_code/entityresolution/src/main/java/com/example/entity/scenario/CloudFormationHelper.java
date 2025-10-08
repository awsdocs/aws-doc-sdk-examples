// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.entity.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.*;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationAsyncWaiter;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CloudFormationHelper {
    private static final String CFN_TEMPLATE = "template.yaml";
    private static final Logger logger = LoggerFactory.getLogger(CloudFormationHelper.class);

    private static CloudFormationAsyncClient cloudFormationClient;

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.error("Usage: CloudFormationHelper <bucketName>");
            return;
        }
        emptyS3Bucket(args[0]);
    }

    private static CloudFormationAsyncClient getCloudFormationClient() {
        if (cloudFormationClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                    .maxConcurrency(100)
                    .connectionTimeout(Duration.ofSeconds(60))
                    .readTimeout(Duration.ofSeconds(60))
                    .writeTimeout(Duration.ofSeconds(60))
                    .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                    .apiCallTimeout(Duration.ofMinutes(2))
                    .apiCallAttemptTimeout(Duration.ofSeconds(90))
                    .retryStrategy(RetryMode.STANDARD)
                    .build();

            cloudFormationClient = CloudFormationAsyncClient.builder()
                    .httpClient(httpClient)
                    .overrideConfiguration(overrideConfig)
                    .build();
        }
        return cloudFormationClient;
    }

    public static void deployCloudFormationStack(String stackName) {
        logger.info("Deploying CloudFormation stack: {}", stackName);
        boolean doesExist = describeStack(stackName);
        if (!doesExist) {
            String templateBody;
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Path filePath = Paths.get(Objects.requireNonNull(classLoader.getResource(CFN_TEMPLATE)).toURI());
                templateBody = Files.readString(filePath);
            } catch (IOException | URISyntaxException e) {
                logger.error("Failed to read CloudFormation template", e);
                throw new RuntimeException(e);
            }

            getCloudFormationClient().createStack(b -> b.stackName(stackName)
                            .templateBody(templateBody)
                            .capabilities(Capability.CAPABILITY_IAM))
                    .whenComplete((csr, t) -> {
                        if (t != null) {
                            logger.error("Error creating stack {}", stackName, t);
                            throw new RuntimeException("Stack creation failed", t);
                        }

                        logger.info("Stack creation requested. ARN: {}", csr.stackId());

                        try (CloudFormationAsyncWaiter waiter = getCloudFormationClient().waiter()) {
                            waiter.waitUntilStackCreateComplete(request -> request.stackName(stackName))
                                    .whenComplete((dsr, th) -> {
                                        if (th != null) {
                                            logger.error("Error waiting for stack creation: {}", stackName, th);
                                        } else {
                                            dsr.matched().response()
                                                    .orElseThrow(() -> new RuntimeException("Stack creation failed for " + stackName));
                                            logger.info("Stack {} created successfully.", stackName);

                                            // Print outputs immediately
                                            getStackOutputsAsync(stackName).whenComplete((outputs, outEx) -> {
                                                if (outEx != null) {
                                                    logger.error("Failed to fetch stack outputs", outEx);
                                                } else {
                                                    logger.info("Stack Outputs for {}:", stackName);
                                                    outputs.forEach((k, v) -> logger.info("  {} = {}", k, v));
                                                }
                                            }).join();
                                        }
                                    }).join();
                        }
                    }).join();
        } else {
            logger.info("Stack {} already exists, skipping creation.", stackName);
        }
    }

    // Check to see if the Stack exists before deploying it
    public static Boolean describeStack(String stackName) {
        try {
            CompletableFuture<DescribeStacksResponse> future = getCloudFormationClient().describeStacks();
            DescribeStacksResponse stacksResponse = future.join();
            for (Stack myStack : stacksResponse.stacks()) {
                if (myStack.stackName().equals(stackName)) {
                    logger.info("Stack {} exists already.", stackName);
                    return true;
                }
            }
        } catch (CloudFormationException e) {
            logger.error("Error describing stack {}", stackName, e);
        }
        return false;
    }

    public static void destroyCloudFormationStack(String stackName) {
        logger.info("Deleting CloudFormation stack: {}", stackName);
        getCloudFormationClient().deleteStack(b -> b.stackName(stackName))
                .whenComplete((dsr, t) -> {
                    if (t != null) {
                        logger.error("Error deleting stack {}", stackName, t);
                        throw new RuntimeException("Delete failed", t);
                    }

                    logger.info("Delete stack requested: {}", stackName);
                    try (CloudFormationAsyncWaiter waiter = getCloudFormationClient().waiter()) {
                        waiter.waitUntilStackDeleteComplete(request -> request.stackName(stackName))
                                .whenComplete((waiterResponse, throwable) -> {
                                    if (throwable != null) {
                                        logger.error("Error waiting for stack deletion {}", stackName, throwable);
                                    } else {
                                        logger.info("Stack {} deleted successfully.", stackName);
                                    }
                                }).join();
                    }
                }).join();
    }

    public static CompletableFuture<Map<String, String>> getStackOutputsAsync(String stackName) {
        logger.info("Fetching stack outputs for {}", stackName);

        DescribeStacksRequest describeStacksRequest = DescribeStacksRequest.builder()
                .stackName(stackName)
                .build();

        return getCloudFormationClient().describeStacks(describeStacksRequest)
                .handle((describeStacksResponse, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to get stack outputs for {}", stackName, throwable);
                        throw new RuntimeException("Failed to get stack outputs for: " + stackName, throwable);
                    }

                    if (describeStacksResponse.stacks().isEmpty()) {
                        throw new RuntimeException("Stack not found: " + stackName);
                    }

                    Stack stack = describeStacksResponse.stacks().get(0);
                    Map<String, String> outputs = new HashMap<>();
                    for (Output output : stack.outputs()) {
                        outputs.put(output.outputKey(), output.outputValue());
                    }
                    return outputs;
                });
    }

    public static void emptyS3Bucket(String bucketName) {
        logger.info("Emptying S3 bucket: {}", bucketName);
        S3AsyncClient s3Client = S3AsyncClient.builder().build();

        s3Client.listObjectsV2(req -> req.bucket(bucketName))
                .thenCompose(response -> {
                    if (response.contents().isEmpty()) {
                        logger.info("Bucket {} is already empty.", bucketName);
                        return CompletableFuture.completedFuture(null);
                    }

                    List<CompletableFuture<DeleteObjectResponse>> deleteFutures = response.contents().stream()
                            .map(s3Object -> {
                                logger.info("Deleting object: {}", s3Object.key());
                                return s3Client.deleteObject(req -> req
                                        .bucket(bucketName)
                                        .key(s3Object.key()));
                            })
                            .collect(Collectors.toList());

                    return CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]));
                })
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        logger.error("Failed to empty bucket {}", bucketName, ex);
                    } else {
                        logger.info("Bucket {} emptied successfully.", bucketName);
                    }
                })
                .join();

        s3Client.close();
    }
}
