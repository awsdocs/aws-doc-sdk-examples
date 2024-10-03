// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iotsitewise.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationAsyncWaiter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CloudFormationHelper {
    private static final String CFN_TEMPLATE = "SitewiseRoles-template.yaml";
    private static final Logger logger = LoggerFactory.getLogger(CloudFormationHelper.class);

    private static CloudFormationAsyncClient cloudFormationClient;

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
        String templateBody;
        boolean doesExist = describeStack(stackName);
        if (!doesExist) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Path filePath = Paths.get(classLoader.getResource(CFN_TEMPLATE).toURI());
                templateBody = Files.readString(filePath);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }

            getCloudFormationClient().createStack(b -> b.stackName(stackName)
                    .templateBody(templateBody)
                    .capabilities(Capability.CAPABILITY_IAM))
                .whenComplete((csr, t) -> {
                    if (csr != null) {
                        System.out.println("Stack creation requested, ARN is " + csr.stackId());
                        try (CloudFormationAsyncWaiter waiter = getCloudFormationClient().waiter()) {
                            waiter.waitUntilStackCreateComplete(request -> request.stackName(stackName))
                                .whenComplete((dsr, th) -> {
                                    if (th != null) {
                                        System.out.println("Error waiting for stack creation: " + th.getMessage());
                                    } else {
                                        dsr.matched().response().orElseThrow(() -> new RuntimeException("Failed to deploy"));
                                        System.out.println("Stack created successfully");
                                    }
                                }).join();
                        }
                    } else {
                        System.out.format("Error creating stack: " + t.getMessage(), t);
                        throw new RuntimeException(t.getCause().getMessage(), t);
                    }
                }).join();
        } else {
            logger.info("{} stack already exists", CFN_TEMPLATE);
        }
    }

    // Check to see if the Stack exists before deploying it
    public static Boolean describeStack(String stackName) {
        try {
            CompletableFuture<?> future = getCloudFormationClient().describeStacks();
            DescribeStacksResponse stacksResponse = (DescribeStacksResponse) future.join();
            List<Stack> stacks = stacksResponse.stacks();
            for (Stack myStack : stacks) {
                if (myStack.stackName().compareTo(stackName) == 0) {
                    return true;
                }
            }
        } catch (CloudFormationException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static void destroyCloudFormationStack(String stackName) {
        getCloudFormationClient().deleteStack(b -> b.stackName(stackName))
            .whenComplete((dsr, t) -> {
                if (dsr != null) {
                    System.out.println("Delete stack requested ....");
                    try (CloudFormationAsyncWaiter waiter = getCloudFormationClient().waiter()) {
                        waiter.waitUntilStackDeleteComplete(request -> request.stackName(stackName))
                            .whenComplete((waiterResponse, throwable) ->
                                System.out.println("Stack deleted successfully."))
                            .join();
                    }
                } else {
                    System.out.format("Error deleting stack: " + t.getMessage(), t);
                    throw new RuntimeException(t.getCause().getMessage(), t);
                }
            }).join();
    }

    public static CompletableFuture<Map<String, String>> getStackOutputsAsync(String stackName) {
        CloudFormationAsyncClient cloudFormationAsyncClient = getCloudFormationClient();

        DescribeStacksRequest describeStacksRequest = DescribeStacksRequest.builder()
            .stackName(stackName)
            .build();

        return cloudFormationAsyncClient.describeStacks(describeStacksRequest)
            .handle((describeStacksResponse, throwable) -> {
                if (throwable != null) {
                    throw new RuntimeException("Failed to get stack outputs for: " + stackName, throwable);
                }

                // Process the result
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
}
