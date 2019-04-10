//snippet-sourcedescription:[ListNamedQueryExample.java demonstrates how to obtain a list of named query IDs.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-25]
//snippet-sourceauthor:[soo-aws]
package aws.example.athena;

import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.ListNamedQueriesRequest;
import software.amazon.awssdk.services.athena.model.ListNamedQueriesResponse;

import java.util.List;

/**
 * ListNamedQueryExample
 * -------------------------------------
 * This code shows how to obtain a list of named query IDs.
 */
public class ListNamedQueryExample {
    public static void main(String[] args) throws Exception {
        // Build an Athena client
        AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClient();

        // Build the request
        ListNamedQueriesRequest listNamedQueriesRequest = ListNamedQueriesRequest.builder().build();

        // Get the list results.
        ListNamedQueriesResponse listNamedQueriesResponse = athenaClient.listNamedQueries(listNamedQueriesRequest);

        // Process the results.
        boolean hasMoreResults = true;

        while (hasMoreResults) {
            List<String> namedQueryIds = listNamedQueriesResponse.namedQueryIds();
            // process named query IDs

            // If nextToken is not null,  there are more results. Get the next page of results.
            if (listNamedQueriesResponse.getNextToken() != null) {
                listNamedQueriesResponse = athenaClient.listNamedQueries(
                        listNamedQueriesRequest.withNextToken(listNamedQueriesResponse.getNextToken()));
            } else {
                hasMoreResults = false;
            }
        }
    }
}
