//snippet-sourcedescription:[ListNamedQueryExample.java demonstrates how to obtain a list of named query IDs.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[athena.java2.ListNamedQueryExample.complete]
//snippet-start:[athena.java.ListNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.ListNamedQueryExample.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.ListNamedQueriesRequest;
import software.amazon.awssdk.services.athena.model.ListNamedQueriesResponse;
import software.amazon.awssdk.services.athena.paginators.ListNamedQueriesIterable;
//snippet-end:[athena.java2.ListNamedQueryExample.import]

import java.util.List;

/**
 * ListNamedQueryExample
 * -------------------------------------
 * This code shows how to obtain a list of named query IDs.
 */
public class ListNamedQueryExample {

    public static void main(String[] args) throws Exception {

        AthenaClient athenaClient = AthenaClient.builder()
                .region(Region.US_WEST_2)
                .build();

        listNamedQueries(athenaClient) ;
        athenaClient.close();
    }

    //snippet-start:[athena.java2.ListNamedQueryExample.main]
    public static void listNamedQueries(AthenaClient athenaClient) {

       try{
            ListNamedQueriesRequest listNamedQueriesRequest = ListNamedQueriesRequest.builder()
                    .build();

            ListNamedQueriesIterable listNamedQueriesResponses = athenaClient.listNamedQueriesPaginator(listNamedQueriesRequest);

            // Process the results.
            for (ListNamedQueriesResponse listNamedQueriesResponse : listNamedQueriesResponses) {
                List<String> namedQueryIds = listNamedQueriesResponse.namedQueryIds();
                System.out.println(namedQueryIds);
            }

       } catch (AthenaException e) {
           e.printStackTrace();
           System.exit(1);
       }
    }
    //snippet-end:[athena.java2.ListNamedQueryExample.main]
}
//snippet-end:[athena.java.ListNamedQueryExample.complete]
//snippet-end:[athena.java2.ListNamedQueryExample.complete]