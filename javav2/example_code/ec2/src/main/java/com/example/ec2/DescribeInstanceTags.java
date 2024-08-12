// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.describe_tags.main]
// snippet-start:[ec2.java2.describe_tags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.DescribeTagsResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.DescribeTagsRequest;
import software.amazon.awssdk.services.ec2.paginators.DescribeTagsIterable;

import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.describe_tags.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeInstanceTags {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                   <resourceId>\s

                Where:
                   resourceId - The instance ID value that you can obtain from the AWS Management Console (for example, i-xxxxxx0913e05f482).\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            return;
        }

        String resourceId = args[0];
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            CompletableFuture<Void> future = describeEC2TagsAsync(ec2AsyncClient, resourceId);
            future.join(); // Wait for the async process to complete.
            System.out.println("EC2 Tags described successfully.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
    }

    public static CompletableFuture<Void> describeEC2TagsAsync(Ec2AsyncClient ec2AsyncClient, String resourceId) {
        Filter filter = Filter.builder()
            .name("resource-id")
            .values(resourceId)
            .build();

        CompletableFuture<DescribeTagsResponse> response = ec2AsyncClient.describeTags(
            DescribeTagsRequest.builder().filters(filter).build());
        response.whenComplete((tagsResponse, ex) -> {
            if (tagsResponse != null) {
                tagsResponse.tags().forEach(tag ->
                    System.out.println("Tag key is: " + tag.key() + " Tag value is: " + tag.value()));
            } else {
                throw new RuntimeException("Failed to describe EC2 tags.", ex);
            }
        });

       return response.thenApply(resp -> null);
    }
}
// snippet-end:[ec2.java2.describe_tags.main]
