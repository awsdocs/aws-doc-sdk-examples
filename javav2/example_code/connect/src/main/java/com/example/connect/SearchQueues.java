// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.connect;

// snippet-start:[connect.java2.search.queue.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.Queue;
import software.amazon.awssdk.services.connect.model.SearchQueuesRequest;
import software.amazon.awssdk.services.connect.model.SearchQueuesResponse;
import java.util.List;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class SearchQueues {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <instanceId>

                Where:
                   instanceId - The id of the Amazon Connect instance.
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
                .region(region)
                .build();

        searchQueue(connectClient, instanceId);
    }

    public static void searchQueue(ConnectClient connectClient, String instanceId) {
        try {
            SearchQueuesRequest queuesRequest = SearchQueuesRequest.builder()
                    .instanceId(instanceId)
                    .maxResults(10)
                    .build();

            SearchQueuesResponse response = connectClient.searchQueues(queuesRequest);
            List<Queue> queuesList = response.queues();
            for (Queue queue : queuesList) {
                System.out.println("The queue name is " + queue.name());
                System.out.println("The queue description is " + queue.description());
                System.out.println("The queue id is " + queue.queueId());
                System.out.println("The queue ARN is " + queue.queueArn());
            }

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[connect.java2.search.queue.main]
