// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kendra;

// snippet-start:[kendra.java2.delete.index.main]
// snippet-start:[kendra.java2.delete.index.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.KendraException;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
// snippet-end:[kendra.java2.delete.index.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteIndex {

    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <indexId>\s

                Where:
                    indexId - The id value of the index.
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String indexId = args[0];
        KendraClient kendra = KendraClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deleteSpecificIndex(kendra, indexId);
    }

    public static void deleteSpecificIndex(KendraClient kendra, String indexId) {
        try {
            DeleteIndexRequest deleteIndexRequest = DeleteIndexRequest.builder()
                    .id(indexId)
                    .build();

            kendra.deleteIndex(deleteIndexRequest);
            System.out.println(indexId + " was successfully deleted.");

        } catch (KendraException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[kendra.java2.delete.index.main]
