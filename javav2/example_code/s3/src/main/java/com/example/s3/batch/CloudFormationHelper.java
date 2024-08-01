// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.batch;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationAsyncWaiter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CloudFormationHelper {

    private static CloudFormationAsyncClient getCloudFormationClient() {
        CloudFormationAsyncClient cfClient = CloudFormationAsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        return cfClient;
    }

    public static void deployCloudFormationStack(String stackName) {
        String templateBody;
        boolean doesExist = describeStack(stackName);
        if (!doesExist) {
            try {
                Path filePath = Paths.get("template.yaml").toAbsolutePath();
                templateBody = Files.readString(filePath);
            } catch (IOException e) {
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
        }
     }

     // Check to see if the Stack exists before deploying it

    public static Boolean describeStack(String stackName) {
        try {
            CompletableFuture<?> future = getCloudFormationClient().describeStacks();
            DescribeStacksResponse stacksResponse = (DescribeStacksResponse) future.join();
            List<Stack> stacks = stacksResponse.stacks();
            for (Stack myStack :stacks) {
                if (myStack.stackName().compareTo(stackName)==0){
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

