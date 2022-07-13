//snippet-sourcedescription:[DeleteIndex.java demonstrates how to delete an Amazon Kendra index.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kendra]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra;

// snippet-start:[kendra.java2.delete.index.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.KendraException;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
// snippet-end:[kendra.java2.delete.index.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteIndex {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <indexId> \n\n" +
                "Where:\n" +
                "    indexId - The id value of the index.\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String indexId = args[0];
        KendraClient kendra = KendraClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        deleteSpecificIndex(kendra, indexId);
    }

    // snippet-start:[kendra.java2.delete.index.main]
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
    // snippet-end:[kendra.java2.delete.index.main]
}
