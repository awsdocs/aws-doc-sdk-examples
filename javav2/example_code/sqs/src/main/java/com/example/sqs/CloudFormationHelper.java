// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationAsyncWaiter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CloudFormationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudFormationHelper.class);
    private static final CloudFormationAsyncClient cfClient = CloudFormationAsyncClient.create();

    public static void deployCloudFormationStack(String stackName, String templateFileName) {
        final URL resource = CloudFormationHelper.class.getClassLoader().getResource(templateFileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        }
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        }
        String templateBody;
        boolean doesExist = describeStack(stackName);
        if (!doesExist) {
            try {
                Path filePath = Paths.get(resource.toURI()).toAbsolutePath();
                templateBody = Files.readString(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            cfClient.createStack(b -> b.stackName(stackName)
                            .templateBody(templateBody)
                            .capabilities(Capability.CAPABILITY_IAM))
                    .whenComplete((csr, t) -> {
                        if (csr != null) {
                            LOGGER.info("Stack creation requested, ARN is {}", csr.stackId());
                            try (CloudFormationAsyncWaiter waiter = cfClient.waiter()) {
                                waiter.waitUntilStackCreateComplete(request -> request.stackName(stackName))
                                        .whenComplete((dsr, th) -> {
                                            if (th != null) {
                                                LOGGER.error("Error waiting for stack creation: {}", th.getMessage());
                                            } else {
                                                dsr.matched().response().orElseThrow(() -> new RuntimeException("Failed to deploy"));
                                                LOGGER.info("Stack created successfully");
                                            }
                                        }).join();
                            }
                        } else {
                            LOGGER.error(t.getMessage(), t);
                            throw new RuntimeException(t.getCause().getMessage(), t);
                        }
                    }).join();
        }
    }

    // Check to see if the Stack exists before trying to deploy it again.
    public static Boolean describeStack(String stackName) {
        try {
            CompletableFuture<?> future = cfClient.describeStacks();
            DescribeStacksResponse stacksResponse = (DescribeStacksResponse) future.join();
            List<Stack> stacks = stacksResponse.stacks();
            for (Stack myStack : stacks) {
                if (myStack.stackName().compareTo(stackName) == 0) {
                    return true;
                }
            }
        } catch (CloudFormationException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return false;
    }

    public static void destroyCloudFormationStack(String stackName) {
        cfClient.deleteStack(b -> b.stackName(stackName))
                .whenComplete((dsr, t) -> {
                    if (dsr != null) {
                        LOGGER.info("Delete stack requested ....");
                        try (CloudFormationAsyncWaiter waiter = cfClient.waiter()) {
                            waiter.waitUntilStackDeleteComplete(request -> request.stackName(stackName))
                                    .whenComplete((waiterResponse, throwable) ->
                                            LOGGER.info("Stack deleted successfully."))
                                    .join();
                        }
                    } else {
                        LOGGER.error("Error deleting stack: {}", t.getMessage(), t);
                        throw new RuntimeException(t.getCause().getMessage(), t);
                    }
                }).join();
    }

    public static Map<String, String> getStackOutputs(String stackName) {
        CloudFormationClient cfClient = CloudFormationClient.create();
        DescribeStacksRequest describeStacksRequest = DescribeStacksRequest.builder()
                .stackName(stackName)
                .build();

        DescribeStacksResponse describeStacksResponse = cfClient.describeStacks(describeStacksRequest);
        List<Stack> stacks = describeStacksResponse.stacks();

        if (stacks.isEmpty()) {
            throw new RuntimeException("Stack not found: " + stackName);
        }

        Stack stack = stacks.get(0);
        Map<String, String> outputs = new HashMap<>();
        for (Output output : stack.outputs()) {
            outputs.put(output.outputKey(), output.outputValue());
        }

        return outputs;
    }
}

