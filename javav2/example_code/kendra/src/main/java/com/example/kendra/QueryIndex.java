//snippet-sourcedescription:[QueryIndex.java demonstrates how to query an Amazon Kendra index.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kendra]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/10/2022]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.kendra;

// snippet-start:[kendra.java2.query.index.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.QueryRequest;
import software.amazon.awssdk.services.kendra.model.QueryResultType;
import software.amazon.awssdk.services.kendra.model.QueryResponse;
import software.amazon.awssdk.services.kendra.model.QueryResultItem;
import software.amazon.awssdk.services.kendra.model.KendraException;
import java.util.List;
// snippet-end:[kendra.java2.query.index.import]

/**
 * To run this Java V2 code example, ensure that you have set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */
public class QueryIndex {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <indexId> <text> \n\n" +
                "Where:\n" +
                "    indexId - The Id value of the index.\n" +
                "    text - The text to use.\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String indexId = args[0];
        String text = args[1];
        KendraClient kendra = KendraClient.builder()
                .region(Region.US_EAST_1)
                .build();
        querySpecificIndex(kendra, indexId, text);
    }

    // snippet-start:[kendra.java2.query.index.main]
    public static void querySpecificIndex(KendraClient kendra, String indexId, String text) {

        try {
            QueryRequest queryRequest = QueryRequest.builder()
                .indexId(indexId)
                .queryResultTypeFilter(QueryResultType.DOCUMENT)
                .queryText("Spring MVC")
                .build();


            QueryResponse response = kendra.query(queryRequest);
            List<QueryResultItem> items = response.resultItems();
            for (QueryResultItem item: items) {
                System.out.println("The document title is "+item.documentTitle());
                System.out.println("Text:");
                System.out.println(item.documentExcerpt().text());
            }

            String id = response.responseMetadata().requestId();
            System.out.println("The request Id is "+id);

        } catch (KendraException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kendra.java2.query.index.main]
}
