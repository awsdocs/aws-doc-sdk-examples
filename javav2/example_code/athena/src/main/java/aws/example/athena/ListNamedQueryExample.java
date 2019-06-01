//snippet-sourcedescription:[ListNamedQueryExample.java demonstrates how to obtain a list of named query IDs.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
//snippet-start:[athena.java2.ListNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.ListNamedQueryExample.import]
import software.amazon.awssdk.services.athena.AthenaClient;
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
    //snippet-start:[athena.java2.ListNamedQueryExample.main]
    public static void main(String[] args) throws Exception {
        // Build an Athena client
        AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClient();

        // Build the request
        ListNamedQueriesRequest listNamedQueriesRequest = ListNamedQueriesRequest.builder().build();

        // Get the list results.
        ListNamedQueriesIterable listNamedQueriesResponses = athenaClient.listNamedQueriesPaginator(listNamedQueriesRequest);


        // Process the results.
        for (ListNamedQueriesResponse listNamedQueriesResponse : listNamedQueriesResponses) {
            List<String> namedQueryIds = listNamedQueriesResponse.namedQueryIds();
            // process named query IDs

            System.out.println(namedQueryIds);
        }


    }
    //snippet-end:[athena.java2.ListNamedQueryExample.main]
}

//snippet-end:[athena.java2.ListNamedQueryExample.complete]