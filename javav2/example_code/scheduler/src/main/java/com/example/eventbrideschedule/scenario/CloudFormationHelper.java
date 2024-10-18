// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.eventbrideschedule.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStackEventsRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStackEventsResponse;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
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

public class CloudFormationHelper {
    private static final String CFN_TEMPLATE = "cfn_template.yaml";
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

    public static void deployCloudFormationStack(String stackName, String email) {
        String templateBody;
        // Read the CloudFormation template file
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Path filePath = Paths.get(classLoader.getResource(CFN_TEMPLATE).toURI());
            templateBody = Files.readString(filePath);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to read CloudFormation template file", e);
        }

        CreateStackRequest stackRequest = CreateStackRequest.builder()
            .stackName(stackName)
            .templateBody(templateBody)
            .parameters(Parameter.builder()
                .parameterKey("email")
                .parameterValue(email)
                .build())
            .capabilities(Capability.CAPABILITY_IAM, Capability.CAPABILITY_NAMED_IAM)
            .build();

        getCloudFormationClient().createStack(stackRequest)
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

    /**
     * Fetches and logs the details of the stack failure by describing stack events.
     */
    private static void fetchStackFailureDetails(CloudFormationClient client, String stackName) {
        DescribeStackEventsRequest eventsRequest = DescribeStackEventsRequest.builder()
            .stackName(stackName)
            .build();

        DescribeStackEventsResponse eventsResponse = client.describeStackEvents(eventsRequest);

        // Log the relevant events, focusing on failures
        eventsResponse.stackEvents().forEach(event -> {
            if ("ROLLBACK_COMPLETE".equals(event.resourceStatusAsString()) || "ROLLBACK_IN_PROGRESS".equals(event.resourceStatusAsString())) {
                logger.error("Stack rollback event: Resource {} - Status {} - Reason {}",
                    event.resourceType(), event.resourceStatusAsString(), event.resourceStatusReason());
            }
        });
    }

    /**
     * Fetches and logs the details of the stack failure by describing stack events.
     */
    private static void fetchStackFailureDetails(CloudFormationAsyncClient client, String stackName) {
        DescribeStackEventsRequest eventsRequest = DescribeStackEventsRequest.builder()
            .stackName(stackName)
            .build();

        client.describeStackEvents(eventsRequest).whenComplete((eventsResponse, th) -> {
            if (th != null) {
                logger.error("Failed to retrieve stack events: {}", th.getMessage(), th);
            } else {
                // Log the relevant events, focusing on failures
                eventsResponse.stackEvents().forEach(event -> {
                    if ("ROLLBACK_COMPLETE".equals(event.resourceStatusAsString()) || "ROLLBACK_IN_PROGRESS".equals(event.resourceStatusAsString())) {
                        logger.error("Stack rollback event: Resource {} - Status {} - Reason {}",
                            event.resourceType(), event.resourceStatusAsString(), event.resourceStatusReason());
                    }
                });
            }
        }).join(); // Ensures we wait for the events to be fetched
    }


    // Check to see if the Stack exists before deploying it
    public static Boolean describeStack(String stackName) {
        CloudFormationClient client = CloudFormationClient.builder()
            .region(Region.US_EAST_1)
            .build();
        try {
            // Create a DescribeStacksRequest object
            DescribeStacksRequest describeStacksRequest = DescribeStacksRequest.builder()
                .stackName(stackName)
                .build();

            // Call the describeStacks method synchronously
            DescribeStacksResponse stacksResponse = client.describeStacks(describeStacksRequest);

            // Process the stacks and check if the stack exists
            List<Stack> stacks = stacksResponse.stacks();
            for (Stack myStack : stacks) {
                if (myStack.stackName().equals(stackName)) {
                    return true;
                }
            }
        } catch (CloudFormationException e) {
            System.err.println("Error describing CloudFormation stacks: " + e.getMessage());
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
       DescribeStacksRequest describeStacksRequest = DescribeStacksRequest.builder()
            .stackName(stackName)
            .build();

        try {
            DescribeStacksResponse describeStacksResponse = getCloudFormationClient().describeStacks(describeStacksRequest).join();
            if (describeStacksResponse.stacks().isEmpty()) {
                throw new RuntimeException("Stack not found: " + stackName);
            }

            Stack stack = describeStacksResponse.stacks().get(0);
            Map<String, String> outputs = new HashMap<>();
            for (Output output : stack.outputs()) {
                outputs.put(output.outputKey(), output.outputValue());
            }

            return outputs;
        } catch (SdkServiceException e) {
            throw new RuntimeException("Failed to get stack outputs for: " + stackName, e);
        }
    }
}